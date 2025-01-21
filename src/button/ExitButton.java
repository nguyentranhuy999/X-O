package button;

import main.Panel;

public class ExitButton extends Button{

    public ExitButton(Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
    }

    @Override
    public void setImageName() {
        this.name1 = "ExitButton1";
        this.name2 = "ExitButton2";
    }

    @Override
    public void functionUpdate() {
        if(button){
            System.exit(0);
        }
    }
}
