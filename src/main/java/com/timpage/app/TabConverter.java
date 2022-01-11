package com.timpage.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedWriter;
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
import java.nio.channels.FileChannel;

import music.Note;
import parser.musicXMLparserDH;
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

            parser = new musicXMLparserDH(filename);
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

    private void WriteFile() {
        BufferedWriter  writer = null;
        try {
            writer = new BufferedWriter( new FileWriter(destFileName));
            writer.write(doc.outerHtml());
            writer.close();
        }
        catch ( IOException e) {
            System.out.println(e);
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
    public ArrayList<ArrayList<ArrayList<Note>>> convertPartsToTab() {
        // iterate through each part
        for (int l=0; l<songPartMatrix.size(); l++) {
            // add xml tags for the TAB clef and stave information
            Element partAttributes = this.doc.select("part").get(l).getElementsByTag("measure").first().getElementsByTag("attributes").first();
            Element clef = new Element("clef");
            partAttributes.getElementsByTag("clef").last().after(clef);
            clef.attr("number", ""+(this.doc.select("part").get(l).getElementsByTag("clef").size()+1));
            Element sign = new Element("sign");
            sign.append("TAB");
            clef.appendChild(sign);
            Element lineAttr = new Element("line");
            lineAttr.append("5");
            clef.appendChild(lineAttr);
            Element staffDetails = new Element("staff-details");
            clef.after(staffDetails);
            Element staffLines = new Element("staff-lines");
            staffLines.append(""+guitar.getGuitarStrings().size());
            staffDetails.appendChild(staffLines);
            for (int j=0; j<guitar.getGuitarStrings().size(); j++) {
                Element staffTuning = new Element("staff-tuning");
                staffTuning.attr("line", ""+guitar.getGuitarStrings().get(j));
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
 
                    } //todo idea::: sort by start time using sort method from above^^
                    int offset = 0; // is this line in the wrong place?
                    // once all string/fret assignments are made, write them to the file
                    for (int j=0; j<songPartMatrix.get(l).get(i).size(); j++) {
                        // gets the right note within the measure using the unsorted matrix
                        Element thismeasure = this.doc.select("part").get(l).getElementsByTag("measure").get((songPartMatrix.get(l).get(i).get(j).getMeasure()-1));
                        boolean aNote = false;
                        Element thisnote = thismeasure.getElementsByTag("note").get(offset + j);
                        while (aNote == false && offset + j < thismeasure.getElementsByTag("note").size()) {
                            thisnote = thismeasure.getElementsByTag("note").get(offset + j);
                            //ignore rests - they don't have a pitch
                            if (!thisnote.getElementsByTag("pitch").isEmpty() && thisnote.select("string").isEmpty()) { 
                                aNote = true; // a note has been found in the bar
                                break;
                            }
                            else {
                                offset++;
                            }
                        }
                        if (aNote) {
                            // add tags to the note if missing
                            if (thisnote.getElementsByTag("notations").isEmpty()) {
                                thisnote.appendElement("notations").appendElement("technical");
                            }
                            if (thisnote.getElementsByTag("notations").get(0).getElementsByTag("technical").isEmpty()) {
                                thisnote.getElementsByTag("notations").get(0).appendElement("technical");
                            }
                            Element technical = thisnote.getElementsByTag("notations").get(0).getElementsByTag("technical").get(0);
                            // the string and fret data 
                            technical.appendElement("string").append((songPartMatrix.get(l).get(i).get(j).getStringNo()+1) + "");
                            technical.appendElement("fret").append(songPartMatrix.get(l).get(i).get(j).getFretNo() + "");
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
        return songPartMatrix;

    }

}
