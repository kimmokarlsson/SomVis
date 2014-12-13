package com.github.kimmokarlsson.somvis.som;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The SOM.
 */
public class SelfOrganizingMap
{
    private MapCell[][] cell;
    private final List<Variable> variables;
    
    private int iterationCount = 1000;
    private double initialLearningRate = 0.1;
    private double clusterThreshold = 0.12;
    private final StringValueRegistry registry;
    private boolean useRandomSamples;
    private boolean multiThread;

    /**
     * 
     * @param c
     * @param r
     * @param vars
     * @param registry
     */
    public SelfOrganizingMap(int c, int r, List<Variable> vars, StringValueRegistry registry)
    {
        this.registry = registry;
        variables = vars;
        int n = vars.size();
        cell = new MapCell[r][c];
        
        for (int j = 0; j < r; j++)
        {
            for (int i = 0; i < c; i++)
            {
                cell[j][i] = new MapCell(n, i, j);
            }
        }
        for (int j = 0; j < r; j++)
        {
            for (int i = 0; i < c; i++)
            {
                int diff = (j % 2 == 0) ? -1 : +1;
                
                cell[j][i].setNeighbor(0, getCell(i+diff, j-1));
                cell[j][i].setNeighbor(1, getCell(i, j-1));
                cell[j][i].setNeighbor(2, getCell(i-1, j));
                cell[j][i].setNeighbor(3, getCell(i+1, j));
                cell[j][i].setNeighbor(4, getCell(i+diff, j+1));
                cell[j][i].setNeighbor(5, getCell(i, j+1));
            }
        }
    }

    public List<Variable> getVariables()
    {
        return variables;
    }

    public boolean isMultiThread()
    {
        return multiThread;
    }

    public void setMultiThread(boolean multiThread)
    {
        this.multiThread = multiThread;
    }

    public boolean isUseRandomSamples()
    {
        return useRandomSamples;
    }

    public void setUseRandomSamples(boolean useRandomSamples)
    {
        this.useRandomSamples = useRandomSamples;
    }

    public double getClusterThreshold()
    {
        return clusterThreshold;
    }

    public void setClusterThreshold(double d)
    {
        clusterThreshold = d;
    }

    public void setIterationCount(int i)
    {
        iterationCount = i;
    }

    public void setInitialLearningRate(double d)
    {
        initialLearningRate = d;
    }

    public StringValueRegistry getRegistry()
    {
        return registry;
    }

    public MapCell getCell(int i, int j)
    {
        if (i >= 0 && i < cell[0].length && j >= 0 && j < cell.length)
        {
            return cell[j][i];
        }
        return null;
    }

    /**
     * 
     */
    public void reset()
    {
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[j].length; i++)
            {
                cell[j][i].reset();
            }
        }
    }

    public void initCell(int i, int j, double[] v)
    {
        cell[j][i].reset();
        cell[j][i].setValue(new Vector(v));
    }

    public void initCells(List<Vector> inputs)
    {
        Random random = new Random();
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[j].length; i++)
            {
                cell[j][i].reset();
                cell[j][i].setValue(getRandomValuesVector(random));
            }
        }
        
        if (useRandomSamples)
        {
            randomSample(inputs, random);
        }
    }
    
    private void randomSample(List<Vector> inputs, Random random)
    {
        for (int j = 0; j < cell.length; j+=5)
        {
            for (int i = 0; i < cell[j].length; i+=5)
            {
                cell[j][i].setValue(normalize(inputs.get(random.nextInt(inputs.size()))));
            }
        }
    }

    private Vector getRandomValuesVector(Random random)
    {
        double[] d = new double[variables.size()];
        int i = 0;
        for (Variable v : variables)
        {
            d[i++] = v.getRandomValue(random);
        }
        return normalize(new Vector(d));
    }

    public MapCell getNearest(Vector v)
    {
        MapCell near = null;
        double minDist = Integer.MAX_VALUE;
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[0].length; i++)
            {
                double dist = getDistance( cell[j][i].getValue(), v );
                if (dist < minDist)
                {
                    minDist = dist;
                    near = cell[j][i];
                }
            }
        }
        return near;
    }

    public MapCell getNearestSync(Vector v)
    {
        MapCell near = null;
        double minDist = Integer.MAX_VALUE;
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[0].length; i++)
            {
                double dist = getDistance( cell[j][i].getValueSync(), v );
                if (dist < minDist)
                {
                    minDist = dist;
                    near = cell[j][i];
                }
            }
        }
        return near;
    }

    private double getDistance(Vector v1, Vector v2)
    {
        Vector u1 = new Vector(v1);
        Vector u2 = new Vector(v2);
        
        int i = 0;
        for (Variable var : variables)
        {
            if (var.isIgnored())
            {
                u1.get()[i] = 0;
                u2.get()[i] = 0;
            }
            i++;
        }
        
        return u1.dist(u2);
    }

    public void learn(List<Vector> inputs)
    {
        double latticeRadius = Math.max(getRows(), getCols())/2;
        double timeConstant = iterationCount / Math.log(latticeRadius);
        double learningRate = initialLearningRate;

        System.out.println();
        long startTime = System.currentTimeMillis();
        long iterStartTime = startTime;
        for (int iter = 0; iter < iterationCount; iter++)
        {
            double neighborhoodRadius = latticeRadius * Math.exp(-iter/timeConstant);
            double nbRadSquared = neighborhoodRadius * neighborhoodRadius;
            if (iter % 10 == 0 && iter > 0)
            {
                long iterEndTime = System.currentTimeMillis();
                long singleIter = (iterEndTime-iterStartTime)/10;
                long remTime = ((iterationCount-iter)*(iterEndTime-iterStartTime)/10)/1000;
                iterStartTime = iterEndTime;
                System.out.print("Starting iteration: "+iter +", avg iter: "+ singleIter + " ms, time remaining: "+ remTime +" s, rate="+ toSmallDoubleString(learningRate) +", rad="+ toSmallDoubleString(neighborhoodRadius) +"\r");
            }
            
            Collections.shuffle(inputs);
            for (Vector vec : inputs)
            {
                Vector nv = normalize(vec);
                MapCell bmu = getNearest(nv);
                if (bmu != null)
                {
                    for (int y = 0; y < getRows(); y++)
                    {
                        for (int x = 0; x < getCols(); x++)
                        {
                            int xd = x-bmu.getX();
                            int yd = y-bmu.getY();
                            MapCell c = getCell(x, y);
                            
                            double distSq = xd*xd+yd*yd;
                            if (distSq < nbRadSquared)
                            {
                                double distFalloff = Math.exp(-(distSq)/(2 * nbRadSquared));
                                c.learn(nv, learningRate, distFalloff);
                            }
                        }
                    }
                }
            }
            learningRate = initialLearningRate *
                    Math.exp(-(double)iter/iterationCount);
            if (learningRate < 0.01)
            {
                System.out.println("\nLearning rate dropped below threshold, stopping at "+ iter);
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        for (Vector vec : inputs)
        {
            Vector nv = normalize(vec);
            MapCell bmu = getNearest(nv);
            if (bmu != null)
            {
                bmu.addData(vec);
            }
        }
        System.out.println("\nLearned through "+ iterationCount +" iterations in " + ((endTime-startTime)/1000) + " s.");
    }

    /**
     * Run 4 threads at 1/4 iteration slices.
     * @param inputs input data
     */
    public void concurrentLearn(List<Vector> inputs)
    {
        long startTime = System.currentTimeMillis();
        Thread t1 = new Thread(new LearningRun(new ArrayList<>(inputs), 0, 4));
        Thread t2 = new Thread(new LearningRun(new ArrayList<>(inputs), 1, 4));
        Thread t3 = new Thread(new LearningRun(new ArrayList<>(inputs), 2, 4));
        Thread t4 = new Thread(new LearningRun(new ArrayList<>(inputs), 3, 4));
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        
        try
        {
            t1.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            t2.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            t3.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        try
        {
            t4.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        for (Vector vec : inputs)
        {
            Vector nv = normalize(vec);
            MapCell bmu = getNearest(nv);
            if (bmu != null)
            {
                bmu.addData(vec);
            }
        }
        System.out.println("\nLearned through "+ iterationCount +" iterations in " + ((endTime-startTime)/1000) + " s.");
    }

    private class LearningRun implements Runnable
    {
        private final List<Vector> inputs;
        private final int startIter;
        private final int step;
        public LearningRun(List<Vector> inputs, int startIter, int step)
        {
            this.inputs = inputs;
            this.startIter = startIter;
            this.step = step;
        }
        @Override
        public void run()
        {
            parallelLearn(inputs, startIter, step);
        }
    }

    private static String toSmallDoubleString(double d)
    {
        String s = Double.toString(d);
        return s.substring(0, Math.min(4, s.length()));
    }

    private void parallelLearn(List<Vector> inputs, int startIter, int step)
    {
        double latticeRadius = Math.max(getRows(), getCols())/2;
        double timeConstant = iterationCount / Math.log(latticeRadius);
        double learningRate = initialLearningRate;

        long startTime = System.currentTimeMillis();
        long iterStartTime = startTime;
        for (int iter = startIter; iter < iterationCount; iter+=step)
        {
            double neighborhoodRadius = latticeRadius * Math.exp(-iter/timeConstant);
            double nbRadSquared = neighborhoodRadius * neighborhoodRadius;
            if (iter % 10 == startIter && iter > step)
            {
                long iterEndTime = System.currentTimeMillis();
                long singleIter = (iterEndTime-iterStartTime)/10;
                long remTime = ((iterationCount-iter)*(iterEndTime-iterStartTime)/10)/1000;
                iterStartTime = iterEndTime;
                System.out.println("Starting iteration: "+iter +", avg iter: "+ singleIter + " ms, time remaining: "+ remTime +" s, rate="+ toSmallDoubleString(learningRate) +", rad="+ toSmallDoubleString(neighborhoodRadius));
            }
            
            Collections.shuffle(inputs);
            for (Vector vec : inputs)
            {
                Vector nv = normalize(vec);
                MapCell bmu = getNearestSync(nv);
                if (bmu != null)
                {
                    for (int y = 0; y < getRows(); y++)
                    {
                        for (int x = 0; x < getCols(); x++)
                        {
                            int xd = x-bmu.getX();
                            int yd = y-bmu.getY();
                            MapCell c = getCell(x, y);
                            
                            double distSq = xd*xd+yd*yd;
                            if (distSq < nbRadSquared)
                            {
                                double distFalloff = Math.exp(-(distSq)/(2 * nbRadSquared));
                                c.learnSync(nv, learningRate, distFalloff);
                            }
                        }
                    }
                }
            }
            learningRate = initialLearningRate *
                    Math.exp(-(double)iter/iterationCount);
            if (learningRate < 0.01)
            {
                System.out.println("\nLearning rate of ("+startIter+") dropped below threshold, stopping at "+ iter);
                break;
            }
        }
    }
    
    private Vector normalize(Vector vec)
    {
        return normalizeComp(vec).normalize();
    }
    
    private Vector normalizeComp(Vector vec)
    {
        double[] inp = vec.get();
        double[] d = new double[vec.dim()];
        int i = 0;
        for (Variable var : variables)
        {
            d[i] = var.getNormalizedValue(inp[i]);
            i++;
        }
        return new Vector(d);
    }

    public int getRows()
    {
        return cell.length;
    }

    public int getCols()
    {
        return cell[0].length;
    }

    public double[][] getUnifiedDistanceMatrix(boolean normalized, boolean cacheResult)
    {
        double[][] umat = new double[cell.length][cell[0].length];
        
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[0].length; i++)
            {
                umat[j][i] = cell[j][i].calculateMeanUnifiedDistance();
            }        
        }
        
        if (normalized)
        {
            normalize(umat);
        }

        if (cacheResult) {
            for (int j = 0; j < cell.length; j++)
            {
                for (int i = 0; i < cell[0].length; i++)
                {
                    cell[j][i].setMeanUnifiedDistance(umat[j][i]);
                }
            }
        }
        
        return umat;
    }

    private void normalize(double[][] umat)
    {
        double maxValue = 0.0;
        double minValue = Integer.MAX_VALUE;
        for (int j = 0; j < umat.length; j++)
        {
            for (int i = 0; i < umat[0].length; i++)
            {
                double v = umat[j][i];
                maxValue = Math.max(maxValue, v);
                minValue = Math.min(minValue, v);
            }
        }
        
        for (int j = 0; j < umat.length; j++)
        {
            for (int i = 0; i < umat[0].length; i++)
            {
                double v = umat[j][i];
                double norm = (v - minValue) / (maxValue - minValue);
                umat[j][i] = norm;
            }
        }
    }
    
    public int getMaxCellWeight() {
        int maxW = 0;
        for (int j = 0; j < cell.length; j++)
        {
            for (int i = 0; i < cell[0].length; i++)
            {
                maxW = Math.max(maxW, cell[j][i].getData().size());
            }
        }
        return maxW;
    }
}
