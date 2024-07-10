package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.R;

import java.util.ArrayList;

import pieces.pieces.Piece;

public class ChessBoardView extends View {
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
    Piece selectedPiece;
    ChessController controller;

    public ChessBoardView(Context context, AttributeSet attributeSet){
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
        paint.setColor(Color.GREEN);
        int boxSize = getWidth()/8;
        if(controller == null)
            return;
        ArrayList<int []> validMoves = new ArrayList<>();
        if(controller.selectedPiece != null){
            validMoves = controller.getValidMoves();
        }
        for(int [] move : validMoves){
            Rect rect = new Rect(move[1]*boxSize,move[0]*boxSize,move[1]*boxSize + boxSize, move[0]*boxSize + boxSize);
            Paint temp = new Paint();
            temp.setColor(Color.rgb(102, 155, 153));
            temp.setAlpha(200);

            canvas.drawRect(rect, temp);
        }
        for(int i = 0; i < controller.board.length; i++){
            for(int j = 0; j < controller.board.length; j++){
                if(controller.board[j][i] == null)
                    continue;
                Rect rect = new Rect(i*boxSize,j*boxSize,i*boxSize + boxSize, j*boxSize + boxSize);
                if(controller.board[j][i] == controller.selectedPiece){
                    Paint temp = new Paint();
                    temp.setColor(Color.MAGENTA);
                    canvas.drawRect(rect,temp);
                }
                canvas.drawBitmap(getCorrectBitmap(controller.board[j][i].getType()),null,rect,paint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    public void giveChessController(ChessController controller){
        this.controller = controller;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Toast.makeText(this.getContext(), "Touched View", Toast.LENGTH_SHORT).show();
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int boxNumberX = (int)event.getX() / (getWidth()/8);
            int boxNumberY = (int)event.getY() / (getHeight()/8);
            controller.selected(boxNumberY,boxNumberX);
            invalidate();
        }
        int[] location = new int[2];
        getLocationOnScreen(location);
        return true;
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
