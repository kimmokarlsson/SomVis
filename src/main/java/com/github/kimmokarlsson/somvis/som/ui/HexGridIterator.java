package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;

/**
 * 
 */
public class HexGridIterator implements Iterator<HexGridCell>, Iterable<HexGridCell>
{
    private int cols;
    private int rows;

    private int cRow;
    private int cCol;
    private int x;
    private int y;
    private Polygon hex;
    private int lineHeight;
    private int hexWidth;

    public HexGridIterator(int cols, int rows, int edge)
    {
        this.cols = cols;
        this.rows = rows;
        
        // create hexagon
        final int vertices = 6;
        hex = new Polygon();
        double a = Math.PI/vertices;
        for (int i = 0; i < vertices; i++, a += 2.0*Math.PI/vertices)
        {
            hex.addPoint((int) Math.round(Math.cos(a)*edge),
                         (int) Math.round(Math.sin(a)*edge));
        }        
        lineHeight = (int) Math.round(edge + edge * Math.cos(2.0 * Math.PI / vertices));
        Rectangle hexBounds = hex.getBounds();
        x = hexBounds.width;
        y = hexBounds.height;
        hexWidth = hexBounds.width;
    }

    @Override
    public boolean hasNext()
    {
        return cRow < rows;
    }
    
    @Override
    public HexGridCell next()
    {
        if (cRow < rows)
        {
            Polygon hex2 = new Polygon(hex.xpoints, hex.ypoints, hex.npoints);
            int pad = 0;
            if (cRow % 2 == 1)
            {
                pad = hexWidth/2;
            }
            for (int n = 0; n < hex2.npoints; n++)
            {
                hex2.xpoints[n] += x + pad;
                hex2.ypoints[n] += y;
            }
            
            HexGridCell cell = new HexGridCell(hex2, cCol, cRow);
            
            cCol++;
            x += hexWidth;
            if (cCol >= cols)
            {
                cCol = 0;
                x = hexWidth;
                cRow++;
                y += lineHeight;
            }
            
            return cell;
        }
        return null;
    }

    @Override
    public Iterator<HexGridCell> iterator()
    {
        return this;
    }

    @Override
    public void remove()
    {
        throw new IllegalStateException();
    }
}
