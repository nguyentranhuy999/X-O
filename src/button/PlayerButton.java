package button;

import main.Panel;

public class PlayerButton extends Button{

    public PlayerButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName() {
        name1 = "2Player1";
        name2 = "2Player2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            panel.frame.gamePanelState = 0;
            panel.frame.gameState = 3;
            panel.frame.update();
            button = false;
        }
    }
}

