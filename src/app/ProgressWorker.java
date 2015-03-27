package app;

import java.beans.PropertyChangeEvent;
import javax.swing.SwingWorker;

public class ProgressWorker extends SwingWorker<Object, Object> {

    private LoadingProcess loadCom;

    public ProgressWorker(LoadingProcess loadCom) {
        this.loadCom = loadCom;

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
