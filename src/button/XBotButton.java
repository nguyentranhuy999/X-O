package button;

import main.Panel;

public class XBotButton extends Button{
    public XBotButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName() {
        name1 = "XBot1";
        name2 = "XBot2";
    }

    @Override
    public void functionUpdate() {
        if(button){
            panel.frame.gamePanelState = 2;
            panel.frame.gameState = 3;
            panel.frame.update();
            button = false;
        }
    }
}

