package com.timpage.app;

import java.util.HashMap;

/**
 * Guitar String class to give the properties of the guitar string
 */
public class GuitarString {
    private int fret;
    private int frettedPitch;
    private int openPitch;
    //todo use this as <position in piece, fret>?
    private HashMap<Integer, Integer> stringAssignments;

    /**
     * Constructor to set a new guitar string with a specified midi pitch
     * @param p the pitch for the new GuitarString object
     */
    public GuitarString(int op) {
       openPitch = op;
    }
    
    public void setOpenMidiPitch(int op) {
        openPitch = op;
    }

    public int getOpenMidiPitch() {
        return openPitch;
    }

    public void setFrettedPitch(int fp) {
        frettedPitch = fp;
        fret = frettedPitch - openPitch;
    }

    public int getFrettedPitch() {
        return frettedPitch;
    }

    public int getFret() {
        return fret;
    }
}
