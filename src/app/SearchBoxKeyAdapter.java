package app;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchBoxKeyAdapter extends KeyAdapter {

    private Root root;

    public SearchBoxKeyAdapter(Root root) {
        this.root = root;
    }

    @Override
    public void keyPressed(KeyEvent arg0) {

        boolean isAllowed
                = (arg0.getKeyCode() == KeyEvent.VK_ENTER && (arg0.getWhen() / 1000) >= 2);

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
    }
};
