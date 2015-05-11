package com.ap.brecht.guitool;

/**
 * Created by Brecht on 1/04/2015.
 */
public class AcceleroFilter {
    private final double S1_Gain0 = 0.019370459361914474;
    private final double S1_Gain2 = -0.83687937305767479;
    private final double S1_Gain3 = 1.7593975356100171;
    private final double S1_Gain4 = 2;

    private double S1B1_Unit1 = 0;
    private double S1B1_Unit2 = 0;
    private double S1B2_Unit0 = 0;
    private double S1B2_Unit1 = 0;
    private double S1B2_Unit2 = 0;
    private double S1B3_Unit0 = 0;
    private double S1B3_Unit1 = 0;
    private double S1B3_Unit2 = 0;
    private double S1B1_Output;
    private double S1B2_Output;
    private double S1B3_Output;
    private double S1_Output;

    private final double S2_Gain0 = 0.01711220663941166;
    private final double S2_Gain2 = -0.62273174921388086;
    private final double S2_Gain3 = 1.5542829226562345;
    private final double S2_Gain4 = 2;

    private double S2B1_Unit1 = 0;
    private double S2B1_Unit2 = 0;
    private double S2B2_Unit0 = 0;
    private double S2B2_Unit1 = 0;
    private double S2B2_Unit2 = 0;
    private double S2B3_Unit0 = 0;
    private double S2B3_Unit1 = 0;
    private double S2B3_Unit2 = 0;
    private double S2B1_Output;
    private double S2B2_Output;
    private double S2B3_Output;
    private double S2_Output;

    private final double S3_Gain0 = 0.12799483651485619;
    private final double S3_Gain3 = 0.74401032697028757;

    private double S3B1_Unit1 = 0;
    private double S3B1_Unit2 = 0;
    private double S3B2_Unit0 = 0;
    private double S3B2_Unit1 = 0;
    private double S3B2_Unit2 = 0;
    private double S3B3_Unit0 = 0;
    private double S3B3_Unit1 = 0;
    private double S3B3_Unit2 = 0;
    private double S3B1_Output;
    private double S3B2_Output;
    private double S3B3_Output;
    private double S3_Output;

    public double Filter(float input)
    {
        SectionOne(input);
        SectionTwo();
        return SectionThree();
    }

    private void SectionOne(float Input)
    {
        //Section 1:
        //Block 1
        S1B1_Output = (Input * S1_Gain0) + (S1B1_Unit1 * S1_Gain2) + (S1B1_Unit2 * S1_Gain3);
        S1B1_Unit1 = S1B1_Unit2;
        S1B1_Unit2 = S1B1_Output;
        //Block 2
        S1B2_Output = (S1B2_Unit0 * S1_Gain4) + (S1B2_Unit1 * S1_Gain2) + (S1B2_Unit2 * S1_Gain3);
        S1B2_Unit1 = S1B2_Unit2;
        S1B2_Unit2 = S1B2_Output;
        S1B2_Unit0 = Input * S1_Gain0;
        //Block 3
        S1B3_Output = (S1B3_Unit0) + (S1B3_Unit1 * S1_Gain2) + (S1B3_Unit2 * S1_Gain3);
        S1B3_Unit1 = S1B3_Unit2;
        S1B3_Unit2 = S1B3_Output;
        S1B3_Unit0 = S1B2_Unit0;
        //Total
        S1_Output= S1B1_Output + S1B2_Output + S1B3_Output;
    }

    private void SectionTwo()
    {
        //Section 2:
        //Block 1
        S2B1_Output = (S1_Output * S2_Gain0) + (S2B1_Unit1 * S2_Gain2) + (S2B1_Unit2 * S2_Gain3);
        S2B1_Unit1 = S2B1_Unit2;
        S2B1_Unit2 = S2B1_Output;
        //Block 2
        S2B2_Output = (S2B2_Unit0 * S2_Gain4) + (S2B2_Unit1 * S2_Gain2) + (S2B2_Unit2 * S2_Gain3);
        S2B2_Unit1 = S2B2_Unit2;
        S2B2_Unit2 = S2B2_Output;
        S2B2_Unit0 = S1_Output * S2_Gain0;
        //Block 3
        S2B3_Output = S2B3_Unit0 + (S2B3_Unit1 * S2_Gain2) + (S2B3_Unit2 * S2_Gain3);
        S2B3_Unit1 = S2B3_Unit2;
        S2B3_Unit2 = S2B3_Output;
        S2B3_Unit0 = S2B2_Unit0;
        //Total
        S2_Output= S2B1_Output + S2B2_Output + S2B3_Output;
    }

    private double SectionThree()
    {
        //Section 3:
        //Block 1
        S3B1_Output = (S2_Output * S3_Gain0) + (S3B1_Unit2 * S3_Gain3);
        S3B1_Unit1 = S3B1_Unit2;
        S3B1_Unit2 = S3B1_Output;
        //Block 2
        S3B2_Output = S3B2_Unit0 + (S3B2_Unit2 * S3_Gain3);
        S3B2_Unit1 = S3B2_Unit2;
        S3B2_Unit2 = S3B2_Output;
        S3B2_Unit0 = S2_Output * S3_Gain0;
        //Block 3
        S3B3_Output =  S3B3_Unit2 * S3_Gain3;
        S3B3_Unit1 = S3B3_Unit2;
        S3B3_Unit2 = S3B3_Output;
        S3B3_Unit0 = S3B2_Unit0;
        //Total
        S3_Output= S3B1_Output + S3B2_Output + S3B3_Output;
        //Filtered accelerometer value = S3_Output

        return S3_Output;
    }
}
