package app;

import app.ui.MainFrame;
import java.util.TreeSet;
import java.util.stream.Collectors;

/*
 * This class represents the searching method and it's behaviour
 */
public class SearchingProcess implements Magical {

    String creteria;
    Root root;

    public SearchingProcess(String creteria, Root root) {
        this.creteria = creteria;
        this.root = root;
    }

    @Override
    public void doMagic() {

        System.out.println("Performing search...");

        StopWatch stopWatch = new StopWatch();

        TreeSet<Question> results
                = new TreeSet<>(root.extractLoadedQuestions().stream()
                        .filter(question -> root.isValid(question, creteria))
                        .collect(Collectors.toList()));

        double time = stopWatch.elapsedTime();

        int resultsCount = results.size();

        // Fullfill table with founded questions
        UI.setDontDisturbMode(Mode.ON);
        App.render.update(results);

        String response = (results.isEmpty())
                ? "По вашему запросу ничего не найдено"
                : "Найдено: " + resultsCount + " "
                + root.getCorrectStrEnding(resultsCount,
                        "результат") + "  (" + time % 60 + " сек.) ";

        MainFrame.getInstance().lblSearch.notify();
        MainFrame.getInstance().lblSearch.setText(response);
        try {
            MainFrame.getInstance().lblSearch.wait();
        } catch (InterruptedException ex) {

        }

    }

}
