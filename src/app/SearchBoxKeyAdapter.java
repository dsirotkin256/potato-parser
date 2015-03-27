package app;

import app.ui.MainFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchBoxKeyAdapter extends KeyAdapter {

    private Root root;

    public SearchBoxKeyAdapter(Root root) {
        this.root = root;
    }
    StopWatch timeCatched = new StopWatch();

    @Override

    public void keyReleased(KeyEvent arg0) {

        double seconds = timeCatched.elapsedTime();

        boolean isAllowed
                = (seconds >= 0.3);

        if (isAllowed) {

            // LOCK for UI interruption changes
            //Interrupt table updates by switching on this mode
            UI.setDontDisturbMode(Mode.ON);

            if (MainFrame.getInstance().searchBox.getText().isEmpty()) {
                // Fullfill table with questions from root derectory
                App.render.update(root.getQuestions());
                MainFrame.getInstance().lblSearch.setText("");
                UI.setDontDisturbMode(Mode.OFF);

            } else {

                String creteria = MainFrame.getInstance().searchBox.getText();

                // Start Search
                new SearchingProcess(creteria, root).doMagic();

            }

        }
        timeCatched = new StopWatch();
    }
};
