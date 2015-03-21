package app;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

/**
 *
 * @author Eiger
 */
public abstract class Document {

    protected String path;
    protected String name;
    protected HWPFDocument document;
    protected WordExtractor extractor;

    public Document(String path) throws IOException {
        setDocument(path);
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    private void setDocument(String path) throws IOException {
        setPath(path);

        document = new HWPFDocument(new FileInputStream(path));
        extractor = new WordExtractor(document);
    }

    private void setPath(String path) {
        this.path = path;
        setName();
    }

    private void setName() {

        String[] dirs = path.split(":?\\\\");
        name = dirs[dirs.length - 1];

        this.name = name;
    }

    @Override
    public String toString() {
        return extractor.getText(); //To change body of generated methods, choose Tools | Templates.
    }

}
