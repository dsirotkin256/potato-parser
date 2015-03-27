package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

public class Root extends TreeSet<QuestionDocument> {

    public static class NoDocumentsFoundException extends Exception {
    }

    private File directory;
    private ArrayList<File> documents;
    private TreeSet<Question> questions;

    public Root(File directory) throws NoDocumentsFoundException {
        this.questions = new TreeSet<>();
        this.directory = directory;
        this.documents = new ArrayList<>(
                FileUtils.listFiles(directory,
                        new RegexFileFilter(".+(?<!_о).doc"),
                        DirectoryFileFilter.DIRECTORY));

        if (documents.isEmpty()) {
            throw new NoDocumentsFoundException();
        }
    }

    @Override
    public boolean add(QuestionDocument d) {
        return super.add(d);
    }

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
    public void loadDocument(QuestionDocument doc) throws IOException, NoSuchElementException {

        String answerFileName = doc.getName().replaceFirst("(?=.doc)", "_о");

        LinkedList<File> answer = new LinkedList<>(
                FileUtils.listFiles(directory,
                        new RegexFileFilter(answerFileName),
                        DirectoryFileFilter.DIRECTORY));

        String answerPath = answer.getFirst().getPath();
        doc.setAnswerDocument(answerPath);
        doc.extractQuestions();

        add(doc);
    }

    /*
     *
     * add new arrived questions from root
     *
     *
     */
    public void updateQuestionList() {
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
    public LinkedList<Question> extractLoadedQuestions() {

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
    public boolean isValid(Question question, String creteria) {
        String regex = String.format("(?i).*{0}.*", creteria);
        Pattern p = Pattern.compile(regex, Pattern.UNICODE_CASE);
        Matcher m = p.matcher(question.getQuestion());

        return m.matches();
    }

    /*
     * Notice: valid only for words ending with Russian consonant letter
     */
    public String getCorrectStrEnding(int num, String ofEntity) {

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
