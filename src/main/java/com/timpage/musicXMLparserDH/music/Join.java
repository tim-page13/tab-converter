package com.timpage.musicXMLparserDH.music;

public class Join {

    private String joinType;
    private int num;
    private String type;
    private int midiPitch;
    private Join matchingJoin;

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
