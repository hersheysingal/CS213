package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class PlayBackView extends View {
    Bitmap blackBishopBitmap;
    Bitmap blackHorseBitmap;
    Bitmap blackKingBitmap;
    Bitmap blackPawnBitmap;
    Bitmap blackQueenBitmap;
    Bitmap blackRookBitmap;
    Bitmap whiteBishopBitmap;
    Bitmap whiteHorseBitmap;
    Bitmap whiteKingBitmap;
    Bitmap whitePawnBitmap;
    Bitmap whiteQueenBitmap;
    Bitmap whiteRookBitmap;
    ChessData data;
    int currentPosition = 0;

    public PlayBackView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        setBackgroundResource(R.drawable.chessboardimage);
        blackBishopBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackbishop);
        blackHorseBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackhorse);
        blackKingBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackking);
        blackPawnBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackpawn);
        blackQueenBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackqueen);
        blackRookBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blackrook);
        whiteBishopBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whitebishop);
        whiteHorseBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whitehorse);
        whiteKingBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whiteking);
        whitePawnBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whitepawn);
        whiteQueenBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whitequeen);
        whiteRookBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.whiterook);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();
        int boxSize = getWidth()/8;
        if(data == null)
            return;
        String[][]board = data.getBoard(currentPosition);
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[j][i].equals(""))
                    continue;
                Rect rect = new Rect(i*boxSize,j*boxSize,i*boxSize + boxSize, j*boxSize + boxSize);
                canvas.drawBitmap(getCorrectBitmap(board[j][i]),null,rect,paint);
            }
        }
    }

    public void setChessData(ChessData data){
        this.data = data;
    }
    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

    }

    public void setOnClickListeners(Button nextButton, Button previousButton){
        nextButton.setOnClickListener(view -> {
            currentPosition++;
            if(data == null)
                return;
            if(currentPosition>= data.boardList.size())
                currentPosition = data.boardList.size()-1;
            invalidate();
        });
        previousButton.setOnClickListener(view -> {
            currentPosition--;
            if(currentPosition<0)
                currentPosition = 0;
            invalidate();
        });
    }

    public Bitmap getCorrectBitmap(String str){
        Bitmap ret = null;
        switch(str)
        {
            case "bR":
                ret = blackRookBitmap;
                break;
            case "bN":
                ret = blackHorseBitmap;
                break;
            case "bB":
                ret = blackBishopBitmap;
                break;
            case "bQ":
                ret = blackQueenBitmap;
                break;
            case "bK":
                ret = blackKingBitmap;
                break;
            case "bp":
                ret = blackPawnBitmap;
                break;
            case "wR":
                ret = whiteRookBitmap;
                break;
            case "wN":
                ret = whiteHorseBitmap;
                break;
            case "wB":
                ret = whiteBishopBitmap;
                break;
            case "wQ":
                ret = whiteQueenBitmap;
                break;
            case "wK":
                ret = whiteKingBitmap;
                break;
            case "wp":
                ret = whitePawnBitmap;
                break;
        }
        return ret;
    }
}
