package com.timpage.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
// import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.timpage.musicXMLparserDH.music.Note;
import com.timpage.musicXMLparserDH.parser.musicXMLparserDH;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;


public class TabConverter {
    
    public org.jsoup.nodes.Document doc;
    private musicXMLparserDH parser;
    private ArrayList<ArrayList<ArrayList<Note>>> songPartMatrix;
    private Guitar guitar;
    private String destFileName;
    // data structure to store the possible combinations of frettings and the score 
    private Hashtable<ArrayList<Integer>, ArrayList<ChordMap>> chordMappings = new Hashtable<>();


    /**
     * Constructor for the TabConverter object
     * Handles the parsing of the given file in preparation of its conversion to tab
     * @param filename the name of the file to be converted to tab
     * @throws IOException if the input file cannot be read
     */
    public TabConverter(String filename) throws IOException {
        try {
            parser = new musicXMLparserDH(filename);
            parser.parseMusicXML();
            songPartMatrix = parser.getSongPartMatrix();
            guitar = new Guitar();
            if (filename.contains(".xml")) {
                int end = filename.lastIndexOf(".xml");
                String start = filename.substring(0, end);
                destFileName = start + "-tab" + ".xml";
            }
            else if (filename.contains(".musicxml")) {
                int end = filename.lastIndexOf(".musicxml");
                String start = filename.substring(0, end);
                destFileName = start + "-tab" + ".musicxml";
            }
            else {
                destFileName = filename + "-tab";
            }
            InputStream is = new FileInputStream(filename) {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
            if (doc.getElementsByTag("note").isEmpty()) {
                doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
                if (doc.getElementsByTag("note").isEmpty()) {
                    System.out.println("Please check that your file is encoded in UTF-8 or UTF-16 and contains notes.");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
             
    }

    /**
     * Constructor for the TabConverter object
     * Handles the parsing of the given file in preparation of its conversion to tab
     * @param file the file to be converted to tab
     * @throws IOException if the input file cannot be read
     */
    public TabConverter(File file) throws IOException {
        try {
            String filename = file.getName();
            parser = new musicXMLparserDH(file);
            parser.parseMusicXML();
            songPartMatrix = parser.getSongPartMatrix();
            guitar = new Guitar();
            if (!filename.contains(".xml") && !filename.contains(".musicxml")) {
                throw new IOException("input file potentially the wrong format");
            }
            int end = filename.lastIndexOf(".");
            String startString = filename.substring(0, end);
            String endString = filename.substring(end);
            destFileName = "../webapps/data/" + startString + "-tab" + endString;

            InputStream is = new FileInputStream(file) {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
            if (doc.getElementsByTag("note").isEmpty()) {
                doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
                if (doc.getElementsByTag("note").isEmpty()) {
                    System.out.println("Please check that your file is encoded in UTF-8 or UTF-16 and contains notes.");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
             
    }

    /**
     * Used to write the document with tab included to permanent file storage
     */
    private void WriteFile() {
        BufferedWriter  writer = null;
        try {
            writer = new BufferedWriter( new FileWriter(destFileName));
            writer.write(doc.outerHtml());
            writer.close();
            System.out.println("Written to file");
        }
        catch ( IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Iterates through each part in the score and converts the sheet music to guitar tab
     * @return the filename of the score with the generated tab in it
     */
    public String convertPartsToTab() {
        int offset = 0;
        int numNotes = 0;
        int currentMeasure = 0;
        // iterate through each part
        for (int l=0; l<songPartMatrix.size(); l++) {
            int backupDistance = 0;
            int numVoices = 0;
            for (Element voice: this.doc.select("part").get(l).getElementsByTag("voice")) {
                try {
                    int voiceNo = Integer.parseInt(voice.ownText());
                    if (voiceNo > numVoices) {
                        numVoices = voiceNo;
                    }
                }
                catch (NumberFormatException e) {
                    // This is thrown when the staves tag doesn't contain an integer
                    System.out.println("Invalid String: staves tag doesn't contain an integer");
                }
            }
            // add xml tags for the TAB clef and stave information
            Element partAttributes = this.doc.select("part").get(l).getElementsByTag("measure").first().getElementsByTag("attributes").first();
            if (partAttributes.getElementsByTag("staves").isEmpty()) {
                Element staves = new Element("staves").append("2");
                partAttributes.getElementsByTag("time").last().after(staves);
            }
            else {
                Element staves = partAttributes.getElementsByTag("staves").first();
                if (!staves.hasText()) {
                    staves.append("2");
                }
                else {
                    int staveCount = 1;
                    try {
                        staveCount = Integer.parseInt(staves.ownText());
                    }
                    catch (NumberFormatException e) {
                        // This is thrown when the staves tag doesn't contain an integer
                        System.out.println("Invalid String: staves tag doesn't contain an integer");
                    }
                    staves.text(""+(staveCount+1));
                }
            }
            // add clef numberings if not already there so the notations will be put on the correct staves
            int numClefs = 0;
            for (Element thisClef : partAttributes.getElementsByTag("clef")) {
                numClefs++;
                if (!thisClef.attributes().hasKey("number")) {
                    thisClef.attr("number", ""+numClefs);
                }
            }
            Element clef = new Element("clef").attr("number", ""+(numClefs+1));
            partAttributes.getElementsByTag("clef").last().after(clef);
            clef.attr("number", ""+this.doc.select("part").get(l).getElementsByTag("clef").size());
            Element sign = new Element("sign");
            sign.append("TAB");
            clef.appendChild(sign);
            Element lineAttr = new Element("line");
            lineAttr.append("5");
            clef.appendChild(lineAttr);
            Element staffDetails = new Element("staff-details").attr("number", ""+(numClefs+1));
            clef.after(staffDetails);
            Element staffLines = new Element("staff-lines");
            staffLines.append(""+guitar.getGuitarStrings().size());
            staffDetails.appendChild(staffLines);
            for (int j=0; j<guitar.getGuitarStrings().size(); j++) {
                Element staffTuning = new Element("staff-tuning");
                staffTuning.attr("line", ""+(j+1));
                staffDetails.appendChild(staffTuning);
                Element tuningStep = new Element("tuning-step");
                tuningStep.append(guitar.getGuitarStrings().get(j).getOpenPitch());
                staffTuning.appendChild(tuningStep);
                if (guitar.getGuitarStrings().get(j).getOpenAccidental() != 0) {
                    Element tuningAlter = new Element("tuning-alter");
                    tuningAlter.append(guitar.getGuitarStrings().get(j).getOpenAccidental()+"");
                    staffTuning.appendChild(tuningAlter);
                }
                Element tuningOctave = new Element("tuning-octave");
                tuningOctave.append(guitar.getGuitarStrings().get(j).getOpenOctave()+"");
                staffTuning.appendChild(tuningOctave);
            }

            // iterate through each slice
            System.out.println("songPartMatrix.get(" + l + ").size(): " + songPartMatrix.get(l).size());
            
            for (int i=0; i<songPartMatrix.get(l).size(); i++) {
                // removes duplicate lines
                if (i > 0 && songPartMatrix.get(l).get(i).size() > 0 && songPartMatrix.get(l).get(i-1).equals(songPartMatrix.get(l).get(i))) {
                    songPartMatrix.get(l).remove(i);
                    i--;
                }
                else if (songPartMatrix.get(l).get(i).size() > 0) {
                    // convert the current line of concurrent notes (i.e. a chord) to tab
                    chordToTab(songPartMatrix.get(l).get(i));
                }
            }
            for (int i=0; i<songPartMatrix.get(l).size(); i++) {
                List<Note> line = songPartMatrix.get(l).get(i);
                if (line.size() > 0) {
                    boolean newMeasure = false;
                    if (songPartMatrix.get(l).get(i).get(0).getMeasure() != currentMeasure) {
                        currentMeasure = songPartMatrix.get(l).get(i).get(0).getMeasure();
                        offset = 0;
                        newMeasure = true;
                        numNotes = this.doc.select("part").get(l).getElementsByTag("measure").get((songPartMatrix.get(l).get(i).get(0).getMeasure()-1)).getElementsByTag("note").size();
                        System.out.println("resetting offset");
                    }
                    // once all string/fret assignments are made, write them to the file
                    for (int j=0; j<songPartMatrix.get(l).get(i).size(); j++) {
                        // gets the right note within the measure using the unsorted matrix
                        Element thismeasure = this.doc.select("part").get(l).getElementsByTag("measure").get((songPartMatrix.get(l).get(i).get(j).getMeasure()-1));
                        boolean aNote = false;
                        boolean aRest = false;
                        Element thisnote = thismeasure.getElementsByTag("note").get(j);
                        // only for the first note of the bar
                        if (newMeasure) {
                            newMeasure = false;
                            // when the "divisions" tag is present, the backup valiue needs to be recalculated because the time signature
                                // or number of divisions per beat may have changed
                            if (!thismeasure.getElementsByTag("divisions").isEmpty()) {
                                try {
                                    int divisions = Integer.parseInt(thismeasure.getElementsByTag("divisions").first().ownText());
                                    int beats = Integer.parseInt(thismeasure.getElementsByTag("beats").first().ownText());
                                    backupDistance = divisions * beats;
                                }
                                catch (NumberFormatException e) {
                                    // This is thrown when the divisions or beats tags don't contain an integer
                                    System.err.println("Invalid String");
                                }
                            }
                            else {
                                System.err.println("Divisions is empty");
                            }
                            if (backupDistance != 0) {
                                // "backup" tag to put the write the tab staff to the beginning of the bar
                                Element backup = new Element("backup");
                                backup.appendElement("duration").append(backupDistance+"");
                                thismeasure.appendChild(backup);
                                // backupDistance = 0;
                            }
                            else {
                                System.err.println("Unable to backup");
                            }
                        }
                        while (aNote == false && offset < numNotes) {
                            System.out.println((offset+j) + "  " + offset + " " + j + "  " + songPartMatrix.get(l).get(i).get(j).getMeasure() + "  " + numNotes);
                            thisnote = thismeasure.getElementsByTag("note").get(offset);
                            //ignore rests (they don't have a pitch) and tab notes
                            if (thisnote.select("staff").isEmpty() || thisnote.getElementsByTag("staff").first().ownText() == "1") { 
                                if (thisnote.getElementsByTag("pitch").isEmpty()) {
                                    aRest = true;
                                    // because j is iterating through the concurrent notes of which a rest is not a part
                                    j--;
                                    System.out.println("rest");
                                }
                                else{
                                    System.out.println("note");
                                }
                                offset++;
                                aNote = true; // a note has been found in the bar
                                break;
                            }
                            else {
                                offset++;
                                System.out.println("incrementing offset; note not found");
                            }
                        }
                        if (aNote) {
                            // make a copy of the note to be the tab staff version
                            Element tabNote = thisnote.clone();
                            thismeasure.appendChild(tabNote);
                            Element voice = tabNote.getElementsByTag("voice").first();
                            int voiceNo = 0;
                            try {
                                voiceNo = Integer.parseInt(voice.ownText()) + numVoices;
                            }
                            catch (NumberFormatException e) {
                                // This is thrown when the staves tag doesn't contain an integer
                                System.out.println("Invalid String");
                            }
                            voice.text(""+voiceNo);
                            Element staff;
                            if (tabNote.getElementsByTag("staff").isEmpty()) {
                                staff = new Element("staff").append("2");
                            }
                            else {
                                staff = partAttributes.getElementsByTag("staff").first();
                                staff.text("2");
                            }
                            if (!tabNote.getElementsByTag("stem").isEmpty()) {
                                tabNote.getElementsByTag("stem").last().after(staff);
                            }
                            else {
                                tabNote.getElementsByTag("type").last().after(staff);
                            }
                            // remove beams from tab notes
                            tabNote.getElementsByTag("beam").remove();
                            // if there is a backup tag before the note, append it to the tab
                            if (thisnote.elementSiblingIndex() > 0 && thisnote.previousElementSibling().tagName() == "backup") {
                                tabNote.before(thisnote.previousElementSibling().clone());
                            }
                            // add tags to the note if missing
                            if (!aRest) {
                                if (tabNote.getElementsByTag("notations").isEmpty()) {
                                    tabNote.appendElement("notations").appendElement("technical");
                                }
                                if (tabNote.getElementsByTag("notations").get(0).getElementsByTag("technical").isEmpty()) {
                                    tabNote.getElementsByTag("notations").get(0).appendElement("technical");
                                }
                                Element technical = tabNote.getElementsByTag("notations").get(0).getElementsByTag("technical").get(0);
                                // the string and fret data 
                                technical.appendElement("string").append((songPartMatrix.get(l).get(i).get(j).getStringNo()+1) + "");
                                technical.appendElement("fret").append(songPartMatrix.get(l).get(i).get(j).getFretNo() + "");
                            }
                        }
                        else {
                            System.out.println("[[[ERROR NO NOTE FOUND]]]");
                        }
                        
                    }

                }

                
            }
            
        }        

        // write the changes to the file
        WriteFile();
        return destFileName;

    }

    /**
     * Converts a chord (set of concurrently played notes) to tab by assigning them all to strings and frets.
     * @param line the set of concurrently played notes to be assigned
     * @return the set of notes but with the string and fret assignments within the Note objects
     */
    private List<Note> chordToTab(List<Note> line) {
        //todo check how many notes are being played. If >6 check for duplicates, else assign the first 6
        
        // perform a DFS with branch and bound to prune invalid combinations of string/fret assignments
        // the midipitches of the notes in the chord
        ArrayList<Integer> chordNotes = new ArrayList<>();
        // chordMappings.put(chordNotes, new ArrayList<ChordMap>());
        for (int j=0; j<line.size(); j++) {
            chordNotes.add(line.get(j).getMidiPitch());
        }
        // if any notes have already been assigned to strings, don't assign any other notes to that string
                // This is leftover from plans for supporting multiple voices
        // boolean[] takenStrings = new boolean[guitar.getGuitarStrings().size()];
        // for (int j=0; j<line.size(); j++) {
        //     if (line.get(j).getStringNo() != null) {
        //         takenStrings[line.get(j).getStringNo()] = true;
        //     }
        // }

        // calculate the possible assignments if not already in the store
        if (!chordMappings.containsKey(chordNotes)) {
            // only if the assignment was successful
            if (assignNote(new ChordMap(), chordNotes, new ArrayList<>())) {
                // sort the ChordMaps by score
                Collections.sort(chordMappings.get(chordNotes));
            }
            else {
                System.out.println("Failed to convert chord to tab");
            }
        }

        // assigns the best scoring ChordMap from chordMappings to the notes in the line
        Enumeration<Integer> assignments = chordMappings.get(chordNotes).get(0).getFretting().keys();
        int j=0;
        while (assignments.hasMoreElements()) {
            int string = assignments.nextElement();
            int fret = chordMappings.get(chordNotes).get(0).getFretting().get(string);
            line.get(j).setStringNo(string);
            line.get(j).setFretNo(fret);
            j++;
        }
        return line;
    }

    /**
     * Recursive function to assign a Note to a string and fret to be played. 
     * A DFS with branch & bound is performed to get all useful combinations.
     * @param cm the ChordMap which contains the current frettings and the score of the assignment
     * @param chordNotes the midipitches of the notes in the chord
     * @param assignedNotes the midipitches of the notes that have been assigned to strings
     * @return the boolean of whether the note assignment has been successful or not.
     */
    private boolean assignNote(ChordMap cm, ArrayList<Integer> chordNotes, ArrayList<Integer> assignedNotes) {
        boolean successfulAssignment = false;
        // recursion base case: all notes assigned a string&fret
        if (cm.size() == chordNotes.size()) {
            ArrayList<ChordMap> cmArray;
            if (!chordMappings.containsKey(chordNotes)) {
                cmArray = new ArrayList<>();
            }
            else {
                cmArray = chordMappings.get(chordNotes);
                // check the assignment is not a duplicate
                for (ChordMap storedCM : cmArray) {
                    // don't add the assignment to the store unless it is new
                    if (storedCM.getFretting().equals(cm.getFretting())) {
                        return true;
                    }
                }
                chordMappings.remove(chordNotes);
            }
            cmArray.add(cm);
            chordMappings.put(chordNotes, cmArray);
            // calculate the score of the ChordMap
            cm.calculateScore();
            return true;
        }
        // loop through the notes to be assigned
        for (int j=0; j<chordNotes.size(); j++) {
            // prune the path where the note has already been assigned
            if (!assignedNotes.contains(j)) {
                // loop through the strings until the note is assigned a string
                for (int k=0; k<guitar.getGuitarStrings().size(); k++) {
                    boolean noteAssigned = false;
                    // prune paths where you are adding a fret out of reach or where fret number is out of range
                    int fret = chordNotes.get(j) - guitar.getGuitarStrings().get(k).getOpenMidiPitch();
                    // Prune the path where a note would be assigned to an invalid string (either because it is not possible or the string is already taken)
                    if (fret >= 0 && fret <= 24 && !cm.isStringTaken(k)) {
                        // deep copy of the chordmap to be modified and passed into the recursive function
                        ChordMap cmCopy = new ChordMap(cm);
                        noteAssigned = cmCopy.addFretting(k, fret);
                        // when there is an assignment for all notes
                        if (noteAssigned) {
                            // deep copy of the assigned notes to be modified and passed into the recursive function
                            ArrayList<Integer> assignedNotesCopy = new ArrayList<>();
                            assignedNotesCopy.addAll(assignedNotes);
                            assignedNotesCopy.add(j);
                            if (assignNote(cmCopy, chordNotes, assignedNotesCopy)) {
                                successfulAssignment = true;
                            }
                        }
                    }
                }
            }
        }
        // if there is at least one possible assignment the overall function returns true
        return successfulAssignment;
    }

}
