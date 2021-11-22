package com.timpage.app;

import java.util.ArrayList;

// import com.timpage.app.GuitarString;

/**
 * Guitar class 
 */
public class Guitar {
    private ArrayList<GuitarString> guitarStrings;


    /**
     * constructor sets default tuning to eBGDAE in MIDI pitch format
     */
    public Guitar() {
        guitarStrings = new ArrayList<GuitarString>();
        int[] gs = new int[] {64, 59, 55, 50, 45, 40};
        setGuitarStrings(gs);
    }

    /**
     * constructor sets guitar string tunings to specified MIDI pitches
     */
    public Guitar(int[] gs) {
        guitarStrings = new ArrayList<GuitarString>();
        setGuitarStrings(gs);
    }

    /**
     * creates the set of guitar strings
     * @param gs
     */
    private void setGuitarStrings(int[] gs) {
        for (int i = 0; i < gs.length; i++) {
            setGuitarString(gs[i]);
        }
    }

    /**
     * create a new guitar string with the specified open pitch, gs. 
     * @param gs the MIDI pitch for the open string being created
     */
    private void setGuitarString(int gs) {
        GuitarString guitarString = new GuitarString(gs);
        guitarStrings.add(guitarString);
    }

    /**
     * getter for the guitarStrings ArrayList
     * @return ArrayList of guitarString objects
     */
    public ArrayList<GuitarString> getGuitarStrings() {
        return guitarStrings;
    }

}
