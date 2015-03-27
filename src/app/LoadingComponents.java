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
public class LoadingComponents implements Magical {

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
    public void doMagic() {

        System.out.println("Calling loader...");

        loading.button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loading.dispose();
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

        StopWatch loadingTime = new StopWatch();

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

            new Thread(() -> root.updateQuestionList()).start();

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
        double lt = loadingTime.elapsedTime();
        App.logger.info(String.format("Documents: %d Time: %02d min %02d sec", root.size() * 2, (int) ((lt % 3600) / 60), (int) (lt % 60)));

        // TO DO add to log file loading time (issue#)
        if (root.isEmpty()) {
            JOptionPane.showMessageDialog(App.ui, "Данная папка не содержит документы НАКС",
                    "Внимание!", JOptionPane.ERROR_MESSAGE);
            Runtime.getRuntime().exit(1);

        }
        loading.dispose();

    }

}
