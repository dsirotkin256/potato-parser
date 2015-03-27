package app;

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

            if (App.ui.searchBox.getText().isEmpty()) {
                // Fullfill table with questions from root derectory
                App.render.update(root.getQuestions());
                App.ui.lblSearch.setText("");
                UI.setDontDisturbMode(Mode.OFF);

            } else {

                String creteria = App.ui.searchBox.getText();

                // Start Search
                new ConcurrentProcessing("Выполняется поиск",
                        new SearchingQuestion(creteria, root)).start();

            }

        }
        timeCatched = new StopWatch();
    }
};
