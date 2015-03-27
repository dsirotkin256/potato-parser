package app;

import app.ui.MainFrame;
import app.ui.UI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.swing.JOptionPane;

/*
 *
 * This class represents the loading method and it's behaviour
 *
 */
public class LoadingProcess implements Magical {

    LoadingDialog loading;
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

    public LoadingProcess(Root root) {
        this.root = root;
        times = new ArrayList<>();
        MainFrame.getInstance().setVisible(true);
        loading = new LoadingDialog();
        this.pw = new ProgressWorker(this);
    }

    @Override
    public void doMagic() {

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
            } catch (IOException | NoSuchElementException ex) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        String.format("Ответы на вопросы %s не найдены", doc.getName()),
                        "Внимание!",
                        JOptionPane.WARNING_MESSAGE);
            }

            current += 2;
            int stack = total - current;

            percentage = (short) ((current * 100) / total);

            if (UI.getDontDisturbMode() == Mode.OFF) {
                UI.render.update(root.getQuestions());
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

            loading.label.setText(String.format(
                    "Времени осталось до загрузки: %s", text));

            loading.setTitle(String.format("Файлов: %d/%d", current, total));

        });
        double lt = loadingTime.elapsedTime();
        App.logger.info(String.format("Documents: %d Time: %02d min %02d sec", root.size() * 2, (int) ((lt % 3600) / 60), (int) (lt % 60)));

        // TO DO add to log file loading time (issue#)
        if (root.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), "Данная папка не содержит документы НАКС",
                    "Внимание!", JOptionPane.ERROR_MESSAGE);
            Runtime.getRuntime().exit(1);

        }
        loading.dispose();

    }

    public LoadingDialog getLoading() {
        return loading;
    }

}
