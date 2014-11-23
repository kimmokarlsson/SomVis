package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class ColorLabel extends JComponent
{
    public ColorLabel(int w, int h, Color init)
    {
        super();
        setPreferredSize(new Dimension(w, h));
        super.setBackground(init);
    }

    @Override
    public void paint(Graphics g)
    {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    @Override
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }
}
