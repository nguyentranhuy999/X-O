package main;

import button.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Mode extends Panel {
    PlayerButton playerButton;
    OBotButton oBotButton;
    XBotButton xBotButton;
    BotButton botButton;
    MenuButton menuButton;
    Random random;
    int randomInt;
    BufferedImage tile, tile1, tile2;

    public Mode(Frame frame) {
        super(frame);
        this.playerButton = new PlayerButton(this, 9 * tileSize,2 * tileSize,6 * tileSize, 2 * tileSize);
        this.oBotButton = new OBotButton(this, 9 * tileSize, 5 * tileSize, 6 * tileSize, 2 * tileSize);
        this.xBotButton = new XBotButton(this, 9 * tileSize, 8 * tileSize, 6 * tileSize, 2 * tileSize);
        this.botButton = new BotButton(this, 9 * tileSize, 11 * tileSize, 6 * tileSize, 2 * tileSize);
        this.menuButton = new MenuButton(this, 0, 0, 2 * tileSize, tileSize);
        random = new Random();
        for (int i = 0; i < screenRow - 1; i++){
            for (int j = 0; j < screenCol; j++){
                randomInt = random.nextInt(20);
                Board[i][j] = randomInt;
            }
        }
        getImage();
    }

    public void getImage(){
        try {
            tile = ImageIO.read((getClass().getResourceAsStream("/image/square1.png")));
            tile1 = ImageIO.read((getClass().getResourceAsStream("/image/X.png")));
            tile2 = ImageIO.read((getClass().getResourceAsStream("/image/O.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        playerButton.update();
        oBotButton.update();
        xBotButton.update();
        botButton.update();
        menuButton.update();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D =(Graphics2D) g;
        for (int i = 0; i < screenRow - 1; i++){
            for (int j = 0; j < screenCol; j++){
                if (Board[i][j] == 0) {
                    g2D.drawImage(tile1, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
                else if (Board[i][j] == 1) {
                    g2D.drawImage(tile2, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
                else{
                    g2D.drawImage(tile, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
            }
        }
        playerButton.draw(g2D);
        oBotButton.draw(g2D);
        xBotButton.draw(g2D);
        botButton.draw(g2D);
        menuButton.draw(g2D);
        g2D.dispose();
    }
}

