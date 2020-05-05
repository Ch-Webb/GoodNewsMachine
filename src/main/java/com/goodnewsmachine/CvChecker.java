package com.goodnewsmachine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CvChecker {
    //Using hashmaps to store words for efficiency
    //instead of running a flat .contains on an arraylist, may as well separate
    //the terms based on their first letter - makes searching so much more efficient
    private final HashMap<Character, ArrayList<String>> badCV;
    private final HashMap<Character, ArrayList<String>> goodCV;

    public CvChecker() {
        //Initialise hashmaps and a buffered reader
        badCV = new HashMap<>();
        goodCV = new HashMap<>();
        BufferedReader b;
        try {
            //When packaging to jar, you've got to use a different type of file handling
            //FileReader() traverses the local filesystem - can't use that in a zip
            //getResourceAsStream() finds the file you need in the resources folder
            b = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/bad_cv.csv")));
            //Split the csv down by the delimiter ","
            String[] badWords = b.readLine().split(",");
            //For each word in the cv
            for(String word : badWords) {
                //Get the first character of each word
                Character startChar = word.charAt(0);
                //Check if it's already a key in the hashmap
                if (!badCV.containsKey(startChar)) {
                    //If it's not, add it + a new arraylist for the words
                    ArrayList<String> words = new ArrayList<>();
                    words.add(word);
                    badCV.put(startChar, words);
                } else {
                    //If it is, add the word to the existing arraylist
                    badCV.get(startChar).add(word);
                }
            }

            //Effectively a copy of the code above but with a couple of variable names and the file path changed
            //Separating into a method would mean adding more hashmaps and wouldn't really be effective
            b = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/good_cv.csv")));
            String[] goodWords = b.readLine().split(",");
            for(String word : goodWords) {
                Character startChar = word.charAt(0);
                if (!goodCV.containsKey(startChar)) {
                    ArrayList<String> words = new ArrayList<>();
                    words.add(word);
                    goodCV.put(startChar, words);
                } else {
                    goodCV.get(startChar).add(word);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //DEBUG
    public boolean checkPositivity(String headline) {
        if (headline.equals("")) {
            return false;
        }
        System.out.println("Headline:" + headline);
        int count = 0;
        for(String word: headline.split(" ")) {
            System.out.println(word);
            Character c = word.charAt(0);
            if (goodCV.get(c) != null && goodCV.get(c).contains(word)) {
                System.out.println("Word in goodCV: " + word);
                count++;
            }
            else if(badCV.get(c) != null && badCV.get(c).contains(word)) {
                System.out.println("Word in badCV: " + word);
                count--;
            }
        }
        return count > 0;
    }


}
