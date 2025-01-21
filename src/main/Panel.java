package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public abstract class Panel extends JPanel implements Runnable {

    final int originalTileSize = 16;
    final int scale = 3;
    public int tileSize = scale * originalTileSize;
    public final int screenCol = 24;
    public final int screenRow = 14;
    public int screenWidth = tileSize * screenCol;
    public int screenHeight = tileSize * screenRow;

    public final int fps = tileSize;

    public Thread thread;

    public Frame frame;

    public int[][] Board = new int[screenRow - 1][screenCol];

    public Panel(Frame frame) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    public void startGameThread() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (thread != null) {
            update();
            repaint();
            try {
                Thread.sleep(1000 / fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void update();

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
