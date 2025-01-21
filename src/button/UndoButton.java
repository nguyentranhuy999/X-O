package button;

import main.GamePanel;
import main.Panel;

public class UndoButton extends Button{
    GamePanel panel;
    public UndoButton(GamePanel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.panel = panel;
    }

    @Override
    public void setImageName() {
        name1 = "UndoButton1";
        name2 = "UndoButton2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            if (panel.historyIndex == -1){
                button = false;
            }
            else {
                panel.turn = panel.turn * -1;
                panel.checkTie--;
                panel.Board[panel.history.get(panel.historyIndex).first][panel.history.get(panel.historyIndex).second] = 0;
                panel.historyIndex--;

                panel.end = false;
                panel.startX = -1;
                panel.startY = -1;
                panel.endX = -1;
                panel.endY = -1;

                button = false;
            }
        }
        if (panel.historyIndex == -1){
            image = image2;
        }
    }
}
