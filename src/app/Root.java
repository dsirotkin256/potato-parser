package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class Root extends TreeSet<QuestionDocument> {

    @Override
    public synchronized boolean add(QuestionDocument e) {
        return super.add(e); //To change body of generated methods, choose Tools | Templates.
    }

    public static class NoDocumentsFoundException extends Exception {
    }

    public Root(File directory) throws NoDocumentsFoundException {
        this.questions = new TreeSet<>();
        this.directory = directory;
        this.documents
                = new ArrayList<>(FileUtils.listFiles(directory,
                                new RegexFileFilter(".+(?<!_о).doc"),
                                DirectoryFileFilter.DIRECTORY));
        int seeds = new Random().nextInt();

        Collections.shuffle(documents, new Random(seeds));

        if (documents.isEmpty()) {
            throw new NoDocumentsFoundException();
        }
    }

    private File directory;
    private ArrayList<File> documents;
    private TreeSet<Question> questions;

    public ArrayList<File> getDocuments() {
        return documents;
    }

    public TreeSet<Question> getQuestions() {
        return questions;
    }

    /*
     * This is overriding of 'add' method.
     *
     * When question document was loaded successfuly, this function
     * is looking for the answer document in selected directory
     * and set detected answer document to the question that preparing to add into collection
     *
     */
    void loadDocument(QuestionDocument doc) {

        if (add(doc)) {
            try {
                String answerFileName = doc.getName().replace(".doc", "") + ("_о") + (".doc");

                LinkedList<File> answer
                        = new LinkedList<File>(FileUtils.listFiles(directory,
                                        new RegexFileFilter(answerFileName),
                                        DirectoryFileFilter.DIRECTORY));

                String answerPath = answer.getFirst().getPath();
                doc.setAnswerDocument(answerPath);
                doc.extractQuestions();

            } catch (IOException | NoSuchElementException ex) {
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
    synchronized void updateQuestionList() {
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
    synchronized LinkedList<Question> extractLoadedQuestions() {

        TreeSet<Question> questions = new TreeSet<Question>() {

            @Override
            public boolean add(Question e) {
                return super.add(e);
            }

        };

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

            if (preLastNum == 1) {
                return grammarStr + "ов";
            }

        }

        if (lastNum >= 2 && lastNum <= 4) {
            grammarStr += "а";
        } else if (lastNum > 4) {
            grammarStr += "ов";
        }

        return grammarStr;
    }

}
