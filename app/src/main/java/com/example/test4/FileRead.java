package com.example.test4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileRead {

    private StringBuffer questionText; //normal text of other characters
    private StringBuffer answerText; // text, which player says as an answer
    private StringBuffer[] allAnswers; //array of all possible answer options

    private InputStream inputStream; //the text file which is read from

    FileRead (InputStream inputStream){
        this.inputStream = inputStream;
    }

    void read() {
        StringBuffer[] allText = readAll();     //all the text in the file
        questionText = readQuestion(allText);
        answerText = readAnswer(allText);
        allAnswers = readAllAnswers(allText);
    }

    //reads all of the file and puts every line in the array of Strings
    StringBuffer[] readAll()
    {
        String newLine;
        StringBuffer[] lines = new StringBuffer[8];
        int lineIndex = 0;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((newLine = bufferedReader.readLine()) != null) {
                lines[lineIndex] = new StringBuffer();
                lines[lineIndex].append(newLine);
                lineIndex++;
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    StringBuffer readQuestion(StringBuffer[] text)
    {
        StringBuffer question = text[0];
        return question;
    }

    StringBuffer readAnswer(StringBuffer[] text)
    {
        StringBuffer answer = text[1];
        return answer;
    }

    StringBuffer[] readAllAnswers(StringBuffer[] text)
    {
        StringBuffer[] answers = new StringBuffer[6];
        for (int i = 2; i < text.length; i++){ //starts from 2 because text of answers start from third line in txt file
            answers[i-2] = text[i];
        }
        return answers;
    }

    StringBuffer getAnswerText() {
        return answerText;
    }

    StringBuffer[] getAllAnswers() {
        return allAnswers;
    }

    StringBuffer getQuestionText() {
        return questionText;
    }

    char[][] readStructureChars()
    //Reads the map file
    {
        String newLine; //each new read line
        StringBuffer oldLine = new StringBuffer();
        char[][] mapTiles = new char[21][40]; //the 2 dimensional array to be returned to the activity
        int lineIndex = 0; // lines starting from 0 to be read afterwards one by one
        try {
            // creates a bufferedReader to be able to read a file from given inputStream (text file)
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //while newLine (line taken from the txt file) is not null
            while ((newLine = bufferedReader.readLine()) != null) {

                oldLine.replace(0, oldLine.length(), newLine); //replaces previous line with the new line
                for(int i=0; i<oldLine.length(); i++){ //goes from the first char to the length of the String line
                    if (oldLine.length() != 0) {
                        mapTiles[lineIndex][i] = oldLine.charAt(i);
                    }else break;
                }

                lineIndex++; //takes next line
            }
            inputStream.close(); //closing the file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapTiles;
    }
}