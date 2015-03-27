package app;

public class UI {

    private static Mode dontDisturbMode = Mode.OFF;

    public static void setDontDisturbMode(Mode dontDisturbMode) {
        UI.dontDisturbMode = dontDisturbMode;
    }

    public static Mode getDontDisturbMode() {
        return dontDisturbMode;
    }
}
