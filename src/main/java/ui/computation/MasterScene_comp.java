package ui.computation;

import ui.MasterScene;
public class MasterScene_comp extends MasterScene {
    static MasterScene_comp instance;
    public static MasterScene_comp getInstance() {
        if (instance == null)
            instance = new MasterScene_comp();
        return instance;
    }

    private MasterScene_comp() {}
}
