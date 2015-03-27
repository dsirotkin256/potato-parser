package app;

import javax.swing.SwingWorker;

public class ProgressWorker extends SwingWorker<Object, Object> {

    private LoadingComponents loadCom;

    public ProgressWorker(LoadingComponents loadCom) {
        this.loadCom = loadCom;
    }

    @Override
    protected Object doInBackground() throws Exception {
        while (loadCom.percentage <= 100) {

            setProgress(loadCom.percentage);
        }
        return null;
    }

}
