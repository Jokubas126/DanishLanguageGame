package com.example.test4;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Exchange {
    //Visual elements
    //----------------------------------------------------------------------------------------------------------------
    private TextView answerTextView;            //Text view to hold the text of the user/player
    private TextView dialogueTextView;          //Text view to hold the text of the NPC
    private TextView[] answerButtonsTextView = new TextView[6];     //Text views of the possible answers
    private ImageView answerBackground;     //Background for the answer text
    private AnimationDrawable backgroundIncorrect; //Changes the background to show that the answer is incorrect

    //Utility variables
    //----------------------------------------------------------------------------------------------------------------
    private MediaPlayer mediaPlayer;      //Media player object used to play sound
    private MainActivity mainActivity;
    private ConversationController parentConversationController; //Conversation controller this exchange belongs to
    private FileRead fileRead; //Creates the file object for all the Strings to be created there


    //NPC text variables
    //----------------------------------------------------------------------------------------------------------------
    private StringBuffer questionText; //Question text with clickable hint buttons
    private ArrayList<String> hintWordList = new ArrayList<>();             //Strings of hint words. Used when building clickable hints.
    private ArrayList<Integer> hintWordIndexList = new ArrayList<>();       //Indexes of these words
    private int hintWordCount = 0;          //Current count of hint words added to the question text

    //Player's answer variables
    //----------------------------------------------------------------------------------------------------------------
    private StringBuffer answerText; //Answer test with clickable buttons for removing a selected answer

    private String fullAnswer;                                  //The full answer text that has the player's selected answers in it(without buttons)
    private int answerSlotCount = 0;                         //The amount of slots to put selected answers in
    private SelectedAnswer[] selectedAnswers = new SelectedAnswer[6];       //Answers that are currently selected by the player

    private int[] correctAnswers;           //Correct answer indexes
    private String usersAnswerUnchanged;    //User's unedited answer to the NPC
    private int answerIndex;            //Index used to know the index of the selected answer when putting it in as an answer
    //----------------------------------------------------------------------------------------------------------------

    Exchange(int exchangeID, MainActivity mainActivity, ConversationController conversationController) {
        this.mainActivity = mainActivity;
        this.parentConversationController = conversationController;

        this.dialogueTextView = mainActivity.findViewById(R.id.dialogue_text);
        this.answerTextView = mainActivity.findViewById(R.id.answer_text);

        for (int i = 0; i < answerButtonsTextView.length; i++) {
            String number = Integer.toString(i);
            String viewText = "answer_button_text_" + number;
            int textViewId = mainActivity.getResources().getIdentifier(viewText, "id", mainActivity.getPackageName());
            this.answerButtonsTextView[i] = mainActivity.findViewById(textViewId);
        }
        loadExchange(exchangeID);
    }

    //Utility functions
    //----------------------------------------------------------------------------------------------
    private void loadExchange(int exchangeID)
    //Loads the exchange from file
    //exchangeId - exchange to be loaded
    {
        //Finding the correct exchange file to read
        String fileIndex = Integer.toString(exchangeID); //use if it complains about using integer in the String in the following line
        String IDToString = "exchange" + fileIndex; //creates a String name of the file to use in the following line
        int fileID = mainActivity.getResources().getIdentifier(IDToString,"raw", mainActivity.getPackageName());
        InputStream inputStream = (mainActivity.getResources().openRawResource(fileID));

        //Reading the file
        fileRead = new FileRead(inputStream);
        fileRead.read();
        questionText = fileRead.getQuestionText();
        answerText = fileRead.getAnswerText();

        //creates exchange object which consists of all the Strings to be put in that one created exchange
        dialogueTextView.setText(addHintsToQuestionText(questionText));
        dialogueTextView.setMovementMethod(LinkMovementMethod.getInstance()); //Make the hint buttons in NPCs' text clickable
        answerTextView.setText(addSlotsToAnswerText(answerText));
        StringBuffer[] answers;
        answers = fileRead.getAllAnswers();
        for (int j = 0; j < answerButtonsTextView.length; j++){
            answerButtonsTextView[j].setText(findAnswer(j,answers));
        }

        resetSelectedAnswers();
        prepareStringForAddingWords();
    }

    private String englishifize(String word)
    //Returns a string that has all the danish and capital letters changed into english and non capital ones. Used for finding the correct audio or image file
    //word - the potentialy danish word to change
    {
        word = word.replaceAll("æ", "ae");
        word = word.replaceAll("ø", "o");
        word = word.replaceAll("å", "aa");
        word = word.replaceAll("Æ", "Ae");
        word = word.replaceAll("Ø", "O");
        word = word.replaceAll("Å", "Aa");
        word = word.toLowerCase();
        return word;
    }

    void playSound(int idOfAudioFile)
    //plays given audio file
    {
        if(mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = MediaPlayer.create(mainActivity, idOfAudioFile);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0); //puts the audio to the beginning
        } else mediaPlayer.start();
    }

    //User's answer related methods
    //----------------------------------------------------------------------------------------------

    private SpannableString addSlotsToAnswerText(StringBuffer answerText)
    //Adds slots where all 6 answers should be and saves the IDs of correct answers
    {
        ArrayList<Integer> ansWordIndexList = new ArrayList<>();            //Indexes that show where each answer should go.
        final Matcher matcher = Pattern.compile("&\\s*(\\w+)").matcher(answerText);
        while (matcher.find()){
            final String word = matcher.group(1);
            int wordIndex = answerText.indexOf(word) - 1;
            ansWordIndexList.add(wordIndex);
        }
        correctAnswers = new int[ansWordIndexList.size()];

        String gap = "____";
        //removing & (button markers) and taking numbers of correct answers
        int index = 0;
        for (int i = 0; i < ansWordIndexList.size(); i++){
            answerText.deleteCharAt(ansWordIndexList.get(i) - i + (gap.length() - 1) * i);
            char number = answerText.charAt(ansWordIndexList.get(i) - i + (gap.length() - 1) * i);
            correctAnswers[index] = java.lang.Character.getNumericValue(number);
            answerText.deleteCharAt(ansWordIndexList.get(i) - i + (gap.length() - 1) * i);
            answerText.insert(ansWordIndexList.get(i) - i + (gap.length() - 1) * i, gap);
            index++;
        }
        SpannableString spannableString = new SpannableString(answerText);

        usersAnswerUnchanged = spannableString.toString();
        return spannableString;
    }

    private void resetSelectedAnswers()
    //Resets the selected answers to their default state, as if no answers were selected.
    {
        for(int i = 0; i < 6; i++)
        {
            selectedAnswers[i] = new SelectedAnswer("____");
        }
    }

    void submitAnswer(View view)
    //What happens when the submit button is clicked
    {
        answerBackground = mainActivity.findViewById(R.id.answer_text_field);
        if (checkAnswer())      //If the answer is correct
        {
            //Animate that the answer is correct
            answerBackground.setImageResource(R.drawable.answer_correct);
            backgroundIncorrect = (AnimationDrawable) answerBackground.getDrawable();
            backgroundIncorrect.start();

            //Play sound signaling that answer was correct
            int idOfAudioFile = mainActivity.getResources().getIdentifier("correct", "raw", mainActivity.getPackageName());
            playSound(idOfAudioFile);

            parentConversationController.nextExchange();
        }
        else
        {
            //Animate that the answer is incorrect
            answerBackground.setImageResource(R.drawable.answer_wrong);
            backgroundIncorrect = (AnimationDrawable) answerBackground.getDrawable();
            backgroundIncorrect.start();

            //Play sound signaling that answer was incorrect
            int idOfAudioFile = mainActivity.getResources().getIdentifier("incorrect", "raw", mainActivity.getPackageName());
            playSound(idOfAudioFile);

            resetAllWordInputFields();
            showAnswerText();       //The answer text is updated to show that its slots are empty
        }
    }
    SpannableString findAnswer(int answerIndex, StringBuffer[] answers)
    //Returns the selected answer from the array of answers
    {
        StringBuffer stringBuffer = answers[answerIndex];
        SpannableString spannableString = new SpannableString(stringBuffer);
        return spannableString;
    }

    void addAnswer(View view)
    //Add clicked answers text to the selected answers
    {
        String answerTextToPut = getTextOfClickedAnswerButton(view);
        answerTextView.setMovementMethod(LinkMovementMethod.getInstance()); //Make the text view clickable. This enables us to add ClickableSpan to this Text View
        prepareStringForAddingWords();

        if(answerTextView.getText().toString().contains("____")) //If there is a slot to put the new word in
        {
            putWordInSlot(answerTextToPut, answerIndex);  //Put the appropriate answer word in the first available slot
            SpannableString spanString = buildAnswerTextSpannableString(selectedAnswers, fullAnswer, answerSlotCount); //Build the string by adding the clickable parts to it
            answerTextView.setText(spanString);
        }
    }
    private void prepareStringForAddingWords()
    //reseting the full answer to represent the original text, giving each slot its own number, counting the amount of slots
    {
        fullAnswer = usersAnswerUnchanged;        //reseting the full answer to represent the original text
        answerSlotCount = 0;
        String tempString = fullAnswer;
        while((tempString).contains("____"))
        {
            tempString = tempString.substring(tempString.indexOf("____") + 4);          //Taking the part of the string that does not have the already found "____"
            fullAnswer = fullAnswer.replaceFirst("____", "#" + answerSlotCount);        //giving each slot its own number
            answerSlotCount++;                                  //counting the amount of slots
        }
    }

    private void putWordInSlot(String ans, int index)
    //Sets and empty selected answer slot to a specified string
    {
        for(int i = 0; i < answerSlotCount; i++)
        {
            if(selectedAnswers[i].word == "____") {
                selectedAnswers[i].word = ans;
                selectedAnswers[i].IDForCheckingAnswer = index;
                break;
            }
        }
    }
    private void resetWordInputField(String answerToReset)
    //Remove the selected word from the list of selected words
    {
        for(int i = 0; i < answerSlotCount; i++)
        {
            if(selectedAnswers[i].word.equals(answerToReset))
            {
                selectedAnswers[i].word = "____";
                selectedAnswers[i].IDForCheckingAnswer = -1;
            }
        }
    }

    private void resetAllWordInputFields()
    {
        for(int i = 0; i < answerSlotCount; i++)
        {
            selectedAnswers[i].word = "____";
        }
    }

    private boolean checkAnswer()
    //Check if the combination of user's selected answers are correct
    {
        for(int i = 0; i < answerSlotCount; i++)
        {
            if(correctAnswers[i] != selectedAnswers[i].IDForCheckingAnswer)
            {
                return false;
            }
        }
        return true;
    }

    private void showAnswerText()
    //Update and show the answer text
    {
        prepareStringForAddingWords();
        SpannableString spanString = buildAnswerTextSpannableString(selectedAnswers, fullAnswer, answerSlotCount);
        answerTextView.setText(spanString);
    }

    private SpannableString buildAnswerTextSpannableString(SelectedAnswer[] selectedAnswers, String stringToAddTo, int answerCount)
    {
        //Placing words in string
        for(int i = 0; i < answerCount; i++)
        {
            selectedAnswers[i].answerPositionIndex = stringToAddTo.indexOf("#" + i);        //We need to remember where the string is positioned to be able to set the clickable span
            stringToAddTo = stringToAddTo.replace("#" + i, selectedAnswers[i].word);
        }

        SpannableString spannableString = new SpannableString(stringToAddTo);
        for(int i = 0; i < answerCount; i++)
        {
            if(selectedAnswers[i].word != "____")
            {
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        //Getting text of clickable span
                        //-------------------------------------
                        TextView tv = (TextView) widget;
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String clickableSpanString = s.subSequence(start, end).toString();

                        resetWordInputField(clickableSpanString);
                        showAnswerText();
                    }
                };
                spannableString.setSpan(clickableSpan, selectedAnswers[i].answerPositionIndex, selectedAnswers[i].answerPositionIndex + selectedAnswers[i].word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    private TextView findAnswerButtonTextView(View view)
    //returns the text view of the clicked answer button
    {
        ImageView answer = (ImageView) view;
        int id = answer.getId();
        String answerTextName = answer.getResources().getResourceName(id); // get string of the of what word is in the answer textView
        char takeNum = answerTextName.charAt(answerTextName.length() - 1); // take the last character of that string
        answerIndex = Integer.parseInt(String.valueOf(takeNum));   // take integer of an answer has been selected when checking if the selected answers are correct
        String textViewName = "answer_button_text_" + takeNum; //making the id name of the answer textView
        int idOfTextView = mainActivity.getResources().getIdentifier(textViewName, "id", mainActivity.getPackageName());
        return mainActivity.findViewById(idOfTextView); // return the textView needed
    }

    //NPC question related methods
    //----------------------------------------------------------------------------------------------

    private SpannableString addHintsToQuestionText(StringBuffer questionText)
    {
        //matcher is made to find all the '#', which are at the beginning of hint words in the sentence
        final Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(questionText);
        while (matcher.find()){
            final String word = matcher.group(1); // group(1) index means the method of pattern matching
            int wordIndex = questionText.indexOf(word) - 1; // adjust the index of a hint word to count from 0
            hintWordIndexList.add(wordIndex); // add index to the list
            hintWordList.add(word); // add word to another list
        }
        //removing hash tags(button markers)
        for (int i = 0; i < hintWordIndexList.size(); i++){
            questionText.deleteCharAt(hintWordIndexList.get(i) - i);
        }

        // turn the stringBuffer in to a spannable string, which lets to add clickable strings
        SpannableString spannableString = new SpannableString(questionText);

        // go through each hint word and create a clickable span(button) with it
        for (int i = 0; i < hintWordIndexList.size(); i++){
            //Amount of hints currently added.
            //We can't use i inside the definition of the onClick method override
            //Instead, we pass the required variable through a getter
            hintWordCount = i;

            //Defining the behaviour of the newly created clickable span(button); what happens when this span is clicked
            //-------------------------------------
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    //make hint view visible
                    parentConversationController.getHintImage().setVisibility(View.VISIBLE);

                    //Getting text of clickable span
                    TextView tv = (TextView) widget;        // get the textview of the question
                    Spanned s = (Spanned) tv.getText();     //get the actual text of that text view
                    int start = s.getSpanStart(this);    //find where the clickable part begins
                    int end = s.getSpanEnd(this);        //find where the clickable part ends
                    String clickableSpanString = s.subSequence(start, end).toString(); //Get the string of the clickable span

                    int currentWord = 0; //used for finding the right word to show image and play sound
                    //finding the correct image to show
                    for(int j = 0; j < hintWordCount + 1; j++)
                    {
                        // if the word in the hint word list is the same as from the clickable string
                        if(hintWordList.get(j).equalsIgnoreCase(clickableSpanString))
                        {
                            //if true, current word gets an index of j and loop breaks
                            currentWord = j;
                            break;
                        }
                    }

                    //Showing the hint image
                    int resId = mainActivity.getResources().getIdentifier(getWordFile(currentWord), "drawable", mainActivity.getPackageName());
                    mainActivity.getHintImage().setImageResource(resId);

                    //Playing the hint sound
                    int idOfAudioFile = mainActivity.getResources().getIdentifier(getWordFile(currentWord), "raw", mainActivity.getPackageName());
                    playSound(idOfAudioFile);
                }
            };
            //-------------------------------------
            //We apply the clickable span to the spannable string
            spannableString.setSpan(clickableSpan,
                    hintWordIndexList.get(i) - i,
                    hintWordIndexList.get(i) - i + hintWordList.get(i).length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //returning the text together with clickable places (spans) in it
        return spannableString;
    }

    //Getters
    //----------------------------------------------------------------------------------------------
    private String getTextOfClickedAnswerButton(View view)
    //Get the text of the clicked answer image view. This connects the image view to the text view in a way.
    {
        TextView clickedAnswerButtonView = findAnswerButtonTextView(view);
        return clickedAnswerButtonView.getText().toString();
    }

    // gets string of a file name of needed hint word
    private String getWordFile(int i)
    {
        return englishifize(hintWordList.get(i));
    }
}