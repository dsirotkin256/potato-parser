package app;

import java.io.File;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    static {
        Locale.setDefault(new Locale("ru"));
        ui = UI.instance;
        render = new TableRender();
        logger = LogManager.getLogger(App.class.getName());
    }

    public static UI ui;
    public static final TableRender render;
    public static final Logger logger;

    public static void main(String[] args) {

        logger.info("Logger Init..");

//        File directory = getRootDirectory();
        try {
            Root root = new Root(new File("sample"));
            new ConcurrentProcessing("Загрузка компонентов",
                    new LoadingComponents(root)).start();
            App.ui.searchBox.addKeyListener(new SearchBoxKeyAdapter(root));

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(App.ui,
                    "Данной папки не существует.",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (Root.NoDocumentsFoundException ex) {
            JOptionPane.showMessageDialog(App.ui,
                    "Указанная папка не содержит документы НАКС",
                    "Внимание!",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }

    public static File getRootDirectory() {

        File directory = null;
        JFileChooser dir = new JFileChooser(System.getProperty("user.home") + "\\Desktop");

        dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dir.setApproveButtonText("Выбрать папку");
        dir.setDialogTitle("Выберите папку с документами НАКС");

        int result = dir.showOpenDialog(App.ui);

        if (result == JFileChooser.APPROVE_OPTION) {

            directory = dir.getSelectedFile();

        } else {
            System.exit(0);
        }

        return directory;
    }

}
