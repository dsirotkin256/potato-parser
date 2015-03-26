package app;

/*
 *  This class runs threads passed as a parameter and controls UI
 */
public class ConcurrentProcessing extends Thread {

    private volatile boolean isInterrupted = false;
    private String message;
    private Runnable foo;

    public ConcurrentProcessing(String message, Runnable foo) {
        this.message = message;
        this.foo = foo;

    }

    @Override
    public void interrupt() {
        //Notify 'while loop' about interuption
        isInterrupted = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        //Start job
        new Thread() {

            @Override
            public void run() {

                System.out.println("Preparing to call function...");
                foo.run();
                System.out.println("Finished");
                isInterrupted = true;
                // End Action
            }
        }.start();

        //Start waiting
        while (!isInterrupted) {

            for (int i = 0; i <= 3 && UI.getDontDisturbMode() == Mode.OFF; i++) {

                synchronized (App.ui.lblSearch) {
                    App.ui.lblSearch.notify();
                    if (i == 0 && !isInterrupted) {
                        App.ui.lblSearch.setText(message);
                    }
                    if (i == 1 && !isInterrupted) {
                        App.ui.lblSearch.setText(message + ".");
                    }
                    if (i == 2 && !isInterrupted) {
                        App.ui.lblSearch.setText(message + ". .");
                    }
                    if (i == 3 && !isInterrupted) {
                        App.ui.lblSearch.setText(message + ". . .");
                    }

                }
                try {
                    this.sleep(400);
                } catch (InterruptedException ex) {

                }

            }
        }
        synchronized (App.ui.lblSearch) {
            App.ui.lblSearch.setText("");
        }

    }
}
