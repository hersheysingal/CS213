package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The King class is an extension of the Piece class which behaves like a king
 * in a game of chess.
 */
public class King extends Piece {

    /**
     * This is to check if the king has performed a castle
     */
    boolean justCastled = false;

    /**
     * This constructor is used to initialize the King object
     * @param xpos This is the row associated with the location of the King object
     * @param ypos This is the column associated with the location of the King object
     * @param type This is the identifier associated with the King Object to display on the board
     * @param color This is the color associated with the instance of the King object
     */
    public King(int xpos, int ypos, String type, String color) {
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
        if(Math.abs(yDiff) == 2 && Math.abs(xDiff) == 0) {
            return castling(x,y,controller);
        }
        if(Math.abs(xDiff)>1 || Math.abs(yDiff)>1) {
            return false;
        }
        if(xDiff == 0 && yDiff == 0)
            return false;
        if(controller.board[x][y] == null || !controller.board[x][y].getColor().equals(getColor())) {
            justCastled = false;
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if a given move that is considered as castling
     * is valid or not
     * @param x This is the new row given by the move
     * @param y This is the new column given by the move
     * @param controller This is the controller that holds the board as well references to the white and black pieces
     * @return boolean This returns true if the move is valid and false if it is invalid
     */
    public boolean castling(int x, int y, ChessController controller) {
        int yDiff = ypos - y;
        if(controller.inCheck(this))
            return false;
        if(moved)
            return false;
        Piece correspondingPiece = null;
        if(yDiff > 0)
            correspondingPiece = controller.board[x][0];
        else
            correspondingPiece = controller.board[x][7];
        if(correspondingPiece ==null || !(correspondingPiece instanceof Rook))
            return false;
        Rook rook = (Rook) correspondingPiece;
        if(rook.hasMoved()){
            return false;
        }
        int direction = yDiff > 0 ? -1 : 1;
        for(int i = getY()+direction; i != rook.getY();i+=direction) {
            if(controller.board[x][y]!=null)
                return false;
        }
        if(controller.inDanger(x, ypos+direction, color) || controller.inDanger(x, y, color))
            return false;
        return true;
    }

    /**
     * This method reverts the current move to the last move
     */
    @Override
    public void revert(ChessController controller) {
        super.revert(controller);
        if(justCastled) {
            if(controller.board[xpos][ypos+1]!=null) {
                Piece piece = controller.board[xpos][ypos+1];
                controller.board[xpos][7] = piece;
                controller.board[xpos][ypos+1] = null;
            }
            else {
                Piece piece = controller.board[xpos][ypos-1];
                controller.board[xpos][0] = piece;
                controller.board[xpos][ypos-1] = null;
            }
            justCastled = false;
        }
    }
}
