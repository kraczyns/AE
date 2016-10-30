package pl.edu.pwr.artificaleyeapp;

/**
 * Created by nieop on 29.04.2016.
 */
public class Parameters {
    private int id;
    private String name;
    private int width;
    private int height;
    private int lowThreshold;
    private int highThreshold;

    public int getId() {
        return id;
    }

    public void setId(int value) {
        this.id = value;
    }

    public String getName() { return name; }

    public void setName(String value) {this.name = value; }

    public int getWidth() {
        return width;
    }

    public void setWidth(int value) {
        this.width = value;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int value) {
        this.height = value;
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(int value) {
        this.lowThreshold = value;
    }

    public int getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(int value) {
        this.highThreshold = value;
    }

    public Parameters(){}

    public Parameters(String name, int width, int height, int lowThreshold, int highThreshold)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    public Parameters(int id, String name, int width, int height, int lowThreshold, int highThreshold)
    {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    @Override
    public String toString()
    {
        return name;
    }

}

