package main;

import javax.swing.*;

public class Frame extends JFrame {
    GamePanel gamePanel;
    Menu menu;
    Mode mode;
    public int gameState;
    public boolean started = false;
    public int gamePanelState;

    public Frame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("X-O");
        this.gamePanelState = 0;
        this.gameState = 1;
        gamePanel = new GamePanel(this);
        mode = new Mode(this);
        menu = new Menu(this);
        this.add(menu);
        menu.startGameThread();
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void update(){
        if(gameState == 1 ){
            gamePanel.thread = null;
            mode.thread = null;
            this.remove(gamePanel);
            this.remove(mode);
            this.add(menu);
            menu.startGameThread();
            this.revalidate();
            this.repaint();
            menu.requestFocusInWindow();
        }

        else if (gameState == 2){
            this.remove(menu);
            menu.thread = null;
            this.add(mode);
            mode.startGameThread();
            this.revalidate();
            this.repaint();
            mode.requestFocusInWindow();
        }

        else if (gameState == 3){
            this.remove(mode);
            mode.thread = null;
            started = true;
            gamePanel = new GamePanel(this);
            this.add(gamePanel);
            gamePanel.startGameThread();
            this.revalidate();
            this.repaint();
            gamePanel.requestFocusInWindow();
        }

        else if (gameState == 4){
            this.remove(menu);
            menu.thread = null;
            this.add(gamePanel);
            gamePanel.startGameThread();
            this.revalidate();
            this.repaint();
            gamePanel.requestFocusInWindow();
        }
    }
}
