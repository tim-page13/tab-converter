package com.timpage.musicXMLparserDH.music;

/**
 * Join object - for handling ties, slides, hammer-ons, and pull-offs
 */
public class Join {

    private String joinType;
    private int num;
    private String type;
    private int midiPitch;
    private Join matchingJoin;

    /**
     * Join object constructor
     * @param joinType The type of join. One of: tie, slide, hammer-on, pull-off
     * @param num Join ID, neeeded since there can be multiple at once
     * @param type "start" or "stop"
     * @param midiPitch The MIDI pitch of the note this end of the join
     */
    public Join(String joinType, int num, String type, int midiPitch) {
        this.joinType = joinType;
        this.num = num;
        this.type = type;
        this.midiPitch = midiPitch;
        this.matchingJoin = null;
    }

    public String getJoinType() {
        return joinType;
    }

    public int getNum() {
        return num;
    }

    public String getType() {
        return type;
    }

    public int getMidiPitch() {
        return midiPitch;
    }

    public void setMatchingJoin(Join matchingJoin) {
        this.matchingJoin = matchingJoin;
    }

    public Join getMatchingJoin() {
        return matchingJoin;
    }

    
}
