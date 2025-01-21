package button;

import main.Panel;

public class NewGameButton extends Button{

    public NewGameButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName(){
        this.name1 = "NewGameButton1";
        this.name2 = "NewGameButton2";
    }

    @Override
    public void functionUpdate(){
        if(button) {
            panel.frame.gameState = 2;
            panel.frame.update();
            button = false;
        }
    }
}