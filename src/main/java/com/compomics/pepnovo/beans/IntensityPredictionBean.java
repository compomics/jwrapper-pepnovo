package com.compomics.pepnovo.beans;

import com.compomics.util.experiment.identification.matches.IonMatch;

/**
 * This class is a
 */
public class IntensityPredictionBean {
    private int iRank;
    private IonMatch ionMatch;
    private double iMZ;
    private double iScore;
    private int iCharge = 1;

    public IntensityPredictionBean(int aRank, IonMatch ionMatch, double aMZ, double aScore, int aCharge) {
        iRank = aRank;
        this.ionMatch = ionMatch;
        iMZ = aMZ;
        iScore = aScore;
        iCharge = aCharge;
    }

    public IntensityPredictionBean() {
    }

    public int getRank() {
        return iRank;
    }

    public void setRank(int aRank) {
        iRank = aRank;
    }

    public IonMatch getIonMatch() {
        return ionMatch;
    }

    public void setIonMatch(IonMatch ionMatch) {
        this.ionMatch = ionMatch;
    }

    public double getMZ() {
        return iMZ;
    }

    public void setMZ(double aMZ) {
        iMZ = aMZ;
    }

    public double getScore() {
        return iScore;
    }

    public void setScore(double aScore) {
        iScore = aScore;
    }

    public int getCharge() {
        return iCharge;
    }

    public void setCharge(int aCharge) {
        iCharge = aCharge;
    }

}
