package com.example.test4;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

class DPad {
    private int moveDist;                   //Travel distance of player in pixels, when a walk button is pressed
    private ImageView worldView;
    private int animationLength = 500;      //Length of the walk animation in milliseconds
    private float moveX;
    private float moveY;                    //Float for moveX/moveY for animation
    private int startX;                     //Starting X coordinate of the map in pixels
    private int startY;                     //Starting Y coordinate of the map in pixels
    private PlayerObject player;

    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    private ImageView dPadBackground;       //Image of the area where the DPad is displayed
    private ImageView dPadImageView;        //To be changed later will be divided by 4

    //for the dpad pressed animations
    private AnimationDrawable pressingUp;
    private AnimationDrawable pressingDown;
    private AnimationDrawable pressingLeft;
    private AnimationDrawable pressingRight;

    //for dpad animations when starting conversation
    private AnimationDrawable conversationPressingUp;
    private AnimationDrawable conversationPressingLeft;
    private AnimationDrawable conversationPressingRight;

    private char[][] mapTiles; //2D array for the map made from map_structure file

    private MainActivity mainActivity;


    DPad (ImageView worldView, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        player = new PlayerObject(mainActivity); //creates a player
        loadMapStructure();
        moveX =  worldView.getX();
        moveY =  worldView.getY();
        this.worldView = worldView;

        //Moving distance is set to be a quarter of the screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        moveDist = width/4;

        startX = (int)Math.round(-80 -moveDist * 15.5); //This value is set manually, to fit the testing device
        startY = (int)Math.round( -moveDist * 8);  //This value is set manually, to fit the testing device

        //Here we are setting specific coordinates for the world map (So character is located at his starting position)
        ObjectAnimator animation = ObjectAnimator.ofFloat(worldView, "y", worldView.getY(), startY);
        animation.setDuration(0);
        animation.start();
        animation = ObjectAnimator.ofFloat(worldView, "X", worldView.getX(), startX);
        animation.setDuration(0);
        animation.start();

        //All DPad elements assigned their ImageView/Button
        upButton = mainActivity.findViewById(R.id.up_button);
        downButton = mainActivity.findViewById(R.id.down_button);
        leftButton = mainActivity.findViewById(R.id.left_button);
        rightButton = mainActivity.findViewById(R.id.right_button);
        dPadBackground = mainActivity.findViewById(R.id.d_pad_background);
        dPadImageView = mainActivity.findViewById(R.id.d_pad_imageview);

        //the image of the dpad is set
        int idOfDpad = mainActivity.getResources().getIdentifier("dpad_base", "drawable", mainActivity.getPackageName());
        dPadImageView.setImageResource(idOfDpad);
    }
    public void hideDPad()
    {
        upButton.setVisibility(View.GONE);
        downButton.setVisibility(View.GONE);
        leftButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        dPadImageView.setVisibility(View.GONE);
        dPadBackground.setVisibility(View.GONE);
    }
    public void showDPad()
    {
        upButton.setVisibility(View.VISIBLE);
        downButton.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        dPadImageView.setVisibility(View.VISIBLE);
        dPadBackground.setVisibility(View.VISIBLE);
    }

    public void moveUp ()
    //The move up button is pressed
    {
        if(checkUp() == 'o') {         //If tile above is empty
            player.moveUp();

            dPadImageView.setImageResource(R.drawable.dpad_press_up);
            pressingUp = (AnimationDrawable) dPadImageView.getDrawable();
            pressingUp.start();

            moveY = worldView.getY() + moveDist;
            ObjectAnimator animation = ObjectAnimator.ofFloat(worldView, "y", worldView.getY(), moveY);
            animation.setDuration(animationLength);
            animation.start();
        }
        else if (checkUp() == '1')          //If tile above has an NPC
        {
            conversationPressingUp = (AnimationDrawable) dPadImageView.getDrawable();
            conversationPressingUp.start();

            Character character = mainActivity.getCharacterAt(player.getXGrid(), player.getYGrid() - 1);
            mainActivity.characterTalkingToYou = character;
            hideDPad();
            character.startConversation();
        }
        else if(checkUp() == '2')           //If the tile above is a portal
        {
                Portal p = mainActivity.getPortalAt(player.getXGrid(), player.getYGrid() - 1);
                p.teleport(player);

        }
    }

    public void moveDown (){
    //The move down button is pressed
        if(checkDown() == 'o')      //If tile below is empty
        {
            player.moveDown();

            dPadImageView.setImageResource(R.drawable.dpad_press_down);
            pressingDown = (AnimationDrawable) dPadImageView.getDrawable();
            pressingDown.start();

            moveY = worldView.getY() - moveDist;
            ObjectAnimator animation = ObjectAnimator.ofFloat(worldView, "y", worldView.getY(), moveY);
            animation.setDuration(animationLength);
            animation.start();

        }
        else if(checkDown() == '2')     //If the tile above is a portal
        {
            if(mainActivity.isTalkedToNiels())      //And the player has finished the first conversation with Niels
            {
                Portal p = mainActivity.getPortalAt(player.getXGrid(), player.getYGrid() + 1);
                p.teleport(player);
            }
        }
    }

    public void moveLeft (){
    //The move left button is pressed
        if(checkLeft() == 'o') {        //If tile on the left is empty
            player.moveLeft();

            dPadImageView.setImageResource(R.drawable.dpad_press_left);
            pressingLeft = (AnimationDrawable) dPadImageView.getDrawable();
            pressingLeft.start();

            moveX = worldView.getX() + moveDist;
            ObjectAnimator animation = ObjectAnimator.ofFloat(worldView, "X", worldView.getX(), moveX);
            animation.setDuration(animationLength);
            animation.start();
        }else if (checkLeft() == '1')           //If tile to the left has an NPC
        {
            conversationPressingLeft = (AnimationDrawable) dPadImageView.getDrawable();
            conversationPressingLeft.start();

            Character character = mainActivity.getCharacterAt(player.getXGrid() - 1, player.getYGrid());
            mainActivity.characterTalkingToYou = character;
            hideDPad();
            character.startConversation();
        }
    }

    public void moveRight ()
    //The move right button is pressed
    {
        if(checkRight() == 'o')     //If tile on the right is empty
        {
            player.moveRight();

            dPadImageView.setImageResource(R.drawable.dpad_press_right);
            pressingRight = (AnimationDrawable) dPadImageView.getDrawable();
            pressingRight.start();

            moveX = worldView.getX() - moveDist;
            ObjectAnimator animation = ObjectAnimator.ofFloat(worldView, "X", worldView.getX(), moveX);
            animation.setDuration(animationLength);
            animation.start();
        }else if (checkRight() == '1') //If tile on the has an NPC
        {
            conversationPressingRight = (AnimationDrawable) dPadImageView.getDrawable();
            conversationPressingRight.start();

            Character character = mainActivity.getCharacterAt(player.getXGrid() + 1, player.getYGrid());
            mainActivity.characterTalkingToYou = character;
            hideDPad();
            character.startConversation();
        }
    }

    //creates the 2D array mapTiles, which holds the structure of the map
    public void loadMapStructure(){
        String idName = "map_structure"; //creates a String name of the file to use in the following line
        int idOfFile = mainActivity.getResources().getIdentifier(idName,"raw", mainActivity.getPackageName());
        InputStream inputStream = mainActivity.getResources().openRawResource(idOfFile);
        FileRead fileStructure = new FileRead(inputStream); //creates the file object for all the Strings to be created there
        mapTiles = fileStructure.readStructureChars(); //reads all of the chars in the structure and adds them to the 2D array
    }

    private char checkLeft() { return mapTiles[player.getYGrid()][player.getXGrid() - 1]; }         //Check what is to the left of the player character
    private char checkUp() { return mapTiles[player.getYGrid() - 1][player.getXGrid()]; }           //Check what is up from the player character
    private char checkDown() { return mapTiles[player.getYGrid() + 1][player.getXGrid()]; }         //Check what is down from the player character
    private char checkRight() { return mapTiles[player.getYGrid()][player.getXGrid() + 1]; }        //Check what is to the right of the player character

    public void switchDpadToConversation ()
    //If there is an NPC next to the player character, set the appropriate image
    {
        if(checkUp() == '1'){
            dPadImageView.setImageResource(R.drawable.dpad_convo_press_up);
        }
        else if (checkRight() == '1'){
            dPadImageView.setImageResource(R.drawable.dpad_convo_press_right);
        }
        else if (checkLeft() == '1'){
            dPadImageView.setImageResource(R.drawable.dpad_convo_press_left);
        }
    }
    //----------------------------------------------------------------------------------------------
    //Getters
    public int getAnimationLength()
    {
        return animationLength;
    }
}


