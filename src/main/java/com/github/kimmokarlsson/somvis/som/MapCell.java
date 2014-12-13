package com.github.kimmokarlsson.somvis.som;

import java.util.ArrayList;
import java.util.List;


/**
 * One cell in a SOM.
 */
public class MapCell
{
    public static final int NEIGHBOR_COUNT = 6;
    
    private Vector value;
    private List<Vector> data;
    
    private MapCell[] neighbors;

    private int x;
    private int y;
    private double meanUnifiedDistance;
    
    /**
     * @param j 
     * @param i  */
    public MapCell(int n, int i, int j)
    {
        data = new ArrayList<>();
        value = new Vector(n);
        this.x = i;
        this.y = j;
        neighbors = new MapCell[NEIGHBOR_COUNT];
    }
    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public MapCell getNeighbor(int i)
    {
        return neighbors[i];
    }
    
    public void setNeighbor(int i, MapCell c)
    {
        neighbors[i] = c;
    }

    public Vector getValue()
    {
        return value;
    }

    public synchronized Vector getValueSync()
    {
        return new Vector(value);
    }
    
    public void setValue(Vector v)
    {
        value = new Vector(v);
    }

    public boolean hasData()
    {
        return data.size() > 0;
    }

    public List<Vector> getData()
    {
        return data;
    }

    public void reset()
    {
        value.scale(0.0);
        data.clear();
    }

    public void addData(Vector vec)
    {
        data.add(new Vector(vec));
    }

    public void learn(Vector v, double learningRate, double distanceFalloff)
    {
        Vector diff = v.sub(value).scale(learningRate * distanceFalloff);
        value.inc(diff);
    }

    public synchronized void learnSync(Vector v, double learningRate, double distanceFalloff)
    {
        Vector diff = v.sub(value).scale(learningRate * distanceFalloff);
        value.inc(diff);
    }

    public double getValue(Variable var)
    {
        if (var.equals(ClusteringVariable.INSTANCE))
        {
            return meanUnifiedDistance;
        }
        if (var.equals(CellWeightVariable.INSTANCE))
        {
            return data.size();
        }
        
        if (data.size() == 0)
        {
            return 0.0;
        }
        
        return value.get()[var.getIndex()];
    }

    public double getAvgValue(Variable var)
    {
        if (var.equals(ClusteringVariable.INSTANCE))
        {
            return meanUnifiedDistance;
        }
        if (var.equals(CellWeightVariable.INSTANCE))
        {
            return data.size();
        }
        
        int i = var.getIndex();
        double val = 0.0;
        for (Vector v : data)
        {
            val += v.get()[i];
        }
        return val / data.size();
    }
    
    /**
     * @return distance for U-matrix.
     */
    public double calculateMeanUnifiedDistance()
    {
        double distSum = 0.0;
        int neighborCount = 0;
        for (MapCell neigh : neighbors)
        {
            if (neigh != null)
            {
                distSum += getDistance(neigh);
                neighborCount++;
            }
        }
        return distSum / neighborCount;
    }

    private double getDistance(MapCell other)
    {
        return value.dist(other.value);
    }

    public double getMeanUnifiedDistance()
    {
        return meanUnifiedDistance;
    }

    public void setMeanUnifiedDistance(double d)
    {
        meanUnifiedDistance = d;
    }
}
