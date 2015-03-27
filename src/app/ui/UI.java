package app.ui;

import app.Mode;
import app.TableRender;
import java.io.File;
import javax.swing.JFileChooser;

public abstract class UI {

    private static Mode dontDisturbMode = Mode.OFF;
    public static final TableRender render;

    static {
        render = new TableRender();
    }

    public static void setDontDisturbMode(Mode dontDisturbMode) {
        UI.dontDisturbMode = dontDisturbMode;
    }

    public static Mode getDontDisturbMode() {
        return dontDisturbMode;
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
