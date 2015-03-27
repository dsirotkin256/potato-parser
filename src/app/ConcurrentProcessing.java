package app;

/*
 *  This class runs threads passed as a parameter and controls UI
 */
public class ConcurrentProcessing implements Magical {

    private String message;
    private Magical foo;

    public ConcurrentProcessing(String message, Magical foo) {
        this.message = message;
        this.foo = foo;

    }

    @Override
    public void doMagic() {

        foo.doMagic();

        App.ui.lblSearch.setText("");

    }
}
