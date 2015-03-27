package app;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

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

    public LoadingComponents(Root root) {
        this.root = root;
        times = new ArrayList<>();
        App.ui.setVisible(true);
        loading = new Loading();
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
            } catch (IOException ex) {
            }

            current += 2;
            int stack = total - current;

            percentage = (short) ((current * 100) / total);

            if (UI.getDontDisturbMode() == Mode.OFF) {
                App.render.update(root.getQuestions());
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

    public Loading getLoading() {
        return loading;
    }

}
