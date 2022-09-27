package com.timpage.app;

import java.util.Enumeration;

public class Transition implements Comparable<Transition> {

    private ChordMap dstMap;
    private float transitionScore;

    /**
     * Constructor for the Transition object
     * @param dstMap the chord moved to in the transition
     */
    public Transition(ChordMap dstMap) {
        this.dstMap = dstMap;
    }
    
    /**
     * Calculates the score for the transition from the given source chord fingering to 
     * the stored destination chord fingering.
     * @param srcMap The source chord fingering
     * @return the score for the transition
     */
    public float calculateTransitionScore(ChordMap srcMap) {
        transitionScore = 0;
        // movement on the neck between the two chords
        float neckMovement = Math.abs(dstMap.getAverageFret()-srcMap.getAverageFret());
        int matchingFingerings = 2*dstMap.size();
        Enumeration<Integer> e = srcMap.getFretting().keys();
        // add a 2 point penalty for fingering in the second chord, there isn't a note 
            // on the same string & fret in the first chord
        while (e.hasMoreElements()) {
            int s = e.nextElement();
            int f = srcMap.getFretting().get(s);
            if (dstMap.getFretting().containsKey(s)) {
                int dstFret = dstMap.getFretting().get(s);
                if (f == dstFret) {
                    matchingFingerings-=2;
                }
            }
        }
        int constDiff = 0;
        int stringMatches = 0;
        // determine if the two chords have the same shape - they are allowed to be in different positions on the neck
        for (int i=0; i<6; i++) {
            if (srcMap.getFretting().containsKey(i)) {
                if (dstMap.getFretting().containsKey(i)) {
                    if (srcMap.getFretting().get(i) != 0 || dstMap.getFretting().get(i) != 0) {
                        stringMatches++;
                        // the difference between the first and second chord's frets must be the same for each string
                        int diff = dstMap.getFretting().get(i) - srcMap.getFretting().get(i);
                        if (stringMatches == 1) {
                            constDiff = diff;
                        }
                        else {
                            if (diff != constDiff) {
                                stringMatches = 7;
                                break;
                            }
                        }
                    }
                }
                else {
                    stringMatches = 7;
                    break;
                }
            }
            else if (dstMap.getFretting().containsKey(i) && dstMap.getFretting().get(i) != 0) {
                stringMatches = 7;
                break;
            }
        }
        // sum all the transition penalties
        transitionScore = neckMovement + matchingFingerings + dstMap.getScore();
        // different shape between chords penalty. rewards chord changes that keep the same shape
        if (stringMatches == 0 || stringMatches == 7) {
            transitionScore+=2;
        }
        return transitionScore;
    }

    /**
     * Gets the transition score for movement between the two chords
     * @return the transition score
     */
    public float getTransitionScore() {
        return transitionScore;
    }

    /**
     * Gets the ChordMap for the chord moved to in the transition
     * @return the destination ChordMap
     */
    public ChordMap getDstMap() {
        return dstMap;
    }

    /**
     * Comparator for transition objects. 
     * Used to sort transitions in order of best to worst
     */
    @Override
    public int compareTo(Transition obj) {
        //sort in ascending order
        float diff = this.transitionScore-obj.getTransitionScore();
        if (diff > 0) { return 1; }
        else if (diff < 0) { return -1; }
        return 0;
    }
    
}
