package app;

import java.io.File;
import javax.swing.JFileChooser;

public class App {

    static {
        ui = UI.instance;
        render = new TableRender();
    }

    public static UI ui;
    public static final TableRender render;

    public static void main(String[] args) {

        File directory = getRootDirectory();
        Root root = new Root(directory);

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

            // IF operation was canceled
            Runtime.getRuntime().exit(1);

        }

        return directory;
    }

}
