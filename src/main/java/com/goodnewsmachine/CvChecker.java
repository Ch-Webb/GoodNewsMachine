package com.goodnewsmachine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CvChecker {
    private final HashMap<Character, ArrayList<String>> badCV;
    private HashMap<Character, ArrayList<String>> goodCV;

    public CvChecker() {
        badCV = new HashMap<>();
        BufferedReader b;
        try {
            b = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/bad_cv.csv")));
            String[] badWords = b.readLine().split(",");
            for(String word : badWords) {
                Character startChar = word.charAt(0);
                if (!badCV.containsKey(startChar)) {
                    ArrayList<String> words = new ArrayList<>();
                    words.add(word);
                    badCV.put(startChar, words);
                } else {
                    badCV.get(startChar).add(word);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkPositivity(String headline) {
        String beans = badCV.get("a").get(0);
        return true;
    }


}
