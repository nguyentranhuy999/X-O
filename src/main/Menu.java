package main;

import button.ContinueButton;
import button.ExitButton;
import button.NewGameButton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Menu extends Panel{
    NewGameButton newGameButton;
    ContinueButton continueButton;
    ExitButton exitButton;
    BufferedImage tile, tile1, tile2;
    Random random;
    int randomInt;


    public Menu(Frame frame){
        super(frame);
        this.newGameButton = new NewGameButton(this,9 * tileSize ,3 * tileSize,6 * tileSize, 2* tileSize);
        this.continueButton = new ContinueButton(this,9 * tileSize,  6 * tileSize, 6 * tileSize, 2 * tileSize);
        this.exitButton = new ExitButton(this, 9 * tileSize, 9 * tileSize, 6 * tileSize, 2 * tileSize);
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
    public void update(){
        newGameButton.update();
        continueButton.update();
        exitButton.update();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D =(Graphics2D) g;
        for (int i = 0; i < screenCol; i++){
            g2D.drawImage(tile, i * tileSize, 0, tileSize, tileSize, null);
        }
        for (int i = 0; i < screenRow - 1; i++){
            for (int j = 0; j < screenCol; j++){
                if (Board[i][j] == 0) {
                    g2D.drawImage(tile1, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
                else if (Board[i][j] == 1) {
                    g2D.drawImage(tile2, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
                else {
                    g2D.drawImage(tile, j * tileSize, (i + 1) * tileSize, tileSize, tileSize, null);
                }
            }
        }
        newGameButton.draw(g2D);
        continueButton.draw(g2D);
        exitButton.draw(g2D);
        g2D.dispose();
    }
}

