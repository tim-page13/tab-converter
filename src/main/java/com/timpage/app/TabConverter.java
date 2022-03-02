package com.timpage.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
import java.io.File;
// import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.swing.table.TableModel;

import java.nio.channels.FileChannel;

import com.timpage.musicXMLparserDH.music.Note;
import com.timpage.musicXMLparserDH.parser.musicXMLparserDH;
import com.timpage.app.Guitar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;


public class TabConverter {
    
    public org.jsoup.nodes.Document doc;
    private musicXMLparserDH parser;
    private ArrayList<ArrayList<Note>> songMatrix;
    private ArrayList<ArrayList<ArrayList<Note>>> songPartMatrix;
    private Guitar guitar;
    private String destFileName;


    public TabConverter(String filename) throws IOException {
        try {
            System.out.println("1" + filename);
            parser = new musicXMLparserDH(filename);
            // parser = new musicXMLparserDH(filename);
            parser.parseMusicXML();
            songMatrix = parser.getSongMatrix();
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
            System.out.println("2" + destFileName);
            // DuplicateFile(filename);

            InputStream is = new FileInputStream(filename) {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
            if (doc.getElementsByTag("note").isEmpty()) {
                doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
    //            doc = Jsoup.parse(input, "UTF-16", filename);
                if (doc.getElementsByTag("note").isEmpty()) {
                    System.out.println("Please check that your file is encoded in UTF-8 or UTF-16 and contains notes.");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
             
    }

    public TabConverter(File file) throws IOException {
        System.out.println("input file name: " + file.getName());

        try {
            String filename = file.getName();
            parser = new musicXMLparserDH(file);
            parser.parseMusicXML();
            songMatrix = parser.getSongMatrix();
            songPartMatrix = parser.getSongPartMatrix();
            guitar = new Guitar();
            if (!filename.contains(".xml") && !filename.contains(".musicxml")) {
                throw new IOException("input file potentially the wrong format");
            }
            int end = filename.lastIndexOf(".");
            String startString = filename.substring(0, end);
            String endString = filename.substring(end);
            destFileName = "../webapps/data/" + startString + "-tab" + endString;
            System.out.println("destFileName: " + destFileName);
            // DuplicateFile(filename);

            InputStream is = new FileInputStream(file) {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
            doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
            if (doc.getElementsByTag("note").isEmpty()) {
                doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
    //            doc = Jsoup.parse(input, "UTF-16", filename);
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

    public ArrayList<ArrayList<Note>> convertToTab() {
        for (int i=0; i<songMatrix.size(); i++) {
            List<Note> line = songMatrix.get(i);
            Collections.sort(line, new Comparator<Note>() {
                @Override
                public int compare(Note n1, Note n2) {
                    int mp1 = n1.getMidiPitch();
                    int mp2 = n2.getMidiPitch();
                    // sort in descending order
                    return mp2-mp1;
                }
            });
            if (line.size() > 0 && (i==0 || !songMatrix.get(i-1).equals(songMatrix.get(i)))) {
                //todo check how many notes are being played. If >6 check for duplicates, else assign the first 6
                //todo start by assigning highest pitch note to highest pitch string (strings may not be in pitch order if in a weird tuning). For now just go in order or strings
                System.out.printf("bar %d:  ", line.get(0).getMeasure());
                // if any notes have already been assigned to strings, don't assign any other notes to that string
                boolean[] takenStrings = new boolean[guitar.getGuitarStrings().size()];
                for (int j=0; j<line.size(); j++) {
                    if (line.get(j).getStringNo() != null) {
                        takenStrings[line.get(j).getStringNo()] = true;
                    }
                }
                for (int j=0; j<line.size(); j++) {
                    if (line.get(j).getStringNo() == null) {
                        boolean noteAssigned = false;
                        int k = 0;
                        // loop through the strings until the note is assigned a string
                        while (noteAssigned == false && k<guitar.getGuitarStrings().size()) {
                            // note can be played on the string and string isn't already taken
                            if (line.get(j).getMidiPitch() >= guitar.getGuitarStrings().get(k).getOpenMidiPitch() && takenStrings[k] == false) {
                                line.get(j).setStringNo(k);
                                line.get(j).setFretNo(line.get(j).getMidiPitch() - guitar.getGuitarStrings().get(k).getOpenMidiPitch());
                                takenStrings[k] = true;
                                noteAssigned = true;
                                break;
                            }
                            k++;
                        }
                    }

                    try {
                        System.out.printf("%s%d%s ", line.get(j).getPitch(), line.get(j).getOctave(), line.get(j).getAccidental());
                        System.out.printf("string: %d fret: %d ", (line.get(j).getStringNo()+1), line.get(j).getFretNo());
                    } catch (Exception e) {
                        System.out.println("note couldn't be assigned. There might be too many notes");
                    }

                }
                System.out.println();
            }
            
            
        }        

        return songMatrix;

    }
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
                    System.out.println("Invalid String");
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
                        System.out.println("Invalid String");
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
                // sort the notes in the time slice in descending midi pitch order
                List<Note> line = songPartMatrix.get(l).get(i);
                Collections.sort(line, new Comparator<Note>() {
                    @Override
                    public int compare(Note n1, Note n2) {
                        int mp1 = n1.getMidiPitch();
                        int mp2 = n2.getMidiPitch();
                        // sort in descending order
                        return mp2-mp1;
                    }
                });
                if (line.size() > 0 && (i==0 || !songPartMatrix.get(l).get(i-1).equals(songPartMatrix.get(l).get(i)))) {
                    //todo check how many notes are being played. If >6 check for duplicates, else assign the first 6
                    //todo start by assigning highest pitch note to highest pitch string (strings may not be in pitch order if in a weird tuning). For now just go in order or strings
                    // if any notes have already been assigned to strings, don't assign any other notes to that string
                    boolean[] takenStrings = new boolean[guitar.getGuitarStrings().size()];
                    for (int j=0; j<line.size(); j++) {
                        if (line.get(j).getStringNo() != null) {
                            takenStrings[line.get(j).getStringNo()] = true;
                        }
                    }
                    for (int j=0; j<line.size(); j++) {
                        if (line.get(j).getStringNo() == null) {
                            boolean noteAssigned = false;
                            int k = 0;
                            // loop through the strings until the note is assigned a string
                            while (noteAssigned == false && k<guitar.getGuitarStrings().size()) {
                                // note can be played on the string and string isn't already taken
                                if (line.get(j).getMidiPitch() >= guitar.getGuitarStrings().get(k).getOpenMidiPitch() && takenStrings[k] == false) {
                                    line.get(j).setStringNo(k);
                                    line.get(j).setFretNo(line.get(j).getMidiPitch() - guitar.getGuitarStrings().get(k).getOpenMidiPitch());
                                    takenStrings[k] = true;
                                    noteAssigned = true;
                                    break;
                                }
                                k++;
                            }
                        }
 
                    } //todo idea::: sort by start time using sort method from above^^
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
                        if (newMeasure == true) {
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
                        while (aNote == false && offset + j <= numNotes) {
                            System.out.println((offset+j) + "  " + offset + " " + j + "  " + songPartMatrix.get(l).get(i).get(j).getMeasure() + "  " + numNotes);
                            thisnote = thismeasure.getElementsByTag("note").get(offset + j);
                            //ignore rests (they don't have a pitch) and tab notes
                            if (thisnote.select("staff").isEmpty() || thisnote.getElementsByTag("staff").first().ownText() == "1") { 
                                if (thisnote.getElementsByTag("pitch").isEmpty()) {
                                    aRest = true;
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
                            // find the amount of time needed to go back to insert the tab version
                            int durationTime = 0;
                            try {
                                durationTime = Integer.parseInt(tabNote.getElementsByTag("duration").first().ownText());
                            }
                            catch (NumberFormatException e) {
                                // This is thrown when the staff tag doesn't contain an integer
                                System.out.println("Invalid String");
                            }
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
                            tabNote.getElementsByTag("beam").empty();
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
        // return songPartMatrix;
        return destFileName;

    }

}
