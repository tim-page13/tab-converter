package com.timpage.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import music.Note;
import parser.musicXMLparserDH;
import com.timpage.app.Guitar;


public class TabConverter {
    
    private musicXMLparserDH parser;
    private ArrayList<ArrayList<Note>> songMatrix;
    private Guitar guitar;


    public TabConverter(String filename) throws IOException {
        try {

            parser = new musicXMLparserDH(filename);
            parser.parseMusicXML();
            songMatrix = parser.getSongMatrix();
            // System.out.println("filename: " + filename);
            guitar = new Guitar();
        } catch (IOException e) {
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
                    // 
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

    // public static void sort(ArrayList<Note> list) {

    // }

}
