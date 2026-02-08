package io.github.shuyang;

public class JieQiInfo {
    private double longitude;
    private int[] date;
    public JieQiInfo(double longitude,int[] date){
        this.longitude = longitude;
        this.date = date;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public int[] getDate(){
        return this.date;
    }
    public String toString(){
        return "this.longitude = " + this.longitude + " , " + "this.date = " + this.date[2];
    }
}
