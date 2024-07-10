package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        Button play = findViewById(R.id.playButton);
        play.setOnClickListener(view -> {
            Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
            startActivity(myIntent);
        });

        Button recorded = findViewById(R.id.recordedButton);
        recorded.setOnClickListener(view -> {
            Intent myIntent = new Intent(MenuActivity.this, PlayBackActivity.class);
            startActivity(myIntent);
        });

//        ChessBoardView chessBoardView = findViewById(R.id.chessBoardView);
//        TextView textViewOne = findViewById(R.id.textViewOne);
//        TextView textViewTwo = findViewById(R.id.textViewTwo);
//        ChessController chessController = new ChessController(chessBoardView,textViewOne,textViewTwo);
        verifyStoragePermissions(this);
    }


    /**
     * This method checks if the application has permission to write to the device's storage.
     * If not, the user will be prompted to grant permission.
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        System.out.println("PERMISSION: " + permission);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
