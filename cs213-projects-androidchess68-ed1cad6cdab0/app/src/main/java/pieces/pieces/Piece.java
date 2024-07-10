package pieces.pieces;

import com.example.myapplication.ChessController;


/**
 * The piece class is an abstract class which establishes the base qualities of every piece
 * in a game of chess.
 */
public abstract class Piece {

    /**
     * This method is an abstract method used to check if a given move is valid or not
     * @param x This is the new row given by the move
     * @param y This is the new column given by the move
     * @param controller This is the controller that holds the board as well references to the white and black pieces
     * @return boolean This returns true if the move is valid and false if it is invalid
     */
    public abstract boolean isValidMove(int x, int y, ChessController controller);

    /**
     * This is the row location of a given Piece object
     */
    protected int xpos;

    /**
     * This is the column location of a given Piece object
     */
    protected int ypos;

    /**
     * This is the identifier associated with the piece Object to display on the board
     */
    protected String type;

    /**
     * This is the color associated with the instance of the Piece object
     */
    protected String color;

    /**
     * This is the previous row location of a given Piece object
     */
    protected int lastX;

    /**
     * This is the previous column location of a given Piece object
     */
    protected int lastY;

    /**
     * This is the piece that was last killed
     */
    protected Piece lastKilledPiece;

    protected boolean moved;
    protected boolean lastMoved;

    /**
     * This constructor is used to initialize the Piece object
     * @param xpos This is the row associated with the location of the Piece object
     * @param ypos This is the column associated with the location of the Piece object
     * @param type This is the identifier associated with the Piece Object to display on the board
     * @param color This is the color associated with the instance of the Piece object
     */
    public Piece(int xpos, int ypos, String type, String color) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.type = type;
        this.color = color;
    }

    /**
     * This method retrieves the row location of a given Piece object
     * @return int This returns the value of xpos for the Piece object
     */
    public int getX() {
        return xpos;
    }

    /**
     * This method retrieves the column location of a given Piece object
     * @return int This returns the value of ypos for the Piece object
     */
    public int getY() {
        return ypos;
    }

    /**
     * This method retrieves the identifier associated with the Piece Object to display on the board
     * @return String This returns the value of type for the Piece object
     */
    public String getType() {
        return type;
    }

    /**
     * This method retrieves the color associated with the Piece Object
     * @return String This returns the value of color for the Piece object
     */
    public String getColor() {
        return color;
    }

    /**
     * This method is used to check if a Piece has moved or not
     * @param x This is the new row given by the move
     * @param y This is the new column given by the move
     * @param controller This is the controller that holds the board as well references to the white and black pieces
     * @return boolean This returns true if the move is valid and false if it is invalid
     */
    public boolean moveTo(int x, int y, ChessController controller) {
        if(!isValidMove(x,y,controller))
            return false;
        if (this instanceof Pawn){
            Pawn pawn = (Pawn)this;
            if(xpos-x == pawn.direction * 2 && !moved && controller.board[xpos-pawn.direction*1][y] == null && controller.board[x][y] == null) {
                pawn.setVulnerabilityToTrue();
            }
            if(pawn.enpassant(x,y,controller.board)){
                Piece correspondingPiece = controller.board[x+pawn.direction][y];
                Pawn correspondingPawn = (Pawn) correspondingPiece;
                controller.Whites.remove(correspondingPawn);
                controller.Whites.remove(correspondingPawn);
                lastKilledPiece = correspondingPawn;
                controller.board[x+pawn.direction][y] = null;
            }
        }
        if (this instanceof King){
            King king = (King) this;
            if(Math.abs(ypos-y) == 2) {
                king.justCastled = true;
                int yDiff = ypos - y;
                Piece correspondingPiece = null;
                if(yDiff > 0)
                    correspondingPiece = controller.board[x][0];
                else
                    correspondingPiece = controller.board[x][7];
                int direction = yDiff > 0 ? -1 : 1;
                controller.board[correspondingPiece.getX()][correspondingPiece.getY()] = null;
                controller.Blacks.remove(correspondingPiece);
                controller.Whites.remove(correspondingPiece);
                controller.board[x][ypos + direction] = new Rook(x,ypos+direction, correspondingPiece.getType(),correspondingPiece.getColor());
            }
            else
                king.justCastled = false;

        }
        controller.board[xpos][ypos] = null;
        lastX = xpos;
        lastY = ypos;
        if(controller.board[x][y]!=null) {
            lastKilledPiece = controller.board[x][y];
            controller.Whites.remove(controller.board[x][y]);
            controller.Blacks.remove(controller.board[x][y]);
        }
        xpos = x;
        ypos = y;
        controller.board[xpos][ypos] = this;
        if(controller.inCheck(color)) {
            revert(controller);
            return false;
        }
        if(moved)
            lastMoved = true;
        moved = true;
        return true;
    }

    /**
     * This method reverts the current move
     */
    public void revert(ChessController controller) {
        controller.board[xpos][ypos] = null;
        if(lastKilledPiece!=null) {
            controller.board[lastKilledPiece.getX()][lastKilledPiece.getY()] = lastKilledPiece;
            if(lastKilledPiece.getColor().equals("White")) {
                controller.Whites.add(lastKilledPiece);
            }
            else
                controller.Blacks.add(lastKilledPiece);
            lastKilledPiece = null;
        }
        moved = lastMoved;
        xpos = lastX;
        ypos = lastY;
        controller.board[lastX][lastY] = this;
    }
}
