package app;

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

    public void setPreview() {

        final String questionHTML = "<!DOCTYPE html><html><body><font size=\"4\" face=\"verdana\">" + getQuestion() + "</font><br><br><br><font size=\"3\" face=\"verdana\">Doc: <i>" + getDocName() + "</i><br>Question <i>#" + getQuestionNumber() + "</i></font></body></html>";

        final String answerHTML = "<!DOCTYPE html><html><body><font size=\"4\" face=\"verdana\">" + getAnswer() + "</font><body></html>";

        App.ui.questionPane.setText(questionHTML);
        App.ui.answerPane.setText(answerHTML);

        App.ui.questionPane.setCaretPosition(0);
        App.ui.answerPane.setCaretPosition(0);
    }

    @Override
    public int compareTo(Question question) {

        return getQuestion().compareTo(question.getQuestion());

    }

}
