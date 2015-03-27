package app;

import app.ui.MainFrame;
import java.io.File;
import java.util.Locale;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class App {

    public static final Logger logger;

    static {
        Locale.setDefault(new Locale("ru"));
        logger = LogManager.getLogger(App.class.getName());
    }

    public static void main(String[] args) {

        try {
            Root root = new Root(new File("temp"));
            new LoadingProcess(root).doMagic();
            MainFrame.getInstance().searchBox.addKeyListener(new SearchBoxKeyAdapter(root));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "Данной папки не существует.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        } catch (Root.NoDocumentsFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "Указанная папка не содержит документы НАКС",
                    "Внимание!",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }
}
