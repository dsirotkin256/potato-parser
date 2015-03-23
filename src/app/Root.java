package app;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class Root extends TreeSet<QuestionDocument> {

    static {
        Locale russian = new Locale("ru");
        Locale.setDefault(russian);
    }

    public Root(File directory) {
        questions = new TreeSet<>();
        setFiles(directory);
        App.ui.searchBox.addKeyListener(new SearchBoxKeyAdapter());
    }

    private File directory;
    private ArrayList<File> documents;
    private volatile TreeSet<Question> questions;

    private static int current = 0;

    private volatile static mode dontDisturbMode = mode.OFF;

    /*
     * Prompt user to choose directory with Documents
     */
    private void setFiles(File directory) {
        this.directory = directory;

        try {
            documents = new ArrayList<>(FileUtils.listFiles(directory,
                    new RegexFileFilter(".+(?<!_о).doc"),
                    DirectoryFileFilter.DIRECTORY));

            long seed = System.nanoTime();
            Collections.shuffle(documents, new Random(seed));

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(App.ui,
                    "Данной папки не существует.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);

            Runtime.getRuntime().exit(1);

        }
        if (documents.isEmpty()) {

            JOptionPane.showMessageDialog(App.ui,
                    "Указанная папка не содержит документы НАКС",
                    "Внимание!",
                    JOptionPane.ERROR_MESSAGE);

            Runtime.getRuntime().exit(1);

        }

        new ConcurrentProcessing("Загрузка компонентов",
                new LoadingComponents(documents)).start();

    }

    enum mode {

        ON, OFF

    }

    private class SearchBoxKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent arg0) {

            boolean isAllowed
                    = (arg0.getKeyCode() == KeyEvent.VK_ENTER && (arg0.getWhen() / 1000) >= 2);

            if (isAllowed) {

                // LOCK for UI interruption changes
                //Interrupt table updates by switching on this mode
                dontDisturbMode = mode.ON;

                if (App.ui.searchBox.getText().isEmpty()) {
                    // Fullfill table with questions from root derectory
                    App.render.update(questions);
                    App.ui.lblSearch.setText("");
                    dontDisturbMode = mode.OFF;

                } else {

                    String creteria = App.ui.searchBox.getText();

                    // Start Search
                    new ConcurrentProcessing("Выполняется поиск",
                            new SearchingQuestion(creteria)).start();

                }

            }
        }
    };

    /*
     * This is overriding of 'add' method.
     *
     * When question document was loaded successfuly, this function
     * is looking for the answer document in selected directory
     * and set detected answer document to the question that preparing to add into collection
     *
     */
    void loadDocument(QuestionDocument doc) {

        boolean isAdded = add(doc);

        if (isAdded) {
            try {
                String answerFileName = doc.getName().replace(".doc", "") + ("_о") + (".doc");

                LinkedList<File> answer = new LinkedList<File>(FileUtils.listFiles(directory,
                        new RegexFileFilter(answerFileName),
                        DirectoryFileFilter.DIRECTORY));

                String answerPath = answer.getFirst().getPath();

                doc.setAnswerDocument(answerPath);

                doc.extractQuestions();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(App.ui,
                        String.format("Ответы на вопросы %s не найдены", doc.getName()),
                        "Внимание!",
                        JOptionPane.WARNING_MESSAGE);
            }

        }

    }

    /*
     *
     * add new arrived questions from root
     *
     *
     */
    void updateQuestionList() {
        forEach(questionDocument -> {
            questionDocument.getQuestions().forEach(question -> {
                questions.add(question);
            });
        });
    }

    /*
     *
     * Extracts unique questions from root folder
     *
     */
    LinkedList<Question> extractLoadedQuestions() {

        TreeSet<Question> questions = new TreeSet<>();

        forEach(questionDocument -> {

            questionDocument.getQuestions().forEach(question -> {

                questions.add(question);

            });

        });

        return new LinkedList<>(questions);
    }

    /*
     *
     * This function checks whether question satisfy the condition of search or not
     *
     */
    boolean isValid(Question question, String creteria) {

        String regex = "(?i).*" + creteria + ".*";

        Pattern p = Pattern.compile(regex, Pattern.UNICODE_CASE);

        Matcher m = p.matcher(question.getQuestion());

        return m.matches();

    }


    /*
     *
     * This class represents the loading method and it's behaviour
     *
     */
    private class LoadingComponents extends Thread {

        ArrayList<File> files;
        Loading loading;
        ArrayList<Double> times;
        StopWatch time;
        double average = 0.00D;
        short percentage = 0;
        volatile boolean isCanceled = false;
        ProgressWorker pw;
        Thread addQuestion;

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

        public LoadingComponents(ArrayList<File> files) {
            Root.this.documents = documents;
            this.files = files;
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

            final int total = files.size() * 2;

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

            files.forEach(file -> {

                // USER cancels loading process by clicking button
                if (isCanceled) {
                    return;
                }

                String path = file.getPath();

                Runnable run = () -> {
                    try {
                        loadDocument(new QuestionDocument(path));
                    } catch (IOException ex) {

                    }
                };

                addQuestion = new Thread(run);
                time = new StopWatch();
                addQuestion.setPriority(3);
                addQuestion.start();

                current += 2;
                int stack = total - current;

                percentage = (short) ((current * 100) / total);

                if (dontDisturbMode == mode.OFF) {
                    synchronized (App.ui.table) {
                        System.out.println(
                                String.format("File %s was loaded dynamicly",
                                        file.getName()));
                        App.ui.table.notifyAll();
                        App.render.update(questions);
                    }
                }

                try {
                    addQuestion.join();
                } catch (InterruptedException ex) {

                }

                updateQuestionList();

                times.add(time.elapsedTime());
                times.forEach(t -> average += t);
                average /= times.size();
                double approximateTime = average * stack;

                String text;

                if (approximateTime >= 60) {

                    //Get representaion of approximate time in minutes
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
            if (isEmpty()) {
                JOptionPane.showMessageDialog(App.ui, "Данная папка не содержит документы НАКС",
                        "Внимание!", JOptionPane.ERROR_MESSAGE);
                Runtime.getRuntime().exit(1);

            }
            loading.dispose();
        }

    }

    /*
     * This class represents the searching method and it's behaviour
     */
    private class SearchingQuestion extends Thread {

        String creteria;

        public SearchingQuestion(String creteria) {
            this.creteria = creteria;
        }

        @Override
        public void run() {

            System.out.println("Performing search...");

            StopWatch stopWatch = new StopWatch();

            TreeSet<Question> results = new TreeSet<>(
                    extractLoadedQuestions().stream()
                    .filter(question -> isValid(question, creteria))
                    .collect(Collectors.toList()));

            double time = stopWatch.elapsedTime();

            int resultsCount = results.size();

            synchronized (App.ui.table) {
                // Fullfill table with founded questions
                dontDisturbMode = mode.ON;
                App.ui.table.notifyAll();
                App.render.update(results);
            }
            synchronized (App.ui.lblSearch) {

                String response = (results.isEmpty())
                        ? "По вашему запросу ничего не найдено"
                        : "Найдено: " + resultsCount + " "
                        + getCorrectStrEnding(resultsCount,
                                "результат") + "  (" + time % 60 + " сек.) ";

                App.ui.lblSearch.notify();
                App.ui.lblSearch.setText(response);
                try {
                    App.ui.lblSearch.wait();
                } catch (InterruptedException ex) {

                }

            }

        }

    }

    /*
     *  This class runs threads passed as a parameter and controls UI
     */
    private class ConcurrentProcessing extends Thread {

        volatile boolean isInterrupted = false;
        String message;
        Runnable foo;

        public ConcurrentProcessing(String message, Runnable foo) {
            this.message = message;
            this.foo = foo;

        }

        @Override
        public void interrupt() {
            //Notify 'while loop' about interuption
            isInterrupted = true;
            super.interrupt(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void run() {
            //Start job
            new Thread() {

                @Override
                public void run() {

                    System.out.println("Preparing to call function...");
                    foo.run();
                    System.out.println("Finished");
                    isInterrupted = true;
                    // End Action

                }
            }.start();

            //Start waiting
            while (!isInterrupted) {

                for (int i = 0; i <= 3 && dontDisturbMode == mode.OFF; i++) {

                    synchronized (App.ui.lblSearch) {
                        App.ui.lblSearch.notify();
                        if (i == 0 && !isInterrupted) {
                            App.ui.lblSearch.setText(message);
                        }
                        if (i == 1 && !isInterrupted) {
                            App.ui.lblSearch.setText(message + ".");
                        }
                        if (i == 2 && !isInterrupted) {
                            App.ui.lblSearch.setText(message + ". .");
                        }
                        if (i == 3 && !isInterrupted) {
                            App.ui.lblSearch.setText(message + ". . .");
                        }

                    }
                    try {
                        this.sleep(400);
                    } catch (InterruptedException ex) {

                    }

                }
            }
            synchronized (App.ui.lblSearch) {
                App.ui.lblSearch.setText("");
            }

        }
    }

    /*
     * Notice: valid only for words ending with Russian consonant letter
     */
    String getCorrectStrEnding(int num, String ofEntity) {

        String rcStr = String.valueOf(num);
        int preLastNum;
        int lastNum;

        String grammarStr = ofEntity;

        lastNum = Integer.parseInt(
                String.valueOf(rcStr.charAt(rcStr.length() - 1)));

        if (rcStr.length() > 1) {

            preLastNum = Integer.parseInt(
                    String.valueOf(rcStr.charAt(rcStr.length() - 2)));

            return grammarStr + "ов";
        }

        if (lastNum >= 2 && lastNum <= 4) {
            grammarStr += "а";
        } else if (lastNum > 4) {
            grammarStr += "ов";
        }

        return grammarStr;
    }

}
