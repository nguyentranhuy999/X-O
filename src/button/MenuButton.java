package button;

import main.Panel;

public class MenuButton extends Button{

    public MenuButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName(){
        this.name1 = "HomeButton1";
        this.name2 = "HomeButton2";
    }

    @Override
    public void functionUpdate(){
        if(button) {
            panel.frame.gameState = 1;
            panel.frame.update();
            button = false;
        }
    }
}
