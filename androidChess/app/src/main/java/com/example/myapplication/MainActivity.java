package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import static com.example.myapplication.MenuActivity.verifyStoragePermissions;

import com.example.myapplication.R;

public class MainActivity extends Activity {

    public static final String storeDir = "data";

    /**
     * This is the file that stores the data
     */
    public static final String storeFile = "userData.dat";
    ChessController chessController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChessBoardView chessBoardView = findViewById(R.id.chessBoardView);
        TextView textViewOne = findViewById(R.id.textViewOne);
        TextView textViewTwo = findViewById(R.id.textViewTwo);
        Button aiButton = findViewById(R.id.aiButton);
        Button resignButton = findViewById(R.id.resignButton);
        Button drawButton = findViewById(R.id.drawButton);
        Button declineButton = findViewById(R.id.declineButton);
        Button acceptButton = findViewById(R.id.acceptButton);
        Button undoButton = findViewById(R.id.undoButton);
        chessController = new ChessController(chessBoardView,textViewOne,textViewTwo, aiButton, resignButton, drawButton, declineButton, acceptButton, undoButton);
        chessBoardView.giveChessController(chessController);
        chessBoardView.invalidate();
        verifyStoragePermissions(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game over!");
        AlertDialog gameOverDialog = builder.create();
        chessController.setGameOverDialog(gameOverDialog);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        System.out.println("Going back");
        File file =getFilesDir();
        file = new File(file.getAbsolutePath()+File.separator + storeFile);
        if(chessController!=null){
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                oos.writeObject(chessController.data);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
