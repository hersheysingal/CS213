package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import pieces.pieces.Bishop;
import pieces.pieces.Horse;
import pieces.pieces.King;
import pieces.pieces.Pawn;
import pieces.pieces.Piece;
import pieces.pieces.Queen;
import pieces.pieces.Rook;

/**
 * This chess controller creates the chessboard, takes user input, and displays the game
 * board.
 */
public class ChessController {
    public Piece selectedPiece;
    public String turn;

    /**
     * This represents the chessboard as a 2-D array of the object piece.
     */
    public Piece [][] board = new Piece[8][8];

    /**
     * Represents the set of black pieces when available.
     */
    public Set<Piece>  Blacks = new HashSet<Piece>();

    /**
     * Represents the set of white pieces when available.
     */
    public Set<Piece> Whites = new HashSet<Piece>();

    /**
     * This method is the driver for this program, initializes the chessboard and allows for
     * the users to play the game.
     * @param args This is the standard input for the main method.
     */
    ChessBoardView chessBoardView;
    TextView textViewOne;
    TextView textViewTwo;
    Button aiButton;
    Button resignButton;
    Button drawButton;
    Button declineButton;
    Button acceptButton;
    Button undoButton;
    ChessData data = new ChessData(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    AlertDialog gameOverDialog;

    public  int drawCounter = 0;
    public  Piece lastMoved;
    public  boolean isUndo = false;
    public ChessController(final ChessBoardView chessBoardView, TextView textViewOne, TextView textViewTwo, final Button aiButton, final Button resignButton, final Button drawButton, final Button declineButton, final Button acceptButton, final Button undoButton){
        turn = "White";
        initializeBoard();
        this.chessBoardView = chessBoardView;
        this.textViewOne = textViewOne;
        this.textViewTwo = textViewTwo;
        this.aiButton = aiButton;
        this.resignButton = resignButton;
        this.drawButton = drawButton;
        this.declineButton = declineButton;
        this.acceptButton = acceptButton;
        this.undoButton = undoButton;
        data.addBoard(getStringBoard());
        data.addTextOne((String)textViewOne.getText());
        data.addTextTwo((String) textViewTwo.getText());
        declineButton.setVisibility(View.INVISIBLE);
        acceptButton.setVisibility(View.INVISIBLE);
        aiButton.setOnClickListener(v -> AIMove());
        resignButton.setOnClickListener(v -> {
            if(turn.equals("White"))
                System.out.println("White Resigned");
            else
                System.out.println("Black Resigned");
        });
        drawButton.setOnClickListener(v -> {
            drawCounter++;
            changeTurn();
            aiButton.setVisibility(View.INVISIBLE);
            drawButton.setVisibility(View.INVISIBLE);
            undoButton.setVisibility(View.INVISIBLE);
            resignButton.setVisibility(View.INVISIBLE);
            declineButton.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.VISIBLE);
        });
        declineButton.setOnClickListener(v -> {
            drawCounter = 0;
            changeTurn();
            drawButton.setVisibility(View.VISIBLE);
            undoButton.setVisibility(View.VISIBLE);
            resignButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.INVISIBLE);
            acceptButton.setVisibility(View.INVISIBLE);
            System.out.println("Draw was declined");
        });
        acceptButton.setOnClickListener(v -> {
            drawCounter = 0;
            drawButton.setVisibility(View.VISIBLE);
            undoButton.setVisibility(View.VISIBLE);
            resignButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.INVISIBLE);
            acceptButton.setVisibility(View.INVISIBLE);
            System.out.println("Game is a Draw");
        });
        undoButton.setOnClickListener(v -> {
            if(isUndo){
                lastMoved.revert(ChessController.this);
                chessBoardView.invalidate();
                changeTurn();
                isUndo = false;
            }
        });
        //readMoves();
    }

    /**
     * This method checks to see if a spot on the baord is in danger of being moved to by the user's piece.
     * @param x This is the row associated with the selected position.
     * @param y This is the column associated with the selected position.
     * @param color This is the color associated with the piece.
     * @return boolean This returns true if the position on the board is in danger and false if not.
     */
    public  boolean inDanger(int x, int y, String color) {
        if(x > 7 || x < 0 || y < 0 || y > 7)
            return true;
        Set<Piece> enemies = color.equals("Black") ? Whites : Blacks;
        for(Piece enemy : enemies) {
            if(enemy.isValidMove(x, y, this))
                return true;
        }
        return false;
    }

    public void selected(int i, int j){
        if(selectedPiece == board[i][j]) {
            System.out.println("1");
            selectedPiece = null;
        }
        else if(board[i][j]!=null && board[i][j].getColor().equals(turn)) {
            System.out.println("2");
            selectedPiece = board[i][j];
        }
        else {
            System.out.println("3");
            if(selectedPiece!=null && selectedPiece.moveTo(i, j, this)){
                changeTurn();
                lastMoved = selectedPiece;
                isUndo = true;
                setInvulnerable(turn);
                selectedPiece = null;
                data.addBoard(getStringBoard());
                data.addTextOne((String) textViewOne.getText());
                data.addTextTwo((String) textViewTwo.getText());
                if(Mate(turn) && inCheck(turn)){
                    gameOver(turn.equals("Black") ? "White" : "Black" );
                }
            }
        }
    }
    public void gameOver(String winningTeam){
        gameOverDialog.setMessage(winningTeam + " " + "Won!\n" + "You can view the playback using the button on the home screen!");
        gameOverDialog.show();
    }
    public void setGameOverDialog(AlertDialog dialog){
        this.gameOverDialog = dialog;
    }
    public String[][] getStringBoard(){
        String[][] returnBoard = new String[8][8];
        for(int i =0 ; i <board.length;i++){
            for(int j = 0; j < board.length; j++){
                returnBoard[i][j] = board[i][j] == null ? "" : board[i][j].getType();
            }
        }
        return returnBoard;
    }

    public ArrayList<int []> getValidMoves(){
        ArrayList<int []> validMoves = new ArrayList<>();
        for(int a = 0; a < board.length; a++){
            for(int b = 0; b < board[a].length; b++){
                if(selectedPiece.isValidMove(a, b, this))
                    validMoves.add(new int[]{a,b});
            }
        }
        return validMoves;
    }

    public ArrayList<int []> getValidMoves(Piece piece){
        ArrayList<int []> validMoves = new ArrayList<int []>();
        for(int a = 0; a < board.length; a++){
            for(int b = 0; b < board[a].length; b++){
                if(piece.isValidMove(a, b, this))
                    validMoves.add(new int[]{a,b});
            }
        }
        return validMoves;
    }

    public void changeTurn(){
        turn = turn.equals("White") ? "Black" : "White";
        if(turn.equals("White")){
            textViewOne.setText("White's Turn");
            textViewOne.setTypeface(null, Typeface.BOLD);
            textViewTwo.setTypeface(null, Typeface.NORMAL);
            textViewTwo.setText("Black");
        }
        if(turn.equals("Black")){
            textViewOne.setText("White");
            textViewOne.setTypeface(null, Typeface.NORMAL);
            textViewTwo.setTypeface(null, Typeface.BOLD);
            textViewTwo.setText("Black's Turn");
        }
    }

    /**
     * This method returns the king of a given color.
     * @param color This is the color of the piece.
     * @return Piece This returns the king object.
     */
    public Piece getKing(String color) {
        for(Piece piece : color.equals("White") ? Whites : Blacks) {
            if(piece instanceof King)
                return piece;
        }
        return null;
    }

    /**
     * This method checks if a given color is in check or not.
     * @param color This is the color.
     * @return boolean This returns true if the given color is in check and false if not.
     */
    public boolean inCheck(String color) {
        Piece king = null;
        for(Piece piece : color.equals("Black") ? Blacks : Whites) {
            if(piece.getType().equals("bK") || piece.getType().equals("wK"))
                king = piece;
        }
        return inDanger(king.getX(),king.getY(),king.getColor());
    }

    /**
     * This method checks if a given king is in check or not.
     * @param king This is the king object.
     * @return boolean This returns true if the given king is in check and false if not.
     */
    public boolean inCheck(Piece king) {
        return inDanger(king.getX(),king.getY(),king.getColor());
    }

    /**
     * This method checks if a given color is in checkmate.
     * @param color This is the color.
     * @return boolean This returns true if the given color is in checkmate or stalemate and false if not.
     */
    public boolean Mate(String color) {
        Piece king = getKing(color);
        if(checkSurroundings(king))
            return false;
        if(checkAllMoves(color,king))
            return false;
        return true;
    }

    /**
     * This method checks to see if there is a move such that the king is not in check anymore.
     * @param color This is the color.
     * @param king This is the king object.
     * @return boolean This returns true if there is such a move that the king is not in check anymore and false if not.
     */
    public boolean checkAllMoves(String color, Piece king) {
        Set<Piece> team = color.equals("Black") ? Blacks : Whites;
        Set<Piece> oldBlacks;
        Set<Piece> oldWhites;
        Piece [][] oldBoard = new Piece[8][8];
        for(Piece piece: team) {
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8;j++) {
                    if(piece.moveTo(i, j, this)) {
                        if(!inCheck(king)) {
                            piece.revert(this);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method checks to see if the king itself can move out of a check.
     * @param king This is the king object.
     * @return boolean This returns true if there is a move the king can do to move itself out of check and false if not.
     */
    public boolean checkSurroundings(Piece king) {
        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2;j++) {
                if(!inDanger(king.getX()+i,king.getY()+j,king.getColor()) && board[king.getX()+i][king.getY()+j] == null && !(i == 0 && j == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method initializes the board with all of the pieces in their starting positions.
     */
    public void initializeBoard() {
        board = new Piece[8][8];
        board[0][0] = new Rook(0,0,"bR", "Black");
        Blacks.add(board[0][0]);
        board[0][1] = new Horse(0,1,"bN","Black");
        Blacks.add(board[0][1]);
        board[0][2] = new Bishop(0,2,"bB","Black");
        Blacks.add(board[0][2]);
        board[0][3] = new Queen(0,3,"bQ","Black");
        Blacks.add(board[0][3]);
        board[0][4] = new King(0,4,"bK","Black");
        Blacks.add(board[0][4]);
        board[0][5] = new Bishop(0,5,"bB","Black");
        Blacks.add(board[0][5]);
        board[0][6] = new Horse(0,6,"bN","Black");
        Blacks.add(board[0][6]);
        board[0][7] = new Rook(0,7,"bR","Black");
        Blacks.add(board[0][7]);
        for(int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(1,i,"bp","Black");
            Blacks.add(board[1][i]);
        }
        for(int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(6,i,"wp","White");
            Whites.add(board[6][i]);
        }
        board[7][0] = new Rook(7,0,"wR","White");
        Whites.add(board[7][0]);
        board[7][1] = new Horse(7,1,"wN","White");
        Whites.add(board[7][1]);
        board[7][2] = new Bishop(7,2,"wB","White");
        Whites.add(board[7][2]);
        board[7][3] = new Queen(7,3,"wQ","White");
        Whites.add(board[7][3]);
        board[7][4] = new King(7,4,"wK","White");
        Whites.add(board[7][4]);
        board[7][5] = new Bishop(7,5,"wB","White");
        Whites.add(board[7][5]);
        board[7][6] = new Horse(7,6,"wN","White");
        Whites.add(board[7][6]);
        board[7][7] = new Rook(7,7,"wR","White");
        Whites.add(board[7][7]);
    }

    /**
     * This method prints out the current board.
     * @param board This is the 2-D array of piece objects which makes up the board.
     */
    public void printBoard(Piece [][] board) {
        for(int i = 0; i < board.length;i++){
            Piece[] arr = board[i];
            for(int j = 0; j < board[i].length;j++) {
                Piece piece = board[i][j];
                if(piece!=null)
                    System.out.print(piece.getType());
                else {
                    if((i + j) % 2 ==0 )
                        System.out.print("  ");
                    else
                        System.out.print("##");
                }
                System.out.print(" ");
            }
            System.out.print(8-i);
            System.out.println("");
        }
        System.out.println(" a  b  c  d  e  f  g  h");
        System.out.println();
    }

    /**
     * This method sets all the pawn vulnerabilities of a specific color to false.
     * @param color This is the color.
     */
    public void setInvulnerable(String color) {
        for(Piece piece: color.equals("Black") ? Blacks : Whites) {
            if(piece instanceof Pawn) {
                Pawn pawn = (Pawn) piece;
                pawn.setVulnerabilityToFalse();
            }
        }
    }

    /**
     * This method checks to see if the input is a valid promotion and assigns the new piece.
     * @param x This is the row associated with the given location.
     * @param y This is the column associated with the given location.
     * @param change This is the piece the user wants to change to given as a string input.
     */
    public void checkForPromotion(int x, int y,String change) {
        if(x>0 && x < 7)
            return;
        Piece piece = board[x][y];
        if(piece == null)
            return;
        if(!(piece instanceof Pawn))
            return;
        Set<Piece> team = piece.getColor().equals("White")? Whites : Blacks;
        String prefix = piece.getColor().equals("White") ? "w" : "b";
        String color = piece.getColor();
        team.remove(piece);
        if(change.equals(" N"))
            piece = new Horse(x,y, prefix + "N", color);
        else if(change.equals(" R"))
            piece = new Rook(x,y, prefix + "R", color);
        else if(change.equals(" B"))
            piece = new Bishop(x,y, prefix + "B", color);
        else
            piece = new Queen(x,y, prefix + "Q", color);
        board[x][y] = piece;
        team.add(piece);

    }

    /**
     * This method initializes the game, takes in user input, and calls the designated methods.
     */
    public void readMoves() {
        Scanner sc = new Scanner(System.in);
        boolean endGame = false;
        String turn = "White";
        boolean askingForDraw = false;
        //stalemateBoard();
        printBoard(board);
        while(!endGame) {
            System.out.print(turn+"'s move: ");
            String input = sc.nextLine();
            System.out.println("");
            if(input.equals("resign")) {
                endGame = true;
                System.out.println((turn.equals("White") ? "Black": "White") + " wins");
                System.out.println("");
                continue;
            }
            if(askingForDraw) {
                if(input.equals("draw")) {
                    endGame = true;
                    System.out.println("draw");
                    continue;
                }
                else
                    askingForDraw = false;
            }
            String move1 = input.substring(0,2);
            String move2 = input.substring(3,5);
            String remaining = input.substring(5,input.length());
            if(remaining.length() >=5 && remaining.substring(remaining.length()-5,remaining.length()).equals("draw?"))
                askingForDraw = true;
            int oldY = move1.charAt(0) - 'a';
            int newY = move2.charAt(0) - 'a';
            int oldX = '8' - move1.charAt(1);
            int newX = '8' - move2.charAt(1);
            if(board[oldX][oldY] == null) {
                System.out.println("Illegal move, try again");
                System.out.println("");
                continue;
            }
            if(!board[oldX][oldY].getColor().equals(turn)) {
                System.out.println("Illegal move, try again");
                System.out.println("");
                continue;
            }
            if(!board[oldX][oldY].moveTo(newX,newY,this)) {
                System.out.println("Illegal move, try again");
                System.out.println("");
                continue;
            }
            checkForPromotion(newX,newY,remaining);
            turn = turn.equals("White") ? "Black" : "White";
            setInvulnerable(turn);
            boolean check = inCheck(turn);
            if(Mate(turn)) {
                if(check) {
                    endGame = true;
                    System.out.println("Checkmate");
                    System.out.println("");
                    System.out.println((turn.equals("White") ? "Black": "White") + " wins");
                    continue;
                }
                else {
                    endGame = true;
                    System.out.println("Stalemate");
                    System.out.println("");
                    System.out.println("Draw");
                    continue;
                }
            }
            if(check) {
                System.out.println("Check");
                System.out.println("");
            }
            printBoard(board);
        }
    }

    public void AIMove(){
        ArrayList<Piece> availablePieces = new ArrayList<Piece>();
        availablePieces.addAll(turn.equals("Black") ? Blacks : Whites);
        ArrayList<ArrayList<int[]>> allPossibleMoves = new ArrayList<ArrayList<int[]>>();
        ArrayList<Piece> movablePieces = new ArrayList<Piece>();
        for(Piece piece: availablePieces){
//			selectedPiece = piece;
            ArrayList<int[]> temp = getValidMoves(piece);
            if(temp.size() > 0) {
                allPossibleMoves.add(temp);
                movablePieces.add(piece);
            }
        }
        while(movablePieces.size() > 0){
            int outer = ThreadLocalRandom.current().nextInt(0, allPossibleMoves.size());
            int inner = ThreadLocalRandom.current().nextInt(0, allPossibleMoves.get(outer).size());
            System.out.println("Moving piece at X: " + movablePieces.get(outer).getX() + " Y: " + movablePieces.get(outer).getY() + "to X: " + allPossibleMoves.get(outer).get(inner)[0] + " Y: " + allPossibleMoves.get(outer).get(inner)[1]);
            if(movablePieces.get(outer).moveTo(allPossibleMoves.get(outer).get(inner)[0], allPossibleMoves.get(outer).get(inner)[1],this)){
                chessBoardView.invalidate();
                changeTurn();
                lastMoved = movablePieces.get(outer);
                isUndo = true;
                setInvulnerable(turn);
                selectedPiece = null;
                data.addBoard(getStringBoard());
                data.addTextOne((String) textViewOne.getText());
                data.addTextTwo((String) textViewTwo.getText());
                if(Mate(turn) && inCheck(turn)){
                    gameOver(turn.equals("Black") ? "White" : "Black" );
                }
                return;
            }
            allPossibleMoves.get(outer).remove(inner);
            if(allPossibleMoves.get(outer).size() == 0){
                allPossibleMoves.remove(outer);
                movablePieces.remove(outer);
            }
        }
        System.out.println("NO POSSIBLE MOVES");
        System.out.println((turn.equals("Black") ? "White" : "Black") + " won!");
    }
}
