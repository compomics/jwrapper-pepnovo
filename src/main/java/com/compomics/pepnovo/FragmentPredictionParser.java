package com.compomics.pepnovo;

import com.compomics.pepnovo.beans.IntensityPredictionBean;
import com.compomics.pepnovo.beans.PeptideOutputBean;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a parses the FragmentIonPrediction output of pepnovo
 * <p/>
 * e.g.:
 * <p/>
 * <p/>
 * PepNovo+ Build 20101117
 * Copyright 2010, The Regents of the University of California. All Rights Reserved.
 * Created by Ari Frank (arf@cs.ucsd.edu)
 * Initializing models (this might take a few seconds)... Done.
 * Fragment tolerance : 0.5000
 * PM tolernace       : 2.5000
 * PTMs considered    : C+57:M+16
 * Predicting fragmentation for input file: input.txt
 * >> KENNYR	2
 * Rank	Ion	m/z	Score
 * 1	y:5	 695.31	3.313
 * 2	y:4	 566.27	2.881
 * 3	y:3	 452.23	0.902
 * 4	y:2	 338.18	0.837
 * 5	b-H2O:2	 240.14	0.464
 * 6	b:2	 258.15	0.379
 * 7	y2:5	 348.16	0.272
 * 8	y:1	 175.12	0.081
 */
public class FragmentPredictionParser {

    public static Set<PeptideOutputBean> parse(File aFile) throws IOException {
        BufferedReader lReader = Files.newReader(aFile, Charset.defaultCharset());
        HashSet<PeptideOutputBean> lOutputBeans = new HashSet<PeptideOutputBean>();
        String line = null;

        PeptideOutputBean lPeptideOutputBean = null;
        IntensityPredictionBean lIntensityPredictionBean = null;
        HashSet<IntensityPredictionBean> lIntensityPredictionBeans = null;

        boolean lFileHeader = true;

        while ((line = lReader.readLine()) != null) {
            if (line.indexOf(">>") == 0) {
                lFileHeader = false; // The lFileHeader has been finished.

                // finish previous PeptideOutputBean?
                if (lPeptideOutputBean != null) {
                    persistPeptideOutputBean(lOutputBeans, lPeptideOutputBean, lIntensityPredictionBeans);
                }

                // make a new PeptideOutputBean.
                lPeptideOutputBean = new PeptideOutputBean();
                lIntensityPredictionBeans = new HashSet<>();

                line = line.replaceAll(">", "").trim(); // remove leading characters
                String[] lElements = line.split("\t");

                lPeptideOutputBean.setPeptideSequence(lElements[0]);
                lPeptideOutputBean.setCharge(Integer.parseInt(lElements[1]));

            } else if (lFileHeader == true) {
                // do nothing with the fileheader
                continue;
            } else if (line.indexOf("Rank") == 0) {
                // ok, previous line was the sectionheader, this line is the columnnames
            } else if (line.equals("")) {
                // ok, skip blank line.
            } else {

                // now we are reading the predictions of the current PeptideOutputBean
                String[] lElements = line.split("\t");
                lIntensityPredictionBean = new IntensityPredictionBean();

                // parse the m/z value.
                double lMZ = Double.parseDouble(lElements[2]);

                // Parse the fragmention
                String[] lFragmentIonElements = lElements[1].split(":");
                int lIonNumber = Integer.parseInt(lFragmentIonElements[1]);
                IonMatch ionMatch = parseFragmentIon(lFragmentIonElements[0], lIonNumber, lMZ);

                // persist the values.
                lIntensityPredictionBean.setMZ(lMZ);
                lIntensityPredictionBean.setRank(Integer.parseInt(lElements[0]));
                lIntensityPredictionBean.setScore(Double.parseDouble(lElements[3]));
                lIntensityPredictionBean.setIonMatch(ionMatch);

                lIntensityPredictionBeans.add(lIntensityPredictionBean);

            }
        }

        // finished reading the file, fence post!
        persistPeptideOutputBean(lOutputBeans, lPeptideOutputBean, lIntensityPredictionBeans);

        // return the results.
        return lOutputBeans;

    }

    /**
     * Add IntensityPrediction beans to specified PeptideOutputBean, and combine with previous PeptideOutputBeans.
     *
     * @param aOutputBeans
     * @param aPeptideOutputBean
     * @param aIntensityPredictionBeans
     */
    private static void persistPeptideOutputBean(HashSet<PeptideOutputBean> aOutputBeans, PeptideOutputBean aPeptideOutputBean, HashSet<IntensityPredictionBean> aIntensityPredictionBeans) {
        // Set the predictions to the current PeptideOutputBean
        aPeptideOutputBean.setPredictionBeanSet(aIntensityPredictionBeans);
        // Add the previous PeptideOutputBean.
        aOutputBeans.add(aPeptideOutputBean);
    }

    /**
     * Private method handles the parsing from pepnovo iontypes to compomics-utilities PeptideFragmentIonType objects.
     *
     * @param aIonType
     * @return
     */
    private static IonMatch parseFragmentIon(String aIonType, int ionNumber, double mz) {
        PeptideFragmentIon lPeptideFragmentIon;
        int fragmentType = -1;
        ArrayList<NeutralLoss> neutralLosses = null;
        Charge charge = new Charge(Charge.PLUS, 1);

        if (aIonType.equals("b")) {
            fragmentType = PeptideFragmentIon.B_ION;
        } else if (aIonType.equals("y")) {
            fragmentType = PeptideFragmentIon.Y_ION;
        } else if (aIonType.equals("b-H2O")) {
            fragmentType = PeptideFragmentIon.B_ION;
            neutralLosses = new ArrayList<>();
            neutralLosses.add(NeutralLoss.H2O);
        } else if (aIonType.equals("y-H2O")) {
            fragmentType = PeptideFragmentIon.Y_ION;
            neutralLosses = new ArrayList<>();
            neutralLosses.add(NeutralLoss.H2O);
        } else if (aIonType.equals("b-NH3")) {
            fragmentType = PeptideFragmentIon.B_ION;
            neutralLosses = new ArrayList<>();
            neutralLosses.add(NeutralLoss.NH3);
        } else if (aIonType.equals("y-NH3")) {
            fragmentType = PeptideFragmentIon.Y_ION;
            neutralLosses = new ArrayList<>();
            neutralLosses.add(NeutralLoss.NH3);
        } else if (aIonType.equals("b2")) {
            fragmentType = PeptideFragmentIon.B_ION;
            charge = new Charge(Charge.PLUS, 2);
        } else if (aIonType.equals("y2")) {
            fragmentType = PeptideFragmentIon.Y_ION;
            charge = new Charge(Charge.PLUS, 2);
        }

        lPeptideFragmentIon = new PeptideFragmentIon(fragmentType, ionNumber, mz, neutralLosses);

        return new IonMatch(null, lPeptideFragmentIon, charge);
    }
}
