package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.example.myapplication.MenuActivity;
import com.example.myapplication.R;

public class PlayBackActivity extends Activity {

    public static final String storeDir = "data";

    /**
     * This is the file that stores the data
     */
    public static final String storeFile = "userData.dat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_activity);
        PlayBackView playBackView = findViewById(R.id.playBackView);
        Button nextButton = findViewById(R.id.nextButton);
        Button previousButton = findViewById(R.id.previousButton);
        playBackView.setOnClickListeners(nextButton,previousButton);
        MenuActivity.verifyStoragePermissions(this);
        try {
            ChessData data = readApp();
            playBackView.setChessData(data);
            playBackView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChessData readApp() throws ClassNotFoundException, IOException {
        ObjectInputStream ois;
        ChessData userData = null;
        File file = getFilesDir();
        file = new File(file.getAbsolutePath() + File.separator + storeFile);
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            userData = (ChessData) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException("File not found: " + storeFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error reading file: " + storeFile);
        }
        return userData;
    }
}
