package com.example.test4;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    //public ImageView img;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /*public static final String TAG = MainActivity.class.getSimpleName();

          public static final String mPath = "data.xml";
          private FileRead mQuoteBank;
          private List<String> mLines;*/
        /*mQuoteBank = new FileRead(this);
        mLines = mQuoteBank.readLine(mPath);
        for (String string : mLines)
            Log.d(TAG, string);*/



        /*BufferedReader reader;

        try{
            reader = new BufferedReader(new FileReader("src/main/res/data.txt"));
            String line = reader.readLine();
            while(line != null){
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e){
            e.printStackTrace();
        }*/

        /*img = findViewById(R.id.hint_img);
        img.setImageResource(R.mipmap.tower);

        Button btn = findViewById(R.id.button);

        

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMethod(v);
            }
        });*/
    }
    /*public void testMethod(View v)
    {
        if(img.getVisibility() == View.GONE)
            img.setVisibility(View.VISIBLE);
        else if(img.getVisibility() == View.VISIBLE)
            img.setVisibility(View.GONE);
    }*/
}
