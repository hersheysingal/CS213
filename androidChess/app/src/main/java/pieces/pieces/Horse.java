package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The Horse class is an extension of the Piece class which behaves like a horse
 * in a game of chess.
 */
public class Horse extends Piece {

    /**
     * This constructor is used to initialize the Horse object
     * @param xpos This is the row associated with the location of the Horse object
     * @param ypos This is the column associated with the location of the Horse object
     * @param type This is the identifier associated with the Horse Object to display on the board
     * @param color This is the color associated with the instance of the Horse object
     */
    public Horse(int xpos, int ypos, String type, String color) {
        super(xpos, ypos, type, color);
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
        int xDiff = xpos-x;
        int yDiff = ypos-y;
        if(!((Math.abs(xDiff) == 2 && Math.abs(yDiff) == 1) || (Math.abs(xDiff)==1 && Math.abs(yDiff) == 2))) {
            return false;
        }
        if(controller.board[x][y] == null || !controller.board[x][y].getColor().equals(getColor()))
            return true;
        return false;
    }
}
