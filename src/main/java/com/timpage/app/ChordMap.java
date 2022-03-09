package com.timpage.app;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ChordMap object is used to store the possible frettings of a chord and its associated score
 */
public class ChordMap implements Comparable<ChordMap> {
    private Hashtable<Integer, Integer> frettings;
    // lower score is better
    private float score;
    
    /**
     * Constructor for the ChordMap object
     */
    public ChordMap() {
        frettings = new Hashtable<>();
    }

    /**
     * Copy Constructor for the ChordMap object. Used to make a deep copy of another ChordMap object
     * @param otherCM The other ChordMap object which is being copied
     */
    public ChordMap(ChordMap otherCM) {
        this.frettings = new Hashtable<>();
        this.frettings.putAll(otherCM.frettings);
        this.score = otherCM.getScore();
    }


    /**
     * Adds a string-fret assignment to the store
     * @param string The guitar string the note is assigned to
     * @param fret The string's fret the note is assigned to
     * @return the boolean for whether the fretting was successfully added to the store or not
     */
    public boolean addFretting(int string, int fret) {
        if (frettings.containsKey(string)) {
            return false;
        }
        // prune the assignment tree path if the stretch between frets exceeds 5
        Enumeration<Integer> e = frettings.keys();
        while (e.hasMoreElements()) {
            int s = e.nextElement();
            int f = frettings.get(s);
            if (f != 0 && Math.abs(f-fret) > 5) {
                return false;
            }
        }
        frettings.put(string, fret);
        return true;
    }

    /**
     * Removes a fretting assignment from the store
     * @param string The string value (key in the Hashtable) for the assignment to be removed
     */
    public void removeFretting(int string) {
        frettings.remove(string);
    }

    /**
     * A bulk allocation of string-fret assignments to be put in the store
     * @param frets the assignments to be put in the store
     */
    public void setFretting(Hashtable<Integer, Integer> frets) {
        frettings = frets;
        calculateScore();
    }

    /**
     * Gets the store of allocated frettings
     * @return the store of allocated string-fret assignments
     */
    public Hashtable<Integer, Integer> getFretting() {
        return frettings;
    }

    /**
     * Utility to tell if a guitar string has already been assigned a note. Used to prune paths in the chordToTab search tree
     * @param string the guitar string we want to know the assignment status of
     * @return the boolean of whether the string has been assigned a note or not
     */
    public boolean isStringTaken(int string) {
        return frettings.containsKey(string);
    }

    /**
     * Gets the score of the ChordMap's assignment
     * @return the score
     */
    public float getScore() {
        return score;
    }

    /**
     * Calculates a score for the quality of the chord's fretting assignment. A lower score is better.
     * The algorithm takes into account the number of frets used, the range between used frets, position on
     * the guitar neck, and whether the strings used are consecutive.
     * @return the calculated score
     */
    public float calculateScore() {
        // todo add a penalty for skipping strings
        int min = -1;
        int max = -1;
        Set<Integer> fretsUsed = new LinkedHashSet<Integer>();
        int cumulativeFrets = 0;
        float skippedStrings = 0;
        int counter = 0;
        Enumeration<Integer> e = frettings.keys();
    
        while (e.hasMoreElements()) {
            int string = e.nextElement();
            int fret = frettings.get(string);
            // the first string
            if (counter != 0) {
                // penalty applied for not using adjacent strings
                skippedStrings += counter-string;
            }
            counter = string-1;
            // open frets are discounted from range penalties 
            if (fret != 0) {
                fretsUsed.add(fret);
                cumulativeFrets += fret;
                if (min == -1 || fret < min) {
                    min = fret;
                }
                if (max == -1 || fret > max) {
                    max = fret;
                }
            }
        }
        int range = max-min;
        int numFretsUsed = fretsUsed.size();
        float neckPenalty = (float) Math.sqrt((double) cumulativeFrets/(double)numFretsUsed);
        skippedStrings = skippedStrings/2;

        score = range + numFretsUsed + neckPenalty + skippedStrings;
        return score;
    }

    /**
     * Utility to say how many strings have been used in the chord's fretting assignment so far
     * @return the number of strings that have been assigned frets
     */
    public int size() {
        return frettings.size();
    }

    @Override
    public int compareTo(ChordMap obj) {
        //sort in ascending order
        float diff = this.score-obj.getScore();
        if (diff > 0) { return 1; }
        else if (diff < 0) { return -1; }
        return 0;
    }

}
