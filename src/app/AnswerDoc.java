package app;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class AnswerDoc {

    private HWPFDocument answerDoc;

    WordExtractor answersExt;

    private String docName;
    private String answerPath;

    public AnswerDoc(String path) {

        String[] dirs = path.split(":?\\\\");
        docName = dirs[dirs.length - 1];

        this.answerPath = path;

        try {
            this.answerDoc = new HWPFDocument(new FileInputStream(answerPath));
            this.answersExt = new WordExtractor(answerDoc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getDocName() {
        return docName;
    }

    public String getDocText() {
        return this.answersExt.getText();
    }
}
