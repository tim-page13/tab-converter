package com.timpage.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.timpage.musicXMLparserDH.music.Join;
import com.timpage.musicXMLparserDH.music.Note;
import com.timpage.musicXMLparserDH.parser.musicXMLparserDH;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.javatuples.Triplet;


public class TabConverter {
    
    public org.jsoup.nodes.Document doc;
    private musicXMLparserDH parser;
    private ArrayList<ArrayList<ArrayList<Note>>> songPartMatrix;
    private Guitar guitar;
    private String destFileName;
    // data structure to store the possible combinations of frettings and the score 
    private Hashtable<ArrayList<Integer>, ArrayList<ChordMap>> chordMappings = new Hashtable<>();
    private ArrayList<ChordMap> bestPath;
    private float currentBest;
    private int searchLimit;


    /**
     * Constructor for the TabConverter object. Only used in testing
     * Handles the parsing of the given file in preparation of its conversion to tab
     * @param filename the name of the file to be converted to tab
     * @throws IOException if the input file cannot be read
     */
    // public TabConverter(String filename) throws IOException {
    //     try {
    //         parser = new musicXMLparserDH(filename);
    //         parser.parseMusicXML();
    //         songPartMatrix = parser.getSongPartMatrix();
    //         guitar = new Guitar();
    //         if (filename.contains(".xml")) {
    //             int end = filename.lastIndexOf(".xml");
    //             String start = filename.substring(0, end);
    //             destFileName = start + "-tab" + ".xml";
    //         }
    //         else if (filename.contains(".musicxml")) {
    //             int end = filename.lastIndexOf(".musicxml");
    //             String start = filename.substring(0, end);
    //             destFileName = start + "-tab" + ".musicxml";
    //         }
    //         else {
    //             destFileName = filename + "-tab";
    //         }
    //         InputStream is = new FileInputStream(filename) {
    //             @Override
    //             public int read() throws IOException {
    //                 return 0;
    //             }
    //         };
    //         doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
    //         if (doc.getElementsByTag("note").isEmpty()) {
    //             doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
    //             if (doc.getElementsByTag("note").isEmpty()) {
    //                 System.out.println("Please check that your file is encoded in UTF-8 or UTF-16 and contains notes.");
    //             }
    //         }
            
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
             
    // }

    /**
     * Constructor for the TabConverter object
     * Handles the parsing of the given file in preparation of its conversion to tab
     * @param file the file to be converted to tab
     * @throws IOException if the input file cannot be read
     */
    public TabConverter(File file) throws IOException {
        try {
            String filename = file.getName();
            // parse file and store note information into songPartMatrix
            parser = new musicXMLparserDH(file);
            parser.parseMusicXML();
            songPartMatrix = parser.getSongPartMatrix();
            guitar = new Guitar();
            if (!filename.contains(".xml") && !filename.contains(".musicxml")) {
                throw new IOException("Input file potentially the wrong format. Make sure it has the '.musicxml' or '.xml' extension.");
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
        // iterate through each part
        for (int l=0; l<songPartMatrix.size(); l++) {
            int offset = 0;
            int numNotes = 0;
            int currentMeasure = 0;
            int backupDistance = 0;
            int divisions = 0;
            int beats = 0;
            int numVoices = 0;
            for (Element voice: this.doc.select("part").get(l).getElementsByTag("voice")) {
                try {
                    int voiceNo = Integer.parseInt(voice.ownText());
                    if (voiceNo > numVoices) {
                        numVoices = voiceNo;
                    }
                }
                catch (NumberFormatException e) {
                    // This is thrown when the voice tag doesn't contain an integer
                    System.out.println("Invalid String: voice tag doesn't contain an integer");
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

            // iterate through each chord
            for (int i=0; i<songPartMatrix.get(l).size(); i++) {
                // removes duplicate chords
                if (i > 0 && songPartMatrix.get(l).get(i-1).equals(songPartMatrix.get(l).get(i))) {
                    songPartMatrix.get(l).remove(i);
                    i--;
                }
                else if (songPartMatrix.get(l).get(i).size() > 0) {
                    // convert the current line of concurrent notes (i.e. a chord) to tab
                    chordToTab(songPartMatrix.get(l).get(i));
                }
            }

            // determine best tab assignment 
            calculateBestTransitions(songPartMatrix.get(l));

            // write the string/fret assignments to the Note objects
            for (int i=0; i<songPartMatrix.get(l).size(); i++) {
                // remove rests from the matrix
                if (songPartMatrix.get(l).get(i).size() == 0) {
                    songPartMatrix.get(l).remove(i);
                    i--;
                }
                else {
                    Enumeration<Integer> strings = bestPath.get(i).getFretting().keys();
                    for (int j=0; j<songPartMatrix.get(l).get(i).size(); j++) {
                        int string = strings.nextElement();
                        // write assignments to the note objects
                        songPartMatrix.get(l).get(i).get(j).setStringNo(string);
                        songPartMatrix.get(l).get(i).get(j).setFretNo(bestPath.get(i).getFretting().get(string));
                    }
                }
            }

            for (int i=0; i<songPartMatrix.get(l).size(); i++) {
                int newMeasureNo = songPartMatrix.get(l).get(i).get(0).getMeasure();
                Element thismeasure = this.doc.select("part").get(l).getElementsByTag("measure").get((newMeasureNo-1));
                // if it is a new bar
                if (newMeasureNo != currentMeasure) {
                    offset = 0;
                    numNotes = this.doc.select("part").get(l).getElementsByTag("measure").get((newMeasureNo-1)).getElementsByTag("note").size();
                    // work through measures backwards from newMeasureNo to currentMeasure to scan for divisions or beats tags to recalculate backupDistance
                    boolean changeDivision = false;
                    boolean changeBeats = false;
                    for (int j=newMeasureNo; j>currentMeasure; j--) {
                        Element tempMeasure = this.doc.select("part").get(l).getElementsByTag("measure").get((j-1));
                        // when the "divisions" tag is present, the backup value needs to be recalculated because the time signature
                            // or number of divisions per beat may have changed
                        if (!changeDivision && !tempMeasure.getElementsByTag("divisions").isEmpty()) {
                            try {
                                divisions = Integer.parseInt(tempMeasure.getElementsByTag("divisions").first().ownText());
                                changeDivision = true;
                            }
                            catch (NumberFormatException e) {
                                // This is thrown when the divisions tag doesn't contain an integer
                                System.err.println("Invalid String");
                            }
                        }
                        if (!changeBeats && !tempMeasure.getElementsByTag("beats").isEmpty()) {
                            try {
                                beats = Integer.parseInt(tempMeasure.getElementsByTag("beats").first().ownText());
                                changeBeats = true;
                            }
                            catch (NumberFormatException e) {
                                // This is thrown when the beats tags doesn't contain an integer
                                System.err.println("Invalid String");
                            }
                        }
                        if (changeBeats && changeDivision) {
                            break;
                        }
                    }
                    backupDistance = divisions * beats;
                    // "backup" tag to put the write the tab staff to the beginning of the bar
                    Element backup = new Element("backup");
                    backup.appendElement("duration").append(backupDistance+"");
                    thismeasure.appendChild(backup);
                    currentMeasure = newMeasureNo;
                }
                // once all string/fret assignments are made, write them to the file
                for (int j=0; j<songPartMatrix.get(l).get(i).size(); j++) {
                    // gets the right note within the measure using the unsorted matrix
                    boolean aNote = false;
                    boolean aRest = false;
                    Element thisnote = thismeasure.getElementsByTag("note").get(j);
                    while (aNote == false && offset < numNotes) {
                        thisnote = thismeasure.getElementsByTag("note").get(offset);
                        //ignore rests (they don't have a pitch) and tab notes
                        if (thisnote.select("staff").isEmpty() || thisnote.getElementsByTag("staff").first().ownText() == "1") { 
                            if (thisnote.getElementsByTag("pitch").isEmpty()) {
                                aRest = true;
                                // because j is iterating through the concurrent notes of which a rest is not a part
                                j--;
                            }
                            offset++;
                            aNote = true; // a note has been found in the bar
                            break;
                        }
                        else {
                            offset++;
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
                        // write the staff information to the correct place
                        if (!tabNote.getElementsByTag("notehead-text").isEmpty()) {
                            tabNote.getElementsByTag("notehead-text").last().after(staff);
                        }
                        else if (!tabNote.getElementsByTag("notehead").isEmpty()) {
                            tabNote.getElementsByTag("notehead").last().after(staff);
                        }
                        else if (!tabNote.getElementsByTag("stem").isEmpty()) {
                            tabNote.getElementsByTag("stem").last().after(staff);
                        }
                        else if (!tabNote.getElementsByTag("time-modification").isEmpty()) {
                            tabNote.getElementsByTag("time-modification").last().after(staff);
                        }
                        else if (!tabNote.getElementsByTag("accidental").isEmpty()) {
                            tabNote.getElementsByTag("accidental").last().after(staff);
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

        // write the changes to the file
        WriteFile();
        return destFileName;

    }

    /**
     * Converts a chord (set of concurrently played notes) to tab by assigning them all to strings and frets.
     * @param chord the set of concurrently played notes to be assigned
     * @return the set of notes but with the string and fret assignments within the Note objects
     */
    private List<Note> chordToTab(List<Note> chord) {
        // perform a DFS with branch and bound to prune invalid combinations of string/fret assignments
        // the midipitches of the notes in the chord
        ArrayList<Integer> chordNotes = new ArrayList<>();
        for (int j=0; j<chord.size(); j++) {
            chordNotes.add(chord.get(j).getMidiPitch());
        }
        // calculate the possible assignments if not already in the store
        if (!chordMappings.containsKey(chordNotes)) {
            // only if the assignment was successful
            if (assignNote(new ChordMap(), chordNotes, new ArrayList<>())) {
                // sort the ChordMaps by score
                Collections.sort(chordMappings.get(chordNotes));
                // uncomment the below code to print each chord fingering and its score
                // System.out.println("new chord");
                // for (int i=0; i<6; i++) {
                //     for (ChordMap cm: chordMappings.get(chordNotes)) {
                //         if (cm.getFretting().containsKey(i) && cm.getFretting().get(i) != null) {
                //             int fret = cm.getFretting().get(i);
                //             if (fret > 9) {
                //                 System.out.printf(fret + "  - ");
                //             }
                //             else {
                //                 System.out.printf(" " + fret + "  - ");
                //             }
                //         }
                //         else {
                //             System.out.printf(" -  - ");
                //         }

                //     }
                //     System.out.println("");
                // }
                // System.out.println("Your ranking from best to worst:");
                // for (ChordMap cm: chordMappings.get(chordNotes)) {
                //     System.out.printf(" %f  - ", cm.getScore());
                // }
                // System.out.println("");
                // System.out.println("");
            }
            else {
                System.out.println("Failed to convert chord to tab");
            }
        }

        

        return chord;
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
                        cmCopy.addPitch(k, chordNotes.get(j));
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

    /**
     * Method to calculate the best path of chord assignments taking into consideration the individual chord 
     * fretting scores and the transition scores.
     * @param smPart The instrument part to be converted to tab
     * @return the boolean value of whether the path search was successful or not
     */
    public boolean calculateBestTransitions(ArrayList<ArrayList<Note>> smPart) {
        Hashtable<Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap>, ArrayList<Transition>> allTransitions = new Hashtable<>();
        for (int i=0; i<smPart.size(); i++) {
            // skip the first chord and rests
            if (i == 0 || smPart.get(i-1).size() == 0) {
                continue;
            }
            else if (smPart.get(i).size() == 0) {
                i++;
                continue;
            }
            ArrayList<Note> chord1 = smPart.get(i-1);
            ArrayList<Note> chord2 = smPart.get(i);
            // the midipitches of the notes in the chords
            ArrayList<Integer> chord1MidiNotes = new ArrayList<>();
            ArrayList<Integer> chord2MidiNotes = new ArrayList<>();
            for (int j=0; j<chord1.size(); j++) {
                chord1MidiNotes.add(chord1.get(j).getMidiPitch());
            }
            for (int j=0; j<chord2.size(); j++) {
                chord2MidiNotes.add(chord2.get(j).getMidiPitch());
            }
            ArrayList<ChordMap> chord1Maps = chordMappings.get(chord1MidiNotes);
            ArrayList<ChordMap> chord2Maps = chordMappings.get(chord2MidiNotes);
            for (ChordMap c1Map : chord1Maps) {
                Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap> key = new Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap>(chord1MidiNotes, chord2MidiNotes, c1Map);
                // store already contains the transition scores for these two chords
                if (allTransitions.contains(key)) {
                    break;
                }
                ArrayList<Transition> possibleTransitions = new ArrayList<>();
                for (ChordMap c2Map : chord2Maps) {
                    Transition transition = new Transition(c2Map);
                    transition.calculateTransitionScore(c1Map);
                    possibleTransitions.add(transition);
                }
                Collections.sort(possibleTransitions);
                allTransitions.put(key, possibleTransitions);
            }
        }
        ArrayList<ArrayList<Note>> section = new ArrayList<>();
        bestPath = new ArrayList<>();
        boolean overflow = false; // for artificial section divisions
        boolean removeLinkChord = false;
        int sectionSizeLimit = 12;
        for (int i=0; i<=smPart.size(); i++) {
            // end of the section
            if (i == smPart.size() || (smPart.get(i).size() == 0 && !section.isEmpty()) || section.size() == sectionSizeLimit) {
                ArrayList<ChordMap> startPath = new ArrayList<>();
                float startScore =  0f;
                int startCounter = 1;
                if (overflow == true) {
                    // when a section length is the limit length followed by a rest or the end of the song
                    if (section.size() == 1 && (i == smPart.size() || smPart.get(i).size() == 0)) {
                        section.clear();
                        overflow = false;
                        continue;
                    }
                    // the chord which gets passed on to be the start of the next section as well
                    ChordMap linkMap = bestPath.get(bestPath.size()-1);
                    startPath.add(linkMap);
                    overflow = false;
                    removeLinkChord = true;
                }
                // section size limit reached and last chord is not a rest or the end of the song
                if (section.size() == sectionSizeLimit && (i != smPart.size() && smPart.get(i).size() > 0)) {
                    section.add(smPart.get(i));
                    overflow = true;
                }
                // flexible search limit calculation
                if (section.size() > 10) {
                    searchLimit = 20-section.size();
                    if (searchLimit < 4) {
                        searchLimit = 4;
                    }
                }
                else if (section.size() > 8) {
                    searchLimit = 21-section.size();
                }
                else {
                    searchLimit = 20;
                }
                currentBest = -1;
                // recursively calculate preferred path for the section
                ArrayList<ChordMap> sectionPath = addToPath(allTransitions, section, startPath, startCounter, startScore);
                if (removeLinkChord) {
                    sectionPath.remove(0);
                    removeLinkChord = false;
                }
                // add the section path to the full song's path without the duplicate starting chord
                bestPath.addAll(sectionPath);
                if (overflow == true) {
                    ArrayList<Note> linkChord = section.get(section.size()-1);
                    section.clear();
                    section.add(linkChord);
                }
                else {
                    section.clear();
                }
            }
            // add chord to the section
            else if (smPart.get(i).size() > 0) {
                section.add(smPart.get(i));
            }

        }
        return true;
    }

    /**
     * Recursive function which adds a chord transition to the path. 
     * DFS to find the best combination of chords based on individual chord fretting scores and transition scores.
     * @param allTransitions Hashtable with all the transitions possible from one chord mapping to another chord
     * @param smPart the section of the instrumental part for which the best tab mapping is being found
     * @param path the current path of frettings being searched currently
     * @param i counter to iterate through smPart
     * @param pathScore the score of the current path being explored
     * @return the best path of chordMap assignments found
     */
    private ArrayList<ChordMap> addToPath(Hashtable<Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap>, ArrayList<Transition>> allTransitions, ArrayList<ArrayList<Note>> smPart, ArrayList<ChordMap> path, int i, float pathScore) {
        // path to be returned
        ArrayList<ChordMap> bestPathSection = null;
        // recursive base case
        if (i >= smPart.size() && (i != 1 || smPart.isEmpty())) {
            currentBest = pathScore;
            return path;
        }
        ArrayList<Note> chord1 = smPart.get(i-1);
        // the midipitches of the notes in the chords
        ArrayList<Integer> chord1MidiNotes = new ArrayList<>();
        ArrayList<Integer> chord2MidiNotes = new ArrayList<>();
        ArrayList<Join> chord1Joins = new ArrayList<>();
        ArrayList<Join> chord2Joins = new ArrayList<>();
        for (int j=0; j<chord1.size(); j++) {
            chord1MidiNotes.add(chord1.get(j).getMidiPitch());
            for (Join join: chord1.get(j).getJoins()) {
                // only interested in the joins starting in chord1 and ending in chord2
                if (join.getType().equals("start")) {
                    chord1Joins.add(join);
                }
            }
        }
        if (smPart.size() > 1) {
            ArrayList<Note> chord2 = smPart.get(i);
            for (int j=0; j<chord2.size(); j++) {
                chord2MidiNotes.add(chord2.get(j).getMidiPitch());
                for (Join join: chord2.get(j).getJoins()) {
                    // only interested in the joins starting in chord1 and ending in chord2
                    if (join.getType().equals("stop")) {
                        // for each join in chord2, check there is a matching join in chord1
                        for (Join c1Join: chord1Joins) {
                            if (c1Join.getJoinType() == join.getJoinType() && c1Join.getNum() == join.getNum()) {
                                chord2Joins.add(join);
                                break;
                            }
                        }
                    }
                }
            }
        }
        // for each join in chord1, check there is a matching join in chord2
        for (Join join1: chord1Joins) {
            Boolean match = false;
            for (Join join2: chord2Joins) {
                if (join1.getJoinType() == join2.getJoinType() && join1.getNum() == join2.getNum()) {
                    match = true;
                    join1.setMatchingJoin(join2);
                    break;
                }
            }
            if (!match) {
                chord1Joins.remove(join1);
            }
        }
        // case of first chord in sequence 
        if (i == 1) {
            ArrayList<ChordMap> chord1Maps;
            if (path.size() == 0) {
                chord1Maps = chordMappings.get(chord1MidiNotes);
            }
            else { // case of path overflow
                chord1Maps = new ArrayList<>();
                chord1Maps.add(path.get(0));
            }
            int mapLimit = searchLimit;
            if (chord1Maps.size() < mapLimit) { mapLimit = chord1Maps.size(); }
            for (ChordMap c1Map : chord1Maps.subList(0, mapLimit)) {
                // if there is only one chord in the section return the best scoring chord
                if (i >= smPart.size() && path.size() == 0) {
                    path.add(c1Map);
                    return path;
                }
                Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap> key = new Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap>(chord1MidiNotes, chord2MidiNotes, c1Map);
                ArrayList<Transition> transitions = allTransitions.get(key);
                int limit = searchLimit;
                if (transitions.size() < limit) { limit = transitions.size(); }
                for (Transition transition: transitions.subList(0, limit)) {
                    ArrayList<ChordMap> newPath = new ArrayList<>();
                    newPath.add(c1Map);
                    ChordMap c2Map = transition.getDstMap();
                    newPath.add(c2Map);
                    float newScore = pathScore+c1Map.getScore()+transition.getTransitionScore();
                    // check tied notes are on the same string
                    for (Join join1: chord1Joins) {
                        int j1String = c1Map.getPitchString(join1.getMidiPitch());
                        int j2Pitch = c2Map.getStringPitch(j1String);
                        if (join1.getMidiPitch() != j2Pitch) {
                            newScore += 5;
                        }
                    }
                    // prune paths that would only lead to a worse score than the current best
                    if (currentBest == -1 || newScore < currentBest) {
                        i++;
                        ArrayList<ChordMap> result = addToPath(allTransitions, smPart, newPath, i, newScore);
                        if (result != null) {
                            bestPathSection = result;
                        }
                        i--;
                    }
                    else break;
                } 
            }
        }
        else {
            ChordMap c1Map = path.get(path.size()-1);
            Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap> key = new Triplet<ArrayList<Integer>, ArrayList<Integer>, ChordMap>(chord1MidiNotes, chord2MidiNotes, c1Map);
            ArrayList<Transition> transitions = allTransitions.get(key);
            // limit the search space to those most likely to find an optimal result
            int limit = searchLimit;
            if (transitions.size() < limit) { limit = transitions.size(); }
            for (Transition transition: transitions.subList(0, limit)) {
                ArrayList<ChordMap> newPath = new ArrayList<>();
                newPath.addAll(path);
                ChordMap c2Map = transition.getDstMap();
                newPath.add(c2Map);
                float newScore = pathScore+transition.getTransitionScore();
                // check tied notes are on the same string
                for (Join join1: chord1Joins) {
                    int j1String = c1Map.getPitchString(join1.getMidiPitch());
                    int j2Pitch = c2Map.getStringPitch(j1String);
                    if (join1.getMidiPitch() != j2Pitch) {
                        newScore += 5;
                    }
                }
                // add a penalty for not reusing chordMaps in the section. has to be calculated here because dependent on the chosen path
                if (!path.contains(transition.getDstMap())) {
                    newScore += 0.5;
                }
                // prune paths that would only lead to a worse score than the current best
                if (currentBest == -1 || newScore < currentBest) {
                    i++;
                    ArrayList<ChordMap> result = addToPath(allTransitions, smPart, newPath, i, newScore);
                    if (result != null) {
                        bestPathSection = result;
                    }
                    i--;
                }
                else break;
            } 
        }
        return bestPathSection;
    }


}
