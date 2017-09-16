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

        File directory = getRootDirectory();
        try {
            Root root = new Root(directory);
            new ConcurrentProcessing("Initialising components",
                    new LoadingComponents(root)).start();
            App.ui.searchBox.addKeyListener(new SearchBoxKeyAdapter(root));

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(App.ui,
                    "Folder doesn't exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (Root.NoDocumentsFoundException ex) {
            JOptionPane.showMessageDialog(App.ui,
                    "Specified folder doesn't contain NAKS documents",
                    "Attention!",
                    JOptionPane.ERROR_MESSAGE);

            main(null);
        }
    }

    public static File getRootDirectory() {

        File directory = null;
        JFileChooser dir = new JFileChooser(System.getProperty("user.home") + "\\Desktop");

        dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dir.setApproveButtonText("Select folder");
        dir.setDialogTitle("Please, select folder containing NAKS documents");

        int result = dir.showOpenDialog(App.ui);

        if (result == JFileChooser.APPROVE_OPTION) {

            directory = dir.getSelectedFile();

        } else {
            System.exit(0);
        }

        return directory;
    }

}
