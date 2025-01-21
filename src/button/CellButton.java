package button;

import main.GamePanel;

public class CellButton extends Button{
    int i,j;
    GamePanel panel;

    public CellButton(GamePanel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.panel = panel;
        this.i = (y / panel.tileSize) - 1;
        this.j = x / panel.tileSize;
    }

    @Override
    public void setImageName() {
        name1 = "square1";
        name2 = "square2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            if (panel.end){
                button = false;
            }
            else if (panel.Board[this.i][this.j] != 0){
                button = false;
            }
        }
        if (panel.end){
            image = image1;
        }
    }
}
