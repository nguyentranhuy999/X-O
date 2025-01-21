package button;

import main.MouseHandle;
import main.Panel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Button {
    Panel panel;
    BufferedImage image, image1, image2;
    String name1,name2;
    public MouseHandle mouseHandle;
    public int x, y, width, height;
    public boolean button;

    public Button(Panel panel, int x, int y, int width, int height) {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mouseHandle = new MouseHandle(x, y, width, height);
        panel.addMouseListener(mouseHandle);
        panel.addMouseMotionListener(mouseHandle);
        setImageName();
        getImage();
        image = image1;
    }

    public abstract void setImageName();

    public void getImage(){
        try {
            image1 = ImageIO.read((getClass().getResourceAsStream("/image/" + name1 + ".png")));
            image2 = ImageIO.read((getClass().getResourceAsStream("/image/" + name2 + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonUpdate() {
        if (mouseHandle.enter) {
            if (image == image1) {
                image = image2;
            }
            mouseHandle.enter = false;
        }

        if (mouseHandle.exit) {
            if (image == image2) {
                image = image1;
            }
            mouseHandle.exit = false;
        }

        if (mouseHandle.click) {
            button = true;
            mouseHandle.click = false;
        }
    }

    public abstract void functionUpdate();

    public void update(){
        buttonUpdate();
        functionUpdate();
    }

    public void draw(Graphics2D g2D) {
        g2D.drawImage(image, x, y, width, height, null);
    }
}
