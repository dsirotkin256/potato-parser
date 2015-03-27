package app;

import app.ui.MainFrame;
import java.io.IOException;
import templates.HtmlTemplate;

public class Question implements Comparable<Question> {

    private String questionNumber;
    private String docName;
    private String link;
    private String question;
    private String answer;

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocName() {
        return docName;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPreview() throws IOException {

        final String questionTpl = String.format(
                HtmlTemplate.getQuestionTplInst().getContent(),
                getQuestion(),
                getDocName(),
                Integer.parseInt(getQuestionNumber()));

        final String asnwerTpl = String.format(
                HtmlTemplate.getAnswerTplInst().getContent(),
                getAnswer()
        );

        MainFrame.getInstance().questionPane.setText(questionTpl);
        MainFrame.getInstance().answerPane.setText(asnwerTpl);

        MainFrame.getInstance().questionPane.setCaretPosition(0);
        MainFrame.getInstance().answerPane.setCaretPosition(0);
    }

    @Override
    public int compareTo(Question question) {

        return getQuestion().compareTo(question.getQuestion());

    }

}
