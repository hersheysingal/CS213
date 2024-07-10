package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The Rook class is an extension of the Piece class which behaves like a rook
 * in a game of chess.
 */
public class Rook extends Piece {

    /**
     * This constructor is used to initialize the Queen object
     * @param xpos This is the row associated with the location of the Rook object
     * @param ypos This is the column associated with the location of the Rook object
     * @param type This is the identifier associated with the Rook Object to display on the board
     * @param color This is the color associated with the instance of the Rook object
     */
    public Rook(int xpos, int ypos, String type, String color) {
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
        if(!((Math.abs(xDiff) > 0 && Math.abs(yDiff) == 0) || (Math.abs(xDiff) == 0 && Math.abs(yDiff) > 0))) {
            return false;
        }
        if(controller.board[x][y] == null || !controller.board[x][y].getColor().equals(getColor())) {
            if(xDiff!=0) {
                int i = xpos;
                int direction = x > xpos ? 1 : -1;
                i+=direction;
                while(i != x) {
                    if(controller.board[i][y]!=null)
                        return false;
                    i+=direction;
                }
                if(moved)
                    lastMoved = true;
                moved = true;
                return true;
            }
            if(yDiff!=0) {
                int i = ypos;
                int direction = y > ypos ? 1 : -1;
                i+=direction;
                while(i!=y) {
                    if(controller.board[x][i]!=null)
                        return false;
                    i+=direction;
                }
                if(moved)
                    lastMoved = true;
                moved = true;
                return true;
            }
        }
        return false;
    }

    /**
     * This method is used to check if the Rook has moved or not
     * @return boolean This returns the value of moved of the Pawn
     */
    public boolean hasMoved() {
        return moved;
    }
}

