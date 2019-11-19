package com.example.test4;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ImageView worldView;        //Image view to show the map
    private DPad dPad;                      //Controls for walking

    private Character[] characters = new Character[4]; //all 5 characters in the game
    private Portal[] portals = new Portal[6]; // 7 portals in the map

    private int moveDist; //the distance the map moves, when dPad button is clicked

    //Variables used for tracking story progress
    private boolean talkedToNiels = false;
    private boolean gotBread = false;
    private boolean gotMilk = false;

    Character characterTalkingToYou; //The character that is talking to the player

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // put the View into a normal fullscreen mode
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setupWorld();
    }

    public void setupWorld(){
        worldView = findViewById(R.id.world_view); // View for the map
        dPad = new DPad(worldView, this); // dpad made for the interaction
        createCharacters();
        createPortals();
        setSizeForAnswerScrollView(); // make sure that both ImageView and ScrollView for the answer field are the same height
        applyFonts();

        ViewGroup.LayoutParams params = worldView.getLayoutParams();
        params.width = 64 + moveDist * 38; // set map's width to be according to the movement distance
        params.height = 64 + moveDist * 20; // set map's height to be according to the movement distance
        // existing height is ok as it is, so no need to edit it
        worldView.setLayoutParams(params);

        dPad.showDPad(); // make all the dPad Views visible
    }

    private void createCharacters()
    {
        // set the Niels character
        ConversationController[] conversations = new ConversationController[2];         // set the amount of conversations this character will have
        int[] exchangeIndexes = {0, 1, 2};                                                    // give the exchange indexes for the first conversation
        conversations[0] = new ConversationController(exchangeIndexes , this, "Niels");   // create the first conversation
        exchangeIndexes = new int[] {11, 12, 13, 14};                                                      // give exchange indexes for the second conversation
        conversations[1] = new ConversationController(exchangeIndexes , this, "Niels");   // create the second conversation
        characters[0] = new Character("Niels", 30, 16, conversations, this);// create the character

        // set the Old man character
        conversations = new ConversationController[1];
        exchangeIndexes = new int[] {3,4};
        conversations[0] = new ConversationController(exchangeIndexes , this, "Old");
        characters[1] = new Character("Old", 14, 14, conversations, this);

        // set the Clerk character
        conversations = new ConversationController[1];
        exchangeIndexes = new int[] {5, 6, 7};
        conversations[0] = new ConversationController(exchangeIndexes , this, "Clerk");
        characters[2] = new Character("Clerk", 27, 4, conversations, this);

        // set the Baker character
        conversations = new ConversationController[1];
        exchangeIndexes = new int[] {8, 9, 10};
        conversations[0] = new ConversationController(exchangeIndexes , this, "Baker");
        characters[3] = new Character("Baker", 33, 4, conversations, this);
    }

    private void createPortals()
    {
        //Find movement distance
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics); //take the metrics of the default screen
        int width = displayMetrics.widthPixels; // take the width of the screen in pixels
        moveDist = width/4; // movement distance is 1/4 of the screen width

        //portal outside Niels home
        // location is in grid system
        int portalX = 16;
        int portalY = 6;

        //locations of x and y in the system
        int newGridX = 31;
        int newGridY = 17;

        // calculates an offset for the player's ImageView to stay in the middle
        int offsetX = (newGridX - portalX) * moveDist;
        int offsetY = (newGridY - (portalY + 1)) * moveDist;
        portals[0] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);

        //portal inside niels home
        portalX = 31;
        portalY = 18;
        newGridX = 16;
        newGridY = 7;
        offsetX = (newGridX - portalX) * moveDist;
        offsetY = (newGridY - (portalY - 1)) * moveDist;
        portals[1] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);

        //left door to shop
        portalX = 6;
        portalY = 12;
        newGridX = 29;
        newGridY = 8;
        offsetX = (newGridX - portalX) * moveDist;
        offsetY = (newGridY - (portalY + 1)) * moveDist;
        portals[2] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);

        //right door to shop
        portalX = 7;
        portalY = 12;
        newGridX = 30;
        newGridY = 8;
        offsetX = (newGridX - portalX) * moveDist;
        offsetY = (newGridY - (portalY + 1)) * moveDist;
        portals[4] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);

        //left door exit from shop
        portalX = 29;
        portalY = 9;
        newGridX = 6;
        newGridY = 13;
        offsetX = (newGridX - portalX) * moveDist;
        offsetY = (newGridY - (portalY - 1)) * moveDist;
        portals[3] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);

        //right door exit from shop
        portalX = 30;
        portalY = 9;
        newGridX = 7;
        newGridY = 13;
        offsetX = (newGridX - portalX) * moveDist;
        offsetY = (newGridY - (portalY - 1)) * moveDist;
        portals[5] = new Portal(worldView, portalX, portalY, offsetX, offsetY, newGridX , newGridY);
    }

    public Portal getPortalAt(int x, int y)
    //Returns portal located at given coordinates
    {
        for(int i = 0; i < 6; i++)
        {
            if(portals[i].getXGrid() == x && portals[i].getYGrid() == y)
            {
                return portals[i];
            }
        }
        throw new Error("Portal not found");
    }

    public Character getCharacterAt(int x, int y)
    //Returns character located at given coordinates
    {
        for(int i = 0; i < 4; i++)
        {
            if(characters[i].getXGrid() == x && characters[i].getYGrid() == y)
            {
                return characters[i];
            }
        }
        throw new Error("Character not found");
    }



    public void moveCharacterUp(final View v)
    {
        dPad.moveUp();
        disableDPadFor();
        dPad.switchDpadToConversation();
    }

    public void moveCharacterDown(final View v)
    {
        dPad.moveDown();
        disableDPadFor();
        dPad.switchDpadToConversation();
    }

    public void moveCharacterLeft(final View v)
    {
        dPad.moveLeft();
        disableDPadFor();
        dPad.switchDpadToConversation();
    }

    public void moveCharacterRight(final View v)
    {
        dPad.moveRight();
        disableDPadFor();
        dPad.switchDpadToConversation();
    }



    public void answerClick(View view) {
        characterTalkingToYou.getCurrentConversationController().getCurrentExchange().addAnswer(view);
    }

    public void onSubmitClick(View view) {
        characterTalkingToYou.getCurrentConversationController().getCurrentExchange().submitAnswer(view);
    }

    public void onSoundViewClick(View v) {
        String audioFileName = "sentence" + characterTalkingToYou.getCurrentConversationController().getCurrentExchangeID();
        int idOfAudioFile = getResources().getIdentifier(audioFileName, "raw", getPackageName());
        characterTalkingToYou.getCurrentConversationController().getCurrentExchange().playSound(idOfAudioFile);
    }

    public void exitConversation(View view) {
        characterTalkingToYou.getCurrentConversationController().hideConversationElements();
        dPad.showDPad();
    }

    public void disableDPadFor()
    {
        final View v = findViewById(R.id.up_button);
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, dPad.getAnimationLength());
        final View v2 = findViewById(R.id.down_button);
        v2.setEnabled(false);
        v2.postDelayed(new Runnable() {
            @Override
            public void run() {
                v2.setEnabled(true);
            }
        }, dPad.getAnimationLength());
        final View v3 = findViewById(R.id.left_button);
        v3.setEnabled(false);
        v3.postDelayed(new Runnable() {
            @Override
            public void run() {
                v3.setEnabled(true);
            }
        }, dPad.getAnimationLength());
        final View v4 = findViewById(R.id.right_button);
        v4.setEnabled(false);
        v4.postDelayed(new Runnable() {
            @Override
            public void run() {
                v4.setEnabled(true);
            }
        }, dPad.getAnimationLength());
    }

    private void setSizeForAnswerScrollView(){
        //make the size of the ImageView equal to the ScrollView of the answer text
        ImageView answer_text_field = findViewById(R.id.answer_text_field);
        ScrollView answer_scrollview = findViewById(R.id.answer_scrollview);
        answer_scrollview.getLayoutParams().height = answer_text_field.getHeight();
    }

    private void applyFonts() {
        // take a downloaded font type
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MerchantCopyMod.ttf");

        // take all the TextViews in the layout that this font type needs to be put to
        TextView[] toChangeFont = {findViewById(R.id.answer_text),
                findViewById(R.id.dialogue_text),
                findViewById(R.id.answer_button_text_0),
                findViewById(R.id.answer_button_text_1),
                findViewById(R.id.answer_button_text_2),
                findViewById(R.id.answer_button_text_3),
                findViewById(R.id.answer_button_text_4),
                findViewById(R.id.answer_button_text_5)};

        // apply the font
        for (TextView t : toChangeFont) {
            t.setTypeface(font);
        }
    }

    public boolean isGotBread() {
        return gotBread;
    }

    public boolean isGotMilk() {
        return gotMilk;
    }
    public boolean isTalkedToNiels() {
        return talkedToNiels;
    }

    public void setGotBread(boolean got)
    {
        gotBread = got;
    }
    public void setGotMilk(boolean got)
    {
        gotMilk = got;
    }
    public void setTalkedToNiels(boolean talked)
    {
        talkedToNiels = talked;
    }

    public ImageView getHintImage() {
        ImageView hintImage = findViewById(R.id.hint_img);
        return hintImage;
    }
    public DPad getDPad()
    {
        return dPad;
    }
}