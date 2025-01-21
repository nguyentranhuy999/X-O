package button;

import main.GamePanel;
import main.Panel;

public class RedoButton extends Button{
    GamePanel panel;
    public RedoButton(GamePanel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.panel = panel;
    }

    @Override
    public void setImageName() {
        name1 = "RedoButton1";
        name2 = "RedoButton2";
    }

    @Override
    public void functionUpdate() {
        if (button){
            if (panel.historyIndex == panel.history.size() - 1){
                button = false;
            }
            else {
                panel.checkTie++;
                panel.historyIndex++;
                panel.Board[panel.history.get(panel.historyIndex).first][panel.history.get(panel.historyIndex).second] = panel.turn;
                panel.turn = panel.turn * -1;

                panel.end = false;
                panel.startX = -1;
                panel.startY = -1;
                panel.endX = -1;
                panel.endY = -1;
                button = false;
            }
        }
        if (panel.historyIndex == panel.history.size() - 1){
            image = image2;
        }
    }
}
