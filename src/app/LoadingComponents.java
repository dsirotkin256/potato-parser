package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/*
 *
 * This class represents the loading method and it's behaviour
 *
 */
public class LoadingComponents extends Thread {

    Loading loading;
    ArrayList<Double> times;
    StopWatch time;
    double average = 0.00D;
    double approximateTime;
    short percentage = 0;
    volatile boolean isCanceled = false;
    ProgressWorker pw;
    Thread addQuestion;
    Root root;
    private static int current = 0;

    class ProgressWorker extends SwingWorker<Object, Object> {

        @Override
        protected Object doInBackground() throws Exception {
            while (percentage <= 100) {

                setProgress(percentage);
                sleep((long) average * 1000);
            }
            return null;
        }

    }

    public LoadingComponents(Root root) {
        this.root = root;
        times = new ArrayList<>();
        App.ui.setVisible(true);
        loading = new Loading();
        this.pw = new ProgressWorker();
    }

    @Override
    public void interrupt() {
        isCanceled = true;
        super.interrupt();
    }

    @Override
    public void run() {

        System.out.println("Calling loader...");

        loading.button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loading.dispose();
                interrupt();
            }
        });

        pw.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals("progress")) {
                    int progress = (int) evt.getNewValue();
                    loading.progressBar.setValue(progress);
                    loading.progressBar.repaint();
                }
            }

        });

        pw.execute();

        final int total = root.getDocuments().size() * 2;

        synchronized (root.getDocuments()) {
            root.getDocuments().forEach(file -> {

                // USER cancels loading process by clicking button
                if (isCanceled) {
                    return;
                }

                String path = file.getPath();

                QuestionDocument doc = null;

                time = new StopWatch();

                try {
                    doc = new QuestionDocument(path);
                    root.loadDocument(doc);
                } catch (IOException ex) {
                }

                current += 2;
                int stack = total - current;

                percentage = (short) ((current * 100) / total);

                if (UI.getDontDisturbMode() == Mode.OFF) {
                    synchronized (App.ui.table) {
                        System.out.println(
                                String.format("File %s was loaded dynamicly",
                                        file.getName()));
                        App.ui.table.notifyAll();
                        App.render.update(root.getQuestions());
                    }
                }

                root.updateQuestionList();

                times.add(time.elapsedTime());

                times.forEach(t -> average += t);
                average /= times.size();
                approximateTime = average * stack;

                String text;

                if (approximateTime >= 60) {

                    //Get representaion of approximate time in minutes and seconds
                    text = String.format("%02d мин. %02d сек.",
                            (int) ((approximateTime % 3600) / 60), (int) (approximateTime % 60));

                } else {

                    text = (int) approximateTime + " сек.";

                }

                loading.label.setText(
                        String.format("До полной загрузки осталось: %s", text));

                loading.setTitle(String.format("Файлов загружено:  %d из %d",
                        current, total));

            });

            // TO DO add to log file loading time (issue#)
            if (root.isEmpty()) {
                JOptionPane.showMessageDialog(App.ui, "Данная папка не содержит документы НАКС",
                        "Внимание!", JOptionPane.ERROR_MESSAGE);
                Runtime.getRuntime().exit(1);

            }
            loading.dispose();
        }
    }

}
