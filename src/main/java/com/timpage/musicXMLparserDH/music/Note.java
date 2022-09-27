package com.timpage.musicXMLparserDH.music;

import java.util.ArrayList;


/**
 * Initially created by dorien on 10/11/14. Modified by Tim Page
 */
public class Note {

    private Integer measure;
    private Integer startTime;
    private Integer duration;
    private String pitch;     //Z is for rest
    private Integer voice;
    private String accidental;
    private Integer accidentalInt;
    private Integer counter;
    private Integer midiPitch;
    private Integer octave;
    private Integer staff;
    // --TP-- used for guitar notes
    private Integer stringNo;
    private Integer fretNo;
    private ArrayList<Join> joins;

    public void setMeasure(Integer measure) {
        this.measure = measure;
    }

    public Integer getMeasure() {
        return measure;
    }

    public Note(Integer id, String nonparsed) {
        this.staff = 1;
    }

    public Note(Integer startTime) {
        this.startTime = startTime;
        this.accidental = "";
        this.midiPitch = null;
        this.accidentalInt = 0;
        this.joins = new ArrayList<>();
    }

    public void setStaff(Integer staff) {
        this.staff = staff;
    }

    public Integer getStaff() {
        if (this.staff == 1 || this.staff == 2 ){
            return staff;
        }
        else{
            System.out.println("note with no staff"+staff);
            return 0;

        }
    }

    public Integer getDuration() {
        return duration;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Integer getCounter() {
        return counter;
    }

    public Integer getVoice() {
        return voice;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public String getPitch() {
        return pitch;
    }

    public void setAccidentalInt(Integer acc){
        this.accidentalInt = acc;
    }


    public void setAccidental(String accidental) {
        this.accidental =  accidental;
    }

    public String getAccidental() {

        return accidental;
    }

    public Integer getMidiPitch() {
        return midiPitch;
    }

    public void setMidiPitch(Integer midiPitch) {
        this.midiPitch = midiPitch;
    }

    public Integer getOctave() {
        return octave;
    }

    public void setOctave(Integer octave) {
        this.octave = octave;
    }

    public void calculateMidiPitch() {

        Integer baseMidi = 0;
        if (this.pitch.equals("C")){
            baseMidi = 0;
        }
        else if(this.pitch.equals("D")){
            baseMidi = 2;
        }
        else if(this.pitch.equals("E")){
            baseMidi = 4;
        }
        else if(this.pitch.equals("F")){
            baseMidi = 5;
        }
        else if(this.pitch.equals("G")){
            baseMidi = 7;
        }
        else if(this.pitch.equals("A")){
            baseMidi = 9;
        }
        else if(this.pitch.equals("B")) {
            baseMidi = 11;
        }

        if (!this.pitch.equals("Z")){
            baseMidi = baseMidi + this.accidentalInt;
            this.midiPitch = baseMidi + ( 12 * (this.octave + 1));
        }

    }

    public Integer getFretNo() {
        return fretNo;
    }
    
    public void setFretNo(Integer fret) {
        fretNo = fret;
    }

    public Integer getStringNo() {
        return stringNo;
    }

    public void setStringNo(Integer sn) {
        stringNo = sn;
    }

    public void addJoin(String joinType, int num, String type, int midiPitch) {
        Join newJoin = new Join(joinType, num, type, midiPitch);
        joins.add(newJoin);
    }

    public ArrayList<Join> getJoins() {
        return joins;
    }



}
