package app;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.SwingWorker;

public class ProgressWorker extends SwingWorker<Object, Object> {

    private LoadingComponents loadCom;

    public ProgressWorker(LoadingComponents loadCom) {
        this.loadCom = loadCom;

        this.loadCom.getLoading().button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadCom.getLoading().dispose();
            }
        });

        this.addPropertyChangeListener((PropertyChangeEvent evt) -> {
            String name = evt.getPropertyName();
            if (name.equals("progress")) {
                int progress1 = (int) evt.getNewValue();
                loadCom.getLoading().progressBar.setValue(progress1);
                loadCom.getLoading().progressBar.repaint();
            }
        });
    }

    @Override
    protected Object doInBackground() throws Exception {
        while (loadCom.percentage <= 100) {

            setProgress(loadCom.percentage);
        }
        return null;
    }

}
