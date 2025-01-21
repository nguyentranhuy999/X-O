package button;

import main.Panel;

public class OBotButton extends Button{
    public OBotButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName() {
        name1 = "OBot1";
        name2 = "OBot2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            panel.frame.gamePanelState = 1;
            panel.frame.gameState = 3;
            panel.frame.update();
            button = false;
        }
    }
}

