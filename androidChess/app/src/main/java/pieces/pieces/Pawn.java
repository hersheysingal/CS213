package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The Pawn class is an extension of the Piece class which behaves like a pawn
 * in a game of chess.
 */
public class Pawn extends Piece {

    /**
     * This is to check if the piece is vulnerable to an enpassant move
     */
    boolean enpassantVulnerable = false;

    /**
     * This is to check the direction of which the pawn is to move
     */
    public int direction = 0;

    /**
     * This constructor is used to initialize the Pawn object
     * @param xpos This is the row associated with the location of the Pawn object
     * @param ypos This is the column associated with the location of the Pawn object
     * @param type This is the identifier associated with the Pawn Object to display on the board
     * @param color This is the color associated with the instance of the Pawn object
     */
    public Pawn(int xpos, int ypos, String type, String color) {
        super(xpos, ypos, type,color);
        if(color.equals("Black"))
            direction = -1;
        else
            direction = 1;
        // TODO Auto-generated constructor stub
    }

    /**
     * This method is used to check if a given move is valid or not
     * @param x This is the new row given by the move
     * @param y This is the new column given by the move
     * @param controller This is the controller that holds the board as well references to the white and black pieces
     * @return boolean This returns true if the move is valid and false if it is invalid
     */
    @Override
    public boolean isValidMove(int x, int y, ChessController controller) {
        if(xpos == x && ypos == y)
            return false;
        if(ypos != y) {
            if((xpos - x)!= direction*1 || Math.abs(ypos - y)!=1)
                return false;
            if(enpassant(x,y,controller.board))
                return true;
            if(controller.board[x][y] == null || controller.board[x][y].getColor().equals(getColor()))
                return false;
            moved = true;
            return true;
        }
        if(xpos-x == direction * 2 && !moved && controller.board[xpos-direction*1][y] == null && controller.board[x][y] == null) {
            return true;
        }
        if(xpos - x == direction * 1 && controller.board[x][y] == null) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if a given move considered as an enpassant is valid or not
     * @param x This is the new row given by the move
     * @param y This is the new column given by the move
     * @param board This is the board represented as a 2D array of pieces
     * @return boolean This returns true if the move is valid and false if it is invalid
     */
    public boolean enpassant(int x, int y, Piece[][] board) {
        Piece correspondingPiece = board[x+direction][y];
        if(correspondingPiece == null || !(correspondingPiece instanceof Pawn))
            return false;
        Pawn correspondingPawn = (Pawn) correspondingPiece;
        if(!correspondingPawn.getVulnerability())
            return false;
        return true;
    }

    /**
     * This method gets the enpassantVulnerable value of the Pawn
     * @return boolean This returns the value of enpassantVulnerable of the Pawn
     */
    public boolean getVulnerability() {
        return enpassantVulnerable;
    }

    /**
     * This method sets the enpassantVulnerable value of the Pawn to false
     */
    public void setVulnerabilityToFalse() {
        enpassantVulnerable = false;
    }
    public void setVulnerabilityToTrue() {
        enpassantVulnerable = true;
    }

    /**
     * This method reverts the current move to the last move
     */
    @Override
    public void revert(ChessController controller) {
        super.revert(controller);
        enpassantVulnerable = false;
    }
}

