package com.timpage.app;

import java.util.Enumeration;

public class Transition implements Comparable<Transition> {

    private ChordMap srcMap;
    private ChordMap dstMap;
    private float transitionScore;

    public Transition(ChordMap srcMap, ChordMap dstMap) {
        this.srcMap = srcMap;
        this.dstMap = dstMap;
    }
    public Transition(ChordMap dstMap) {
        this.dstMap = dstMap;
    }

    public float calculateTransitionScore() {
        transitionScore = 0;
        float neckMovement = Math.abs(dstMap.getAverageFret()-srcMap.getAverageFret());

        transitionScore = neckMovement + srcMap.getScore() + dstMap.getScore();
        return transitionScore;
    }
    
    public float calculateTransitionScore(ChordMap srcMap) {
        transitionScore = 0;
        float neckMovement = Math.abs(dstMap.getAverageFret()-srcMap.getAverageFret());
        int matchingFingerings = 2*dstMap.size();
        Enumeration<Integer> e = srcMap.getFretting().keys();
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
        for (int i=0; i<6; i++) {
            if (srcMap.getFretting().containsKey(i)) {
                if (dstMap.getFretting().containsKey(i)) {
                    if (srcMap.getFretting().get(i) != 0 || dstMap.getFretting().get(i) != 0) {
                        stringMatches++;
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
        
        transitionScore = neckMovement + matchingFingerings + dstMap.getScore();
        // different shape between chords penalty. rewards chord changes that keep the same shape
        if (stringMatches == 0 || stringMatches == 7) {
            transitionScore++;
        }
        return transitionScore;
    }

    public float getTransitionScore() {
        return transitionScore;
    }

    public ChordMap getDstMap() {
        return dstMap;
    }

    @Override
    public int compareTo(Transition obj) {
        //sort in ascending order
        float diff = this.transitionScore-obj.getTransitionScore();
        if (diff > 0) { return 1; }
        else if (diff < 0) { return -1; }
        return 0;
    }
    
}
