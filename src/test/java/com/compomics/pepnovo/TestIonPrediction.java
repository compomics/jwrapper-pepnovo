package com.compomics.pepnovo;

import com.compomics.pepnovo.beans.IntensityPredictionBean;
import com.compomics.pepnovo.beans.PeptideInputBean;
import com.compomics.pepnovo.beans.PeptideOutputBean;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is a
 */

public class TestIonPrediction extends TestCase {
    /**
     * Create TestIonPrediction
     *
     * @param testName name of the test case
     */
    public TestIonPrediction(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TestIonPrediction.class);
    }

    /**
     * Main test.
     */
    public void testMain() {
        HashSet<PeptideInputBean> lPeptideInputBeans = new HashSet<PeptideInputBean>();
        lPeptideInputBeans.add(new PeptideInputBean("KENNYR", 2));
        lPeptideInputBeans.add(new PeptideInputBean("DATSPLR", 1));

        Set<PeptideOutputBean> lPeptideOutputBeans = FragmentPredictionTask.predictFragmentIons(lPeptideInputBeans);
        for (PeptideOutputBean lOutputBean : lPeptideOutputBeans ){
            // simple testing!
            if(lOutputBean.getPeptideSequence().equals("KENNYR")){
                Assert.assertEquals(lOutputBean.getPredictionBeanSet().size(), 8);
                Assert.assertEquals(lOutputBean.getCharge(), 2);
                Set<IntensityPredictionBean> lPredictionBeanSet = lOutputBean.getPredictionBeanSet();
                for (IntensityPredictionBean lPredictionBean : lPredictionBeanSet) {
                    if(lPredictionBean.getRank() == 1){
                        PeptideFragmentIon peptideFragmentIon = (PeptideFragmentIon) lPredictionBean.getIonMatch().ion;
                        Assert.assertEquals(5, peptideFragmentIon.getNumber());
                        Assert.assertEquals(PeptideFragmentIon.Y_ION, peptideFragmentIon.getSubType());
                    }
                    if(lPredictionBean.getRank() == 7){
                        PeptideFragmentIon peptideFragmentIon = (PeptideFragmentIon) lPredictionBean.getIonMatch().ion;
                        Assert.assertEquals(5, peptideFragmentIon.getNumber());
                        Assert.assertEquals(PeptideFragmentIon.Y_ION, peptideFragmentIon.getSubType());
                    }
                }

            }

            if(lOutputBean.getPeptideSequence().equals("DATSPLR")){
                Assert.assertEquals(lOutputBean.getPredictionBeanSet().size(), 8);
                Assert.assertEquals(lOutputBean.getCharge(), 1);

                Set<IntensityPredictionBean> lPredictionBeanSet = lOutputBean.getPredictionBeanSet();
                for (IntensityPredictionBean lPredictionBean : lPredictionBeanSet) {
                    if(lPredictionBean.getRank() == 5){
                        PeptideFragmentIon peptideFragmentIon = (PeptideFragmentIon) lPredictionBean.getIonMatch().ion;
                        Assert.assertEquals(6, peptideFragmentIon.getNumber());
                        Assert.assertEquals(PeptideFragmentIon.Y_ION, peptideFragmentIon.getSubType());
                    }
                }

            }
        }
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        //
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
