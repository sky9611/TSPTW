package com.septanome.ihm;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import com.septanome.model.Livraison;
import com.septanome.model.Point;

public class IHMPoint extends JButton {

    private boolean isLivraison;
    private Point point;
    private Livraison livraison;

    public IHMPoint(Point p) {
        isLivraison = false;
        this.point = p;
        java.awt.Point location = getLocation();
        Dimension size = getPreferredSize();
        size.width = 5;
        size.height = 5;
        location.x = p.getCoordX();
        location.y = p.getCoordY();
        setPreferredSize(size);
        setLocation(location);
        setContentAreaFilled(false);
        setBackground(Color.BLACK);
    }

    public IHMPoint(Livraison livraison) {
        isLivraison = true;
        this.livraison = livraison;
        java.awt.Point location = getLocation();
        Dimension size = getPreferredSize();
        size.width = 10;
        size.height = 10;
        location.x = livraison.getCoordX();
        location.y = livraison.getCoordY();
        setPreferredSize(size);
        setLocation(location);
        setContentAreaFilled(false);
        setBackground(Color.GREEN);
    }

    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(0, 0, getSize().width - 1,
                getSize().height - 1);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(0, 0, getSize().width - 1,
                getSize().height - 1);
    }

    Shape shape;

    public boolean contains(int x, int y) {
        if (shape == null ||
                !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(0, 0,
                    getWidth(), getHeight());
        }
        return shape.contains(x, y);
    }
}
