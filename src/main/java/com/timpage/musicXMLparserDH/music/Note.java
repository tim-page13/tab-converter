package com.timpage.musicXMLparserDH.music;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dorien on 10/11/14.
 */



public class Note {

    private Integer id;
    private Integer measure;

    public void setMeasure(Integer measure) {
        this.measure = measure;
    }

    public Integer getMeasure() {

        return measure;
    }

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
    private Integer referenceNoteID;
    private Integer referenceChange;
    // --TP-- used for guitar notes
    private Integer stringNo;
    private Integer fretNo;
    private ArrayList<Join> joins;

    public void setReferenceChange(Integer referenceChange) {
        this.referenceChange = referenceChange;
    }

    public void setReferenceNoteID(Integer referenceNoteID) {
        this.referenceNoteID = referenceNoteID;
    }

    private ArrayList<Integer> durationSet = new ArrayList<Integer>();

    public Integer getReferenceNoteID() {
        return referenceNoteID;
    }

    public Integer getReferenceChange() {
        return referenceChange;
    }
    public Note(Integer id, String nonparsed) {

        this.id = id;
        this.staff = 1;


        //fill duration set
        durationSet.add(2);
        durationSet.add(4);
        durationSet.add(8);

        //default values
        referenceNoteID = 0;
        referenceChange = 0;

    }

    public Note(Integer startTime) {
        this.startTime = startTime;
        this.accidental = "";
        this.midiPitch = null;
        this.accidentalInt = 0;

        this.referenceNoteID = 0;
        this.referenceChange = 0;

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

    public void setRandom(){

        Random random = new Random();

        int index = random.nextInt(durationSet.size());

        this.duration = durationSet.get(index);


    }

    public Integer getDuration() {
        return duration;
    }

    public ArrayList<Integer> getDurationSet() {
        return durationSet;
    }

    public void setId(int id) {
        this.id = id;
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

        }else if(this.pitch.equals("E")){
            baseMidi = 4;
        }else if(this.pitch.equals("F")){
            baseMidi = 5;
        }else if(this.pitch.equals("G")){
            baseMidi = 7;
        }else if(this.pitch.equals("A")){
            baseMidi = 9;
        }else if(this.pitch.equals("B")) {
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

    public Integer getId() {
        return id;
    }

    public void addJoin(String joinType, int num, String type, int midiPitch) {
        Join newJoin = new Join(joinType, num, type, midiPitch);
        joins.add(newJoin);
    }

    public ArrayList<Join> getJoins() {
        return joins;
    }



}
