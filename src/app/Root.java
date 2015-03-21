package app;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class Root extends TreeSet<QuestionDocument> {

    static {

        Locale russian = new Locale("ru");
        Locale.setDefault(russian);

        ui = new UI();
        root = new Root();

    }

    static File directory;

    static JTextField searchBox;

    static JEditorPane answerPane, questionPane;
    static TableRender render;

    private static int current = 0;
    private static Double average = 0.00D;

    private static Root root;
    private static UI ui;

    private static boolean dontDisturbMode = false;

    /*
     * Prompt user to choose directory with Documents
     */
    private static Collection<File> getFiles() {

        JFileChooser dir = new JFileChooser(System.getProperty("user.home") + "\\Desktop");

        dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dir.setApproveButtonText("Выбрать папку");
        dir.setDialogTitle("Выберите папку с документами НАКС");

        int result = dir.showOpenDialog(ui);

        if (result == JFileChooser.APPROVE_OPTION) {

            directory = dir.getSelectedFile();

            return FileUtils.listFiles(directory,
                    new RegexFileFilter(".+(?<!_о).doc"),
                    DirectoryFileFilter.DIRECTORY);

        } else {

            // IF operation was canceled
            Runtime.getRuntime().exit(1);

        }

        return null;
    }

    public static void main(String[] args) {

        ui.setLocationRelativeTo(null);

        answerPane = ui.answerPane;
        questionPane = ui.questionPane;
        searchBox = ui.searchBox;

        searchBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arg0) {

                if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (searchBox.getText().isEmpty()) {
                        // Fullfill table with questions from root derectory
                        render = new TableRender(ui.table, extractQuestions(root));
                        ui.lblSearch.setText("");
                        dontDisturbMode = false;
                    } else {
                        //Interrupt table updates by switching on this mode
                        dontDisturbMode = true;
                        String creteria = searchBox.getText();

                        // Start Search
                        new ConcurrentProcessing("Выполняется поиск",
                                new SearchingQuestion(creteria)).start();

                    }

                }
            }
        });

        Collection<File> files = getFiles();

        if (files.isEmpty()) {

            JOptionPane.showMessageDialog(ui,
                    "Указанная папка не содержит документы НАКС",
                    "Внимание!",
                    JOptionPane.ERROR_MESSAGE);

            Runtime.getRuntime().exit(1);

        }

        new ConcurrentProcessing("Загрузка компонентов", new LoadingComponents(files)).start();

        ui.setVisible(true);

    }

    /*
     * This is overriding of 'add' method.
     *
     * When question document is adding this function
     * is looking for the answer document in selected directory
     * and set detected answer document to the question that preparing to add into collection
     *
     */
    @Override
    public boolean add(QuestionDocument doc) {

        try {

            String answerFileName = doc.getName().replace(".doc", "") + ("_о") + (".doc");

            LinkedList<File> answer = new LinkedList<File>(FileUtils.listFiles(directory,
                    new RegexFileFilter(answerFileName),
                    DirectoryFileFilter.DIRECTORY));

            String answerPath = answer.getFirst().getPath();

            doc.setAnswerDocument(answerPath);

            doc.extractQuestions();

            return super.add(doc); //To change body of generated methods, choose Tools | Templates.
        } catch (IOException | NoSuchElementException ex) {

            JOptionPane.showMessageDialog(ui,
                    String.format("Ответы на вопросы %s не найдены", doc.getName()),
                    "Внимание!",
                    JOptionPane.WARNING_MESSAGE);

            return false;
        }

    }

    /*
     * Extracts unique questions from root array
     */
    static LinkedList<Question> extractQuestions(Root root) {

        TreeSet<Question> questions = new TreeSet<>();

        root.forEach(questionDocument -> {

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
    static boolean isValid(Question question, String creteria) {

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
    private static class LoadingComponents extends Thread {

        Collection<File> files;
        Loading loading;
        ArrayList<Double> times = new ArrayList<>();
        static volatile boolean isCanceled = false;

        public LoadingComponents(Collection<File> files) {
            this.files = files;
        }

        @Override
        public void interrupt() {
            isCanceled = true;
            super.interrupt(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void run() {

            System.out.println("Calling loader...");

            loading = new Loading();

            loading.button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    loading.dispose();
                    interrupt();
                    //Runtime.getRuntime().exit(1);
                }
            });

            loading.setVisible(true);
            loading.setLocationRelativeTo(ui);

            // total questions to load
            final int total = files.size();
            loading.progressBar.setMaximum(total);

            StopWatch logTime = new StopWatch();
            files.forEach(file -> {

                // USER cancels
                if (isCanceled) {
                    return;
                }

                Runnable run = () -> {
                    try {
                        root.add(new QuestionDocument(file.getPath()));
                    } catch (IOException ex) {

                    }
                };

                Thread t = new Thread(run);
                StopWatch stopwatch = new StopWatch();
                t.start();

                current += 1;
                int stack = total - current;

                loading.progressBar.setStringPainted(true);
                loading.progressBar.setValue((current * 100) / total);

                if (!dontDisturbMode) {
                    synchronized (ui.table) {
                        ui.table.notifyAll();
                        render = new TableRender(ui.table, extractQuestions(root));
                    }
                }

                try {
                    t.join();
                } catch (InterruptedException ex) {

                }

                times.add(stopwatch.elapsedTime());
                times.forEach(time -> average += time);
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

                loading.setTitle(String.format("Выполнено:  %d из %d",
                        (current * 2), (total * 2)));

            });

            // TO DO add to log file
            double logTimeEnd = logTime.elapsedTime();

            if (root.isEmpty()) {
                JOptionPane.showMessageDialog(ui, "Данная папка не содержит документы НАКС",
                        "Внимание!", JOptionPane.ERROR_MESSAGE);
                Runtime.getRuntime().exit(1);

            }
            loading.dispose();
        }

    }

    /*
     * This class represents the searching method and it's behaviour
     */
    private static class SearchingQuestion extends Thread {

        String creteria;

        public SearchingQuestion(String creteria) {
            this.creteria = creteria;
        }

        @Override
        public void run() {

            System.out.println("Performing search...");

            StopWatch stopWatch = new StopWatch();

            LinkedList<Question> results = new LinkedList<>(
                    extractQuestions(root).stream()
                    .filter(question -> isValid(question, creteria))
                    .collect(Collectors.toList()));

            double time = stopWatch.elapsedTime();

            int resultsCount = results.size();

            // LOCK for UI representation changes
            synchronized (ui.table) {
                // Fullfill table with founded questions
                dontDisturbMode = true;
                ui.table.notifyAll();
                render = new TableRender(ui.table, results);
            }
            synchronized (ui.lblSearch) {

                String response = (results.isEmpty())
                        ? "По вашему запросу ничего не найдено"
                        : "Найдено: " + resultsCount + " "
                        + getCorrectStrEnding(resultsCount,
                                "результат") + "  ( " + time % 60 + " сек. ) ";

                ui.lblSearch.notifyAll();
                ui.lblSearch.setText(response);

            }

        }

    }

    /*
     *  This class runs threads passed as a parameter and controls UI
     */
    private static class ConcurrentProcessing extends Thread {

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

                for (int i = 0; i <= 3 && !dontDisturbMode; i++) {

                    synchronized (ui.lblSearch) {
                        ui.lblSearch.notifyAll();
                        if (i == 0 && !isInterrupted) {
                            ui.lblSearch.setText(message);
                        }
                        if (i == 1 && !isInterrupted) {
                            ui.lblSearch.setText(message + ".");
                        }
                        if (i == 2 && !isInterrupted) {
                            ui.lblSearch.setText(message + ". .");
                        }
                        if (i == 3 && !isInterrupted) {
                            ui.lblSearch.setText(message + ". . .");
                        }

                    }
                    try {
                        this.sleep(500);
                    } catch (InterruptedException ex) {

                    }

                }
            }
            synchronized (ui.lblSearch) {
                try {
                    ui.lblSearch.wait(5000);
                    ui.lblSearch.setText("");
                } catch (InterruptedException ex) {

                }
            }
        }
    }

    /*
     * Notice: valid only for words ending with Russian consonant letter
     */
    static String getCorrectStrEnding(int num, String ofEntity) {

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
