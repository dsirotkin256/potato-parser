package templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

public class HtmlTemplate {

    private final String content;

    public HtmlTemplate(String path) throws IOException {
        content = loadTemplate(path);
    }

    public String getContent() {
        return content;
    }

    // static part
    private static HtmlTemplate answerTplInst,
            questionTplInst;

    static {
        try {
            answerTplInst = new HtmlTemplate("answer.html");
            questionTplInst = new HtmlTemplate("question.html");
        } catch (IOException ex) {
            System.exit(1);
        }
    }

    public static HtmlTemplate getAnswerTplInst() {
        return answerTplInst;
    }

    public static HtmlTemplate getQuestionTplInst() {
        return questionTplInst;
    }

    private static String loadTemplate(String path) throws IOException {
        return IOUtils.toString(
                new FileInputStream(new File("lib/templates/" + path)));
    }
}
