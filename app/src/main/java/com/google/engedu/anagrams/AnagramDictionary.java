/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWord = new HashMap<>();
    private int currentWordLength;

    public AnagramDictionary(Reader reader) throws IOException {

        BufferedReader in = new BufferedReader(reader);
        String line;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            String sortedWord = sortLetters(word);
            wordList.add(word);   //an arraylist

            if (lettersToWord.containsKey(sortedWord)){
                lettersToWord.get(sortedWord).add(word);
            }else{
                ArrayList<String> anagramGroup = new ArrayList<>();
                anagramGroup.add(word);
                lettersToWord.put(sortedWord, anagramGroup);
            }

            Integer size = word.length();
            if(sizeToWord.containsKey(size)){
                sizeToWord.get(size).add(word);
            }
            else {
                ArrayList<String> sizeGroup = new ArrayList<>();
                sizeGroup.add(word);
                sizeToWord.put(size,sizeGroup);
            }
        }
        currentWordLength = DEFAULT_WORD_LENGTH;
    }

    public boolean isGoodWord(String word, String base) {
        return !word.contains(base);
    }


    public ArrayList<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        String sortedWord = sortLetters(targetWord);
        if (lettersToWord.containsKey(sortedWord)){
            result.addAll(Objects.requireNonNull(lettersToWord.get(sortedWord)));
        }

        return result;
    }

    public ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        int char_a = 97; //ASCII value of character a in ASCII table
        int char_z = 122; //ASCII value of character z in ASCII table

        for (int i = char_a; i < char_z; i++){
            char c = (char) i;
            String oneMoreLetteredWord = word.concat(String.valueOf(c));
            ArrayList<String> anagrams = getAnagrams(oneMoreLetteredWord);

            for (String anagram: anagrams){
                if (isGoodWord(anagram, word)){
                    result.add(anagram);
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> sizeToWordList = sizeToWord.get(currentWordLength + 1);
        String randomWord = Objects.requireNonNull(sizeToWordList).get(random.nextInt(sizeToWordList.size() - 1));

        ArrayList<String> anagrams = getAnagramsWithOneMoreLetter(sortLetters(randomWord));
        if (anagrams.size() >= MIN_NUM_ANAGRAMS){
            if (currentWordLength < MAX_WORD_LENGTH){
                currentWordLength++;
            }
            return randomWord;
        }

        return pickGoodStarterWord();
    }

    public String sortLetters(String inputString){
        char[] charInput = inputString.toCharArray();
        Arrays.sort(charInput);
        return new String(charInput);
    }
}
