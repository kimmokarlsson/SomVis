package com.github.kimmokarlsson.somvis.som;

/**
 * 
 */
public class Vector
{
    private double[] val;
    
    public Vector(int n)
    {
        val = new double[n];
    }

    public Vector(double[] d)
    {
        this(d.length);
        System.arraycopy(d, 0, val, 0, d.length);
    }
    
    public Vector(Vector v) {
        this(v.val);
    }

    public Vector scale(double d)
    {
        for (int i = 0; i < val.length; i++)
        {
            val[i] *= d;
        }
        return this;
    }

    public Vector mul(double d)
    {
        Vector n = new Vector(val.length);
        for (int i = 0; i < val.length; i++)
        {
            n.val[i] *= d;
        }
        return n;
    }

    public Vector inc(Vector t)
    {
        assert (val.length == t.val.length);
        
        for (int i = 0; i < val.length; i++)
        {
            val[i] += t.val[i];
        }
        return this;
    }

    public Vector add(Vector t)
    {
        assert (val.length == t.val.length);
        
        Vector n = new Vector(val);
        for (int i = 0; i < val.length; i++)
        {
            n.val[i] += t.val[i];
        }
        return n;
    }

    public Vector sub(Vector t)
    {
        assert (val.length == t.val.length);
        
        Vector n = new Vector(val);
        for (int i = 0; i < val.length; i++)
        {
            n.val[i] -= t.val[i];
        }
        return n;
    }

    public double length()
    {
        double d = 0.0;
        for (int i = 0; i < val.length; i++)
        {
            double f = val[i];
            d += f*f;
        }
        return Math.sqrt(d);
    }

    public double dist(Vector v)
    {
        assert (val.length == v.val.length);
        
        double d = 0.0;
        for (int i = 0; i < val.length; i++)
        {
            double f = val[i] - v.val[i];
            d += f*f;
        }
        
        return Math.sqrt(d);
    }

    public Vector normalize()
    {
        double d = length();
        for (int i = 0; i < val.length; i++)
        {
            val[i] /= d;
        }
        return this;
    }

    public double[] get()
    {
        return val;
    }

    public int dim()
    {
        return val.length;
    }
}
