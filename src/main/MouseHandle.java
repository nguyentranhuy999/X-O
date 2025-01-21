package main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandle implements MouseListener, MouseMotionListener {
    public boolean click,enter,exit;
    Rectangle button;
    int x,y;
    int height,width;

    // Constructor
    public MouseHandle(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        button = new Rectangle(x, y, width, height);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if( button.contains(e.getPoint())){
            click = true;
        }
        else{
            click = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    // Mouse enter and exit
    @Override
    public void mouseMoved(MouseEvent e) {
        if( button.contains(e.getPoint())){
            enter = true;
            exit  = false;
        }
        else{
            enter = false;
            exit = true;
        }
    }
}
