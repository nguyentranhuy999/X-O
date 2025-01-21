package button;

import main.Panel;

public class ContinueButton extends Button{

    public ContinueButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName(){
        this.name1 = "ContinueButton1";
        this.name2 = "ContinueButton2";
    }

    @Override
    public void functionUpdate(){
        if(button && panel.frame.started) {
            panel.frame.gameState = 4;
            panel.frame.update();
            button = false;
        }
        if(!panel.frame.started){
            image = image2;
        }
    }
}

