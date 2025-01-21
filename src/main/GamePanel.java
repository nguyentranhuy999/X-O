package main;

import Bot.Own;
import button.CellButton;
import button.MenuButton;
import button.RedoButton;
import button.UndoButton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends Panel {
    MenuButton menuButton;
    UndoButton undoButton;
    RedoButton redoButton;
    public int gamePanelState;
    public CellButton [][] cellButtons = new CellButton[screenRow - 1][screenCol];
    public ArrayList<Pair<Integer, Integer>> history = new ArrayList<>(100);
    public int historyIndex = -1;
    public int turn = 1;
    public int checkTie = 0;
    public boolean end = false;
    public int startX = -1;
    public int startY = -1;
    public int endX = -1;
    public int endY = -1;

    Own own = new Own(this);

    Font arial_40;

    BufferedImage tile1, tile2;

    public GamePanel(Frame frame) {
        super(frame);
        this.gamePanelState = frame.gamePanelState;
        this.menuButton = new MenuButton(this,0,0,2* tileSize,tileSize);
        this.undoButton = new UndoButton(this, 22 * tileSize, 0, tileSize, tileSize);
        this.redoButton = new RedoButton(this, 23 * tileSize, 0, tileSize, tileSize);

        for (int i = 0; i < screenRow - 1; i++){
            for (int j = 0; j < screenCol; j++){
                cellButtons[i][j] = new CellButton(this, j * tileSize, (i + 1) * tileSize, tileSize, tileSize);
                Board[i][j] = 0;
            }
        }
        arial_40 = new Font("Arial", Font.BOLD, 40);
        getImage();
    }

    public void getImage(){
        try {
            tile1 = ImageIO.read((getClass().getResourceAsStream("/image/X.png")));
            tile2 = ImageIO.read((getClass().getResourceAsStream("/image/O.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int checkWin(int i, int j, int turn){
        int count = 0;

        // Hang ngang
        count = turn;
        for (int a = 1; ; a++){
            if (j + a < screenCol){
                if (Board[i][j + a] == turn){
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        for (int a = 1; ; a++){
            if (j - a >= 0) {
                if (Board[i][j - a] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        if (count >= 5 || count <= -5){
            return 1;
        }

        // Hang doc
        count = turn;
        for (int a = 1; ; a++) {
            if (i + a < screenRow - 1) {
                if (Board[i + a][j] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        for (int a = 1; ; a++) {
            if (i - a >= 0) {
                if (Board[i - a][j] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            } else {
                break;
            }
        }
        if (count >= 5 || count <= -5){
            return 2;
        }

        // Hang cheo 1
        count = turn;
        for (int a = 1; ; a++) {
            if (i + a < screenRow - 1 && j + a < screenCol) {
                if (Board[i + a][j + a] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        for (int a = 1; ; a++) {
            if (i - a >= 0 && j - a >= 0) {
                if (Board[i - a][j - a] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        if (count >= 5 || count <= -5){
            return 3;
        }

        // Hang cheo 2
        count = turn;
        for (int a = 1; ; a++) {
            if (i + a < screenRow - 1 && j - a >= 0) {
                if (Board[i + a][j - a] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        for (int a = 1; ; a++) {
            if (i - a >= 0 && j + a < screenCol) {
                if (Board[i - a][j + a] == turn) {
                    count = count + turn;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
        if (count >= 5 || count <= -5){
            return 4;
        }
        return 0;
    }

    @Override
    public void update(){

        // Button
        menuButton.update();
        undoButton.update();
        redoButton.update();

        // Bot
        if (turn == -1 && historyIndex == history.size() - 1 && !history.get(history.size() - 1).special){
            own.update();
        }

        // Cell
        for (int i = 0; i < screenRow - 1; i++) {
            for (int j = 0; j < screenCol; j++) {
                cellButtons[i][j].update();
                if (cellButtons[i][j].button){
                    Board[i][j] = turn;

                    // Hang ngang
                    if (checkWin(i, j, this.turn) == 1){
                        for (int a = 1; ; a++) {
                            if (j + a < screenCol) {
                                if (Board[i][j + a] != turn) {
                                    startX = ((j + a - 1) * tileSize) + tileSize / 2;
                                    startY = ((i + 1) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                startX = ((j + a - 1) * tileSize) + tileSize / 2;
                                startY = ((i + 1) * tileSize) + tileSize / 2;
                                break;
                            }
                        }

                        for (int a = 1; ; a++) {
                            if (j - a >= 0) {
                                if (Board[i][j - a] != turn) {
                                    endX = ((j - a + 1) * tileSize) + tileSize / 2;
                                    endY = ((i + 1) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                endX = ((j - a + 1) * tileSize) + tileSize / 2;
                                endY = ((i + 1) * tileSize) + tileSize / 2;
                                break;
                            }
                        }
                    }

                    // Hang doc
                    else if (checkWin(i, j, this.turn) == 2){
                        for (int a = 1; ; a++) {
                            if (i + a < screenRow - 1) {
                                if (Board[i + a][j] != turn) {
                                    startX = (j * tileSize) + tileSize / 2;
                                    startY = ((i + a) * tileSize) + tileSize / 2;
                                    break;
                                }
                            } else {
                                startX = (j * tileSize) + tileSize / 2;
                                startY = ((i + a) * tileSize) + tileSize / 2;
                                break;
                            }
                        }

                        for (int a = 1; ; a++) {
                            if (i - a >= 0) {
                                if (Board[i - a][j] != turn) {
                                    endX = (j * tileSize) + tileSize / 2;
                                    endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                endX = (j * tileSize) + tileSize / 2;
                                endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                break;
                            }
                        }
                    }

                    // Hang cheo 1
                    else if (checkWin(i, j, this.turn) == 3){
                        for (int a = 1; ; a++) {
                            if (i + a < screenRow - 1 && j + a < screenCol) {
                                if (Board[i + a][j + a] != turn) {
                                    startX = ((j + a - 1) * tileSize) + tileSize / 2;
                                    startY = ((i + a) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                startX = ((j + a - 1) * tileSize) + tileSize / 2;
                                startY = ((i + a) * tileSize) + tileSize / 2;
                                break;
                            }
                        }

                        for (int a = 1; ; a++) {
                            if (i - a >= 0 && j - a >= 0) {
                                if (Board[i - a][j - a] != turn) {
                                    endX = ((j - a + 1) * tileSize) + tileSize / 2;
                                    endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                endX = ((j - a + 1) * tileSize) + tileSize / 2;
                                endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                break;
                            }
                        }
                    }

                    // Hang cheo 2
                    else if (checkWin(i, j, this.turn) == 4){
                        for (int a = 1; ; a++) {
                            if (i + a < screenRow - 1 && j - a >= 0) {
                                if (Board[i + a][j - a] != turn) {
                                    startX = ((j - a + 1) * tileSize) + tileSize / 2;
                                    startY = ((i + a) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                startX = ((j - a + 1) * tileSize) + tileSize / 2;
                                startY = ((i + a) * tileSize) + tileSize / 2;
                                break;
                            }
                        }

                        for (int a = 1; ; a++) {
                            if (i - a >= 0 && j + a < screenCol) {
                                if (Board[i - a][j + a] != turn) {
                                    endX = ((j + a - 1) * tileSize) + tileSize / 2;
                                    endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                    break;
                                }
                            }
                            else {
                                endX = ((j + a - 1) * tileSize) + tileSize / 2;
                                endY = ((i - a + 2) * tileSize) + tileSize / 2;
                                break;
                            }
                        }
                    }

                    // Lich su
                    if (historyIndex < history.size() - 1){
                        history.clear();
                        historyIndex = -1;
                    }
                    Pair pair = new Pair(i, j);
                    if (checkWin(i, j, this.turn) != 0){
                        pair.special = true;
                        pair.winX1 = startX;
                        pair.winY1 = startY;
                        pair.winX2 = endX;
                        pair.winY2 = endY;
                    }
                    if (history.size() == 100){
                        history.remove(0);
                    }
                    history.add(pair);

                    historyIndex ++;

                    checkTie ++;
                    if (checkWin(i, j, this.turn) > 0 || checkTie == 312){
                        end = true;
                    }
                    turn = turn * -1;
                    cellButtons[i][j].button = false;
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D =(Graphics2D) g;
        menuButton.draw(g2D);
        undoButton.draw(g2D);
        redoButton.draw(g2D);

        for (int i = 0; i < screenRow - 1; i++){
            for (int j = 0; j < screenCol; j++){
                if (Board[i][j] == 0) {
                    cellButtons[i][j].draw(g2D);
                }
                else if (Board[i][j] == 1){
                    g2D.drawImage(tile1, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
                else{
                    g2D.drawImage(tile2, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
            }
        }
        if (end && turn == -1){
            g2D.setColor(new Color(244, 67, 54));
            g2D.setFont(arial_40);
            g2D.drawString("X Wins!", 11 * tileSize, (3 * tileSize) / 4);
        }
        else if (end && turn == 1){
            g2D.setColor(new Color(3, 169, 244));
            g2D.setFont(arial_40);
            g2D.drawString("O Wins!", 11 * tileSize, (3 * tileSize) / 4);
        }
        else if (checkTie == 312){
            g2D.setColor(new Color(255, 255, 255));
            g2D.setFont(arial_40);
            g2D.drawString("Tie!", 12 * tileSize, (3 * tileSize) / 4);
        }
        else {
            if (turn == 1) {
                g2D.setColor(new Color(244, 67, 54));
                g2D.setFont(arial_40);
                g2D.drawString("X's Turn", 11 * tileSize, (3 * tileSize) / 4);
            } else {
                g2D.setColor(new Color(3, 169, 244));
                g2D.setFont(arial_40);
                g2D.drawString("O's Turn", 11 * tileSize, (3 * tileSize) / 4);
            }
        }
        if (historyIndex == history.size() - 1 && historyIndex != -1) {
            if (history.get(historyIndex).special) {
                g2D.setStroke(new BasicStroke(3));
                g2D.setColor(new Color(0, 0, 0));
                g2D.drawLine(history.get(historyIndex).winX1, history.get(historyIndex).winY1, history.get(historyIndex).winX2, history.get(historyIndex).winY2);
                end = true;
            }
        }
        g2D.dispose();
    }
}

