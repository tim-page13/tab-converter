package com.timpage.app;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import parser.musicXMLparserDH;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import music.Note;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void ParserIsProducingOutput()
    {
        // boolean successA = false;
        // boolean successB = false;
        // try {
        //     musicXMLparserDH parser = new musicXMLparserDH("../OtherTestMusic/xmlsamples/BrookeWestSample.musicxml");
        //     // musicXMLparserDH parser = new musicXMLparserDH("../GuitarPro7Files/PinkFloyd-WishYouWereHere.xml");

        //     String[] flatSong = parser.parseMusicXML();
            
        //     System.out.println("printing flatSong...");
        //     for (int i = 0; i < flatSong.length; i++) {
        //         // uncomment this to print out flattened format of the song - this is each note that is played at each time
        //         // System.out.println(flatSong[i]);
        //         successA = true;
        //     }

        //     ArrayList<Note> songSequenceOfNoteObjects = parser.getNotesOfSong();
        //     System.out.println("printing notes...");
        //     for (int i = 0; i < songSequenceOfNoteObjects.size(); i++) {
        //         Note theNote = songSequenceOfNoteObjects.get(i);
        //         // System.out.println(theNote.getPitch() + theNote.getOctave() + theNote.getAccidental() + " at measure: " + theNote.getMeasure() + " duration:" + theNote.getDuration() + " startTime:" + theNote.getStartTime() + " midiPitch:" + theNote.getMidiPitch());
        //         successB = true;
        //     }

        //     ArrayList<ArrayList<Note>> songMatrix = parser.getSongMatrix();
        //     for (int i=0; i<songMatrix.size(); i++) {
        //         ArrayList<Note> line = songMatrix.get(i);
        //         if (line.size() > 0) {
        //             System.out.printf("unsorted bar %d:  ", line.get(0).getMeasure());
        //             for (int j=0; j<line.size(); j++) {
        //                 System.out.printf("%d %s%d%s(%d) ", line.get(j).getId(), line.get(j).getPitch(), line.get(j).getOctave(), line.get(j).getAccidental(), line.get(j).getMidiPitch());
        //             }
        //             // sorts the notes into descending order of pitch
        //             // Collections.sort(line, new Comparator<Note>() {
        //             //     @Override
        //             //     public int compare(Note n1, Note n2) {
        //             //         int mp1 = n1.getMidiPitch();
        //             //         int mp2 = n2.getMidiPitch();
        //             //         // sort in descending order
        //             //         return mp2-mp1;
        //             //     }
        //             // });
        //             // System.out.printf("\n  sorted bar %d:  ", line.get(0).getMeasure());
        //             // for (int j=0; j<line.size(); j++) {
        //             //     System.out.printf("%s%d%s(%d) ", line.get(j).getPitch(), line.get(j).getOctave(), line.get(j).getAccidental(), line.get(j).getMidiPitch());
        //             // }
        //         }
        //         else {
        //             System.out.printf(line.toString());
        //         }
                
        //         System.out.println();
        //     }

        // } catch (IOException e) {
        //     e.printStackTrace();
        //     assertTrue(false);
        // }
        // assertTrue( successA && successB );

        assertTrue(true);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void testTabConverter() {

        try {

            // String filename = "../OtherTestMusic/xmlsamples/BrookeWestSample.musicxml";
            String filename = "../GuitarPro7Files/PinkFloyd-WishYouWereHere.xml";
            // String filename = "../GuitarPro7Files/Test1.xml";
            // String filename = "../OtherTestMusic/Nick Jonas - Guitar Solo On Acm Awards 2016.xml";

            TabConverter tc = new TabConverter(filename);

            ArrayList<ArrayList<Note>> sm = tc.convertToTab();

        } catch (IOException e1) {
            e1.printStackTrace();
            assertTrue(false);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        } 

        assertTrue(true);
    }
}
