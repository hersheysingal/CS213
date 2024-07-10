package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The Queen class is an extension of the Piece class which behaves like a queen
 * in a game of chess.
 */
public class Queen extends Piece {

    /**
     * This is utilized to mimic the rook qualities of a queen, moving horizontally and vertically
     */
    Rook linear;

    /**
     * This is utilized to mimic the bishop qualities of a queen, moving diagonally
     */
    Bishop diagonal;

    /**
     * This constructor is used to initialize the Queen object
     * @param xpos This is the row associated with the location of the Queen object
     * @param ypos This is the column associated with the location of the Queen object
     * @param type This is the identifier associated with the Queen Object to display on the board
     * @param color This is the color associated with the instance of the Queen object
     */
    public Queen(int xpos, int ypos, String type, String color) {
        super(xpos, ypos, type, color);
        linear = new Rook(xpos, ypos, type, color);
        diagonal = new Bishop(xpos, ypos, type, color);
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
        this.diagonal.xpos = xpos;
        this.diagonal.ypos = ypos;
        this.linear.xpos = xpos;
        this.linear.ypos = ypos;
        if(this.diagonal.isValidMove(x, y, controller) || this.linear.isValidMove(x, y, controller))
        {
            return true;
        }
        return false;
    }
}
