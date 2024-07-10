package pieces.pieces;

import com.example.myapplication.ChessController;

/**
 * The Bishop class is an extension of the Piece class which behaves like a bishop
 * in a game of chess.
 */
public class Bishop extends Piece {

    /**
     * This constructor is used to initialize the Bishop object.
     * @param xpos This is the row associated with the location of the Bishop object.
     * @param ypos This is the column associated with the location of the Bishop object.
     * @param type This is the identifier associated with the Bishop Object to display on the board.
     * @param color This is the color associated with the instance of the Bishop object.
     */
    public Bishop(int xpos, int ypos, String type, String color) {
        super(xpos, ypos, type, color);
    }

    /**
     * This method is used to check if a given move is valid or not.
     * @param x This is the new row given by the move.
     * @param y This is the new column given by the move.
     * @param controller This is the controller that holds the board as well references to the white and black pieces.
     * @return boolean This returns true if the move is valid and false if it is invalid.
     */
    @Override
    public boolean isValidMove(int x, int y, ChessController controller) {
        int xDiff = xpos-x;
        int yDiff = ypos-y;
        if(!(Math.abs(xDiff) == Math.abs(yDiff))) {
            return false;
        }
        if(controller.board[x][y] == null || !controller.board[x][y].getColor().equals(getColor())) {
            boolean upRight = (x < xpos) && (y > ypos);
            boolean downRight = (x > xpos) && (y > ypos);
            boolean upLeft = (x < xpos) && (y < ypos);
            boolean downLeft = (x > xpos) && (y < ypos);
            if(upRight)
            {
                int xStart = xpos - 1;
                int yStart = ypos + 1;
                for(int i = xStart; i > x; i--)
                {
                    if(controller.board[i][yStart] != null)
                        return false;
                    yStart++;
                }
                return true;
            }
            if(downRight)
            {
                int xStart = xpos + 1;
                int yStart = ypos + 1;
                for(int i = xStart; i < x; i++)
                {
                    if(controller.board[i][yStart] != null)
                        return false;
                    yStart++;
                }
                return true;
            }
            if(upLeft)
            {
                int xStart = xpos - 1;
                int yStart = ypos - 1;
                for(int i = xStart; i > x; i--)
                {
                    if(controller.board[i][yStart] != null)
                        return false;
                    yStart--;
                }
                return true;
            }
            if(downLeft)
            {
                int xStart = xpos + 1;
                int yStart = ypos - 1;
                for(int i = xStart; i < x; i++)
                {
                    if(controller.board[i][yStart] != null)
                        return false;
                    yStart--;
                }
                return true;
            }
        }
        return false;
    }
}
