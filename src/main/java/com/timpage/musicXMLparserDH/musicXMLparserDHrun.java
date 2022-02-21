package com.timpage.musicXMLparserDH;
import java.io.IOException;
import java.util.ArrayList;
import com.timpage.musicXMLparserDH.parser.musicXMLparserDH;
import com.timpage.musicXMLparserDH.music.Note;

/**
 * Created by Dorien Herremans on 05/02/15.
 */

public class musicXMLparserDHrun {

        public static void main (String[]args)throws IOException {

            System.out.println("Reading in file... ");
            String[] songSequence = null;
            String[] songSequenceParsed = null;
            String filename = "";

            if (args.length > 0) {
                filename = args[0];
                System.out.println("Reading in " + filename);

            } else {
                System.out.print("You did not specify any filename as on option.");
                System.exit(0);
            }
          //filename = "./data/test.xml";

            musicXMLparserDH parser = new musicXMLparserDH(filename);



            //prints out the note sounding at the same slice (each division of the musicxml file
            String[] flatSong = parser.parseMusicXML();

            //print out the songI j
            if (args.length > 1) {
                System.out.println("printing flatSong...");
                if (args[1].equals("-print")) {
                    for (int i = 0; i < flatSong.length; i++) {
                        // uncomment this to print out flattened format of the song
                        // System.out.println(flatSong[i]);
                    }
                }
            }


            //returns an ArrayList containing all the note objects                  // commented out by Tim
            ArrayList<Note> songSequenceOfNoteObjects = parser.getNotesOfSong();
             //print out the songI j
             if (args.length > 1) {
                System.out.println("printing flatSong...");
                if (args[1].equals("-print")) {
                    for (int i = 0; i < songSequenceOfNoteObjects.size(); i++) {
                        Note theNote = songSequenceOfNoteObjects.get(i);
                        System.out.println(theNote.getPitch() + theNote.getOctave() + theNote.getAccidental() + " at " + theNote.getMeasure() + ":" + theNote.getStartTime());
                    }
                }
            }


            System.out.print("test");


        }
    }
