package com.example.humanactivitydetection;

public class getProgress {

    private String walkTotal;
    private String runTotal;

    public getProgress() {
    }

    public getProgress(String walkTotal, String runTotal) {
        this.walkTotal = walkTotal;
        this.runTotal = runTotal;
    }

    public String getWalkTotal() {
        return walkTotal;
    }

    public void setWalkTotal(String walkTotal) {
        this.walkTotal = walkTotal;
    }

    public String getRunTotal() {
        return runTotal;
    }

    public void setRunTotal(String runTotal) {
        this.runTotal = runTotal;
    }
}


