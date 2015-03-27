package app;

/*
 *  This class runs threads passed as a parameter and controls UI
 */
public class ConcurrentProcessing implements Magical {

    private volatile boolean isInterrupted = false;
    private String message;
    private Magical foo;

    public ConcurrentProcessing(String message, Magical foo) {
        this.message = message;
        this.foo = foo;

    }

    @Override
    public void doMagic() {

        new Thread(() -> {
            foo.doMagic();
        }).start();

        synchronized (App.ui.lblSearch) {
            App.ui.lblSearch.setText("");
        }

    }
}
