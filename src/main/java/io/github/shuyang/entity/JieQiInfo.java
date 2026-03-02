package io.github.shuyang.entity;

public class JieQiInfo {
    private final double longitude;
    private final int[] date;
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
