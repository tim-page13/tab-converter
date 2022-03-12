package com.timpage.app;

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

        transitionScore = neckMovement + srcMap.getScore() + dstMap.getScore();
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
