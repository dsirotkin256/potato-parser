package app;

import app.ui.MainFrame;;
import java.io.File;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    static {
        Locale.setDefault(new Locale("ru"));
        render = new TableRender();
        logger = LogManager.getLogger(App.class.getName());
    }

    public static final TableRender render;
    public static final Logger logger;

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

    public static File getRootDirectory() {

        File directory = null;
        JFileChooser dir = new JFileChooser(System.getProperty("user.home") + "\\Desktop");

        dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dir.setApproveButtonText("Выбрать папку");
        dir.setDialogTitle("Выберите папку с документами НАКС");

        int result = dir.showOpenDialog(MainFrame.getInstance());

        if (result == JFileChooser.APPROVE_OPTION) {

            directory = dir.getSelectedFile();

        } else {
            System.exit(0);
        }

        return directory;
    }

}
