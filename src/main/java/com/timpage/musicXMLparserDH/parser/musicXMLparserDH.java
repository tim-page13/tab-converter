package com.timpage.musicXMLparserDH.parser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.timpage.musicXMLparserDH.music.Note;

/**
 * Initially created by Dorien Herremans on 02/02/15.  Modified by Tim Page.
 */
public class musicXMLparserDH extends HttpServlet {

    public org.jsoup.nodes.Document doc;
    HashMap<Integer, Integer> divMultiplier;
    private ArrayList<Note> notesOfSong;  //parsed all notes in order of file
    private ArrayList<ArrayList<Note>> notesOfSongParts;
    private ArrayList<ArrayList<Note>> songMatrix;
    private ArrayList<ArrayList<ArrayList<Note>>> songPartMatrix;
    public ArrayList<Note> notesOfSongNoRest;
    public int divisions;


    public musicXMLparserDH(File input) throws IOException {

        try {
            doc = Jsoup.parse(input, "UTF-8", "", Parser.xmlParser());

            if (doc.getElementsByTag("note").isEmpty()) {
                    System.out.println("Please check that your file is encoded in UTF-8 or UTF-16 and contains notes.");
            }

        } catch(IOException e) {
            System.out.println("could not parse input file");
            System.out.println(e.getStackTrace()); 
        }

    }


    /**
     * Parse the MusicXML file to first determine how many individual time slices are needed in the piece
     */
    public void parseMusicXML() {
        //get the number of divisions and the change throughout the piece
        ArrayList<Integer> divisions = new ArrayList<>();

        for (Element thisdiv : this.doc.getElementsByTag("divisions")) {
            divisions.add(Integer.valueOf(thisdiv.text()));
        }
        divMultiplier = new HashMap<Integer, Integer>();

        if (!this.doc.getElementsByTag("divisions").isEmpty()) {
            Integer lcm = lcm(divisions);
            //set the multiplier for each division.
            for (Integer i : divisions) {
                divMultiplier.put(i, (int) lcm / i);
            }
        } else {

            divMultiplier.put(1, 1);
        }

        notesOfSongParts = getAllNotesParts();
        songPartMatrix = setSongPartMatrix(notesOfSongParts);

    }


    public void removeRests(){

        this.notesOfSongNoRest = new ArrayList<>();

        for (int i = 0; i < notesOfSong.size(); i++){
            if (notesOfSong.get(i).getPitch() != "Z") {


                this.notesOfSongNoRest.add(notesOfSong.get(i));
            }


        }
    }

    public ArrayList<ArrayList<Note>> getSongMatrix() {
        return songMatrix;
    }

    public ArrayList<ArrayList<ArrayList<Note>>> getSongPartMatrix() {
        return songPartMatrix;
    }

    public ArrayList<Note> getNotesOfSongNoRests() {
        return this.notesOfSongNoRest;
    }

    public ArrayList<Note> getNotesOfSong() {
        return notesOfSong;
    }


    /**
     * Write the note information into the data structure
     * @param notesOfSong
     * @return
     */
    private ArrayList<ArrayList<ArrayList<Note>>> setSongPartMatrix(ArrayList<ArrayList<Note>> notesOfSong){
        //list of note ideas per slice
        ArrayList<ArrayList<ArrayList<Note>>> songPartMatrix = new ArrayList<>();
        // iterate through each part
        for (int i=0; i<notesOfSong.size(); i++) {
            ArrayList<ArrayList<Note>> partiMatrix = new ArrayList<>();
            // iterate through each chord of concurrent notes
            Integer numberOfSlices = 0;
            for (int k=0; k<notesOfSong.get(i).size(); k++) {
                // iterate through notes in the slice
                if (notesOfSong.get(i).get(k).getDuration() + notesOfSong.get(i).get(k).getStartTime() > numberOfSlices) {
                    numberOfSlices = notesOfSong.get(i).get(k).getDuration() + notesOfSong.get(i).get(k).getStartTime();
                }
            }
                
            // add an arraylist for each slice of notes
            for (int j = 0; j < numberOfSlices; j++){
                ArrayList<Note> mylist = new ArrayList<Note>();
                partiMatrix.add(mylist);
            }

            // add the instrument part to the matrix with all the instrument parts
            songPartMatrix.add(partiMatrix);
        }
            
        for (int i=0; i<notesOfSong.size(); i++) {
            // for each note in the part
            for (Note inote : notesOfSong.get(i)) {
                if (inote.getPitch() != "Z") {
                    for (int j = 0; j < inote.getDuration(); j++) {
                        songPartMatrix.get(i).get(j + inote.getStartTime()).add(inote);
                    }
                }
            }
        }
        
        return songPartMatrix;

    }


    /**
     * Modified version of DH method. 
     * Gets all the notes in the song but organises them by instrumental part.
     * @return Data structure of each part with all of its chords comprised of notes played simultaneously.
     */
    private ArrayList<ArrayList<Note>> getAllNotesParts() {

        ArrayList<ArrayList<Note>> notesOfSong = new ArrayList<ArrayList<Note>>();
        Integer currentVoice = 0;

        Integer duration;

        notesOfSong.clear();

        Integer position = 0;
        Integer lastDuration = 0;
        Integer counter = 0;

        // for all parts of the score
        for (int z = 0; z < this.doc.select("part").size(); z++ ){

            position = 0;
            lastDuration = 0;
            counter = 1;
            divisions = 1;

            //go through each measure in the part
            for (Element thismeasure : this.doc.select("part").get(z).getElementsByTag("measure")) {
                String measure = "0";
                if (!thismeasure.getElementsByTag("divisions").isEmpty()) {
                    divisions = Integer.valueOf(thismeasure.getElementsByTag("divisions").text());
                }
                measure = thismeasure.attr("number");
                // go through each note in the measure
                for (Element thisnote : thismeasure.children()) {
                    if (thisnote.tagName().equals("note")) {

                        Note note = new Note(0);
                        counter++;

                        //get current voice
                        currentVoice = Integer.valueOf(thisnote.getElementsByTag("voice").text());
                        note.setVoice(currentVoice);

                        //get the pitch of the note
                        if (!thisnote.getElementsByTag("pitch").isEmpty()) {
                            for (Element thispitch : thisnote.getElementsByTag("pitch")) {
                                note.setPitch(thispitch.getElementsByTag("step").text());
                                String octave = thispitch.getElementsByTag("octave").text().replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2");
                                Integer octaveInt = Integer.parseInt(octave);
                                note.setOctave(octaveInt);

                                String alter = String.valueOf(thispitch.getElementsByTag("alter").text());
                                if (!thispitch.getElementsByTag("alter").isEmpty()) {

                                    note.setAccidentalInt(Integer.parseInt(alter));

                                    if (alter.equals("1")) {
                                        note.setAccidental("#");
                                    } else if (alter.equals("-1")) {
                                        note.setAccidental("b");
                                    } else if (alter.equals("2")) {
                                        note.setAccidental("##");
                                    } else if (alter.equals("-2")) {
                                        note.setAccidental("bb");
                                    }
                                }
                                note.calculateMidiPitch();
                            }

                        } 
                        else { 
                            // no pitch tag in the note so it is a rest
                            note.setPitch("Z");

                        }

                        // set the duration of the note
                        if (thisnote.getElementsByTag("duration").text().isEmpty()) {
                            duration = 0;
                        } 
                        else {
                            duration = Integer.valueOf(thisnote.getElementsByTag("duration").text()) * divMultiplier.get(divisions);
                        }
                        note.setDuration(duration);

                        // set the staff number of the note
                        int staff;
                        if (thisnote.getElementsByTag("staff").text().isEmpty()){
                            staff=currentVoice;
                        }
                        else {
                            staff = Integer.valueOf(thisnote.getElementsByTag("staff").text());
                        }
                        note.setStaff(staff);

                        // set the tie/join information of the note
                        if (!thisnote.getElementsByTag("tied").isEmpty()){
                            for (Element tie: thisnote.getElementsByTag("tied")) {
                                int tieNum = 1;
                                if (tie.attr("number") != "") {
                                    tieNum = Integer.valueOf(tie.attr("number"));
                                }
                                note.addJoin("tied", tieNum, tie.attr("type"), note.getMidiPitch());
                            }
                        }
                        if (!thisnote.getElementsByTag("slide").isEmpty()){
                            for (Element slide: thisnote.getElementsByTag("slide")) {
                                int slideNum = 1;
                                if (slide.attr("number") != "") {
                                    slideNum = Integer.valueOf(slide.attr("number"));
                                }
                                note.addJoin("slide", slideNum, slide.attr("type"), note.getMidiPitch());
                            }
                        }
                        if (!thisnote.getElementsByTag("hammer-on").isEmpty()){
                            for (Element hammer: thisnote.getElementsByTag("hammer-on")) {
                                int hammerNum = 1;
                                if (hammer.attr("number") != "") {
                                    hammerNum = Integer.valueOf(hammer.attr("number"));
                                }
                                note.addJoin("hammer-on", hammerNum, hammer.attr("type"), note.getMidiPitch());
                            }
                        }
                        if (!thisnote.getElementsByTag("pull-off").isEmpty()){
                            for (Element pullOff: thisnote.getElementsByTag("pull-off")) {
                                int pullOffNum = 1;
                                if (pullOff.attr("number") != "") {
                                    pullOffNum = Integer.valueOf(pullOff.attr("number"));
                                }
                                note.addJoin("pull-off", pullOffNum, pullOff.attr("type"), note.getMidiPitch());
                            }
                        }
                        if (!thisnote.getElementsByTag("slur").isEmpty()){
                            for (Element slur: thisnote.getElementsByTag("slur")) {
                                int slurNum = 1;
                                if (slur.attr("number") != "") {
                                    slurNum = Integer.valueOf(slur.attr("number"));
                                }
                                note.addJoin("slur", slurNum, slur.attr("type"), note.getMidiPitch());
                            }
                        }

                        // check if it is part of a chord
                        if (!thisnote.getElementsByTag("chord").isEmpty()) {
                            note.setStartTime(position);
                            //retract previous duration
                            note.setStartTime(position - lastDuration);
                        } 
                        else {
                            //increment start time of the current voice
                            note.setStartTime(position);
                            position = position + duration;
                        }

                        lastDuration = duration;
                        note.setCounter(counter);
                        note.setMeasure(Integer.valueOf(measure));

                        if (notesOfSong.size() <= z) {
                            ArrayList<Note> part = new ArrayList<Note>();
                            part.add(note);
                            notesOfSong.add(part);
                        }
                        else {
                            notesOfSong.get(z).add(note);
                        }

                    } 
                    // time adjustment forwards or backwards if it is not a note
                    else if (thisnote.tagName().equals("forward")) {
                        position = position + Integer.valueOf(thisnote.getElementsByTag("duration").text()) * divMultiplier.get(divisions);
                    } 
                    else if (thisnote.tagName().equals("backup")) {
                        position = position - Integer.valueOf(thisnote.getElementsByTag("duration").text()) * divMultiplier.get(divisions);
                    }
                }

            }
        }

        return notesOfSong;
    }


    /**
     * get greatest common denominator
     *
     * @param a
     * @param b
     * @return
     */
    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }


    /**
     * get least common multiplier
     */
    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }


    /**
     * get least common multiplier of a list
     */
    private static Integer lcm(ArrayList<Integer> inputInt) {
        long[] input = new long[inputInt.size()];

        for (int i = 0; i < inputInt.size(); i++) {
            input[i] = inputInt.get(i);

        }
        long result = input[0];
        for (int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return ((int) result);
    }


}