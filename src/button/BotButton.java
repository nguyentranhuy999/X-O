package button;

import main.Panel;

public class BotButton extends Button{
    public BotButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName() {
        name1 = "2Bot1";
        name2 = "2Bot2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            panel.frame.gamePanelState = 3;
            panel.frame.gameState = 3;
            panel.frame.update();
            button = false;
        }
    }
}

