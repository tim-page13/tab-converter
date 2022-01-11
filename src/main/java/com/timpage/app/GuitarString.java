package com.timpage.app;

import java.util.HashMap;

/**
 * Guitar String class to give the properties of the guitar string
 */
public class GuitarString {
    private int fret;
    private int frettedPitch;
    private int openMidiPitch;
    private String openPitch;
    private int openOctave;
    private int openAccidental;
    //todo use this as <position in piece, fret>?
    private HashMap<Integer, Integer> stringAssignments;

    /**
     * Constructor to set a new guitar string with a specified midi pitch
     * @param p the pitch for the new GuitarString object
     */
    public GuitarString(int op) {
       setopenPitch(op);
    }
    
    public void setOpenMidiPitch(int op) {
        setopenPitch(op);
    }

    public int getOpenMidiPitch() {
        return openMidiPitch;
    }

    public void setFrettedPitch(int fp) {
        frettedPitch = fp;
        fret = frettedPitch - openMidiPitch;
    }

    public int getFrettedPitch() {
        return frettedPitch;
    }

    public int getFret() {
        return fret;
    }
    
    public String getOpenPitch() {
        return openPitch;
    }

    public int getOpenOctave() {
        return openOctave;
    }

    public int getOpenAccidental() {
        return openAccidental;
    }

    private void setopenPitch(int omp) {
        openMidiPitch = omp;
        openOctave = omp/12 -1;
        openAccidental = 0;
        switch (omp%12) {
            case 1: openAccidental = 1;
            case 0: openPitch = "C";
                    break;
            case 3: openAccidental = 1;
            case 2: openPitch = "D";
                    break;
            case 4: openPitch = "E";
                    break;
            case 6: openAccidental = 1;
            case 5: openPitch = "F";
                    break;
            case 8: openAccidental = 1;
            case 7: openPitch = "G";
                    break;
            case 10: openAccidental = 1;
            case 9: openPitch = "A";
                    break;
            case 11: openPitch = "B";
        }
    }

}
