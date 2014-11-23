package com.github.kimmokarlsson.somvis.som.ui;

import java.awt.Polygon;

public class HexGridCell
{
    private Polygon polygon;
    private int row;
    private int col;
    
    public HexGridCell(Polygon p, int i, int j)
    {
        this.polygon = p;
        this.col = i;
        this.row = j;
    }

    public Polygon getPolygon()
    {
        return polygon;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    /**
     *   /\
     *  |  |  
     *   \/
     *   
     * @return
     */
    public Polygon getSmallerPolygon()
    {
        int[] xpoints = new int[polygon.npoints];
        int[] ypoints = new int[polygon.npoints];

        xpoints[0] = polygon.xpoints[0] - 1;
        ypoints[0] = polygon.ypoints[0];
        xpoints[1] = polygon.xpoints[1];
        ypoints[1] = polygon.ypoints[1] - 1;
        xpoints[2] = polygon.xpoints[2] - 1;
        ypoints[2] = polygon.ypoints[2];
        xpoints[3] = polygon.xpoints[3] - 1;
        ypoints[3] = polygon.ypoints[3];
        xpoints[4] = polygon.xpoints[4];
        ypoints[4] = polygon.ypoints[4] - 1;
        xpoints[5] = polygon.xpoints[5] - 1;
        ypoints[5] = polygon.ypoints[5];
        
        return new Polygon(xpoints, ypoints, xpoints.length);
    }
}
