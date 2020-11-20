package edu.iastate.cs472.proj1;

import java.util.ArrayList;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 * @author Brendan Yeong
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        // Todo: setup the board with pieces BLACK, RED, and EMPTY
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if ( row % 2 == col % 2 ) {
                   //Top part is BLACK and bottom RED
                   if (row < 3)
                     board[row][col] = BLACK;
                   else if (row > 4)
                     board[row][col] = RED;
                   else
                     board[row][col] = EMPTY;
               }
               else {
                  board[row][col] = EMPTY;
               }
            }
         }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     * @return  true if the piece becomes a king, otherwise false
     */
    boolean makeMove(CheckersMove move) {
        return makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }

    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     * @return        true if the piece becomes a king, otherwise false
     */
    boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        // Todo: update the board for the given move.
        // You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        // 2. if this move is a jump, remove the captured piece
        if(fromRow - toRow == 2 || fromRow - toRow == -2){
          board[(fromRow + toRow)/2][(fromCol + toCol)/2] = EMPTY;
        }
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
        if (toRow == 0 && board[toRow][toCol] == RED){
            board[toRow][toCol] = RED_KING;
            return true;
          }
         if (toRow == 7 && board[toRow][toCol] == BLACK){
            board[toRow][toCol] = BLACK_KING;
            return true;
          }
          return false;
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        // Todo: Implement your getLegalMoves here.
        if (player != RED && player != BLACK)
            return null;

        int playerKing;
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = BLACK_KING;

        //The number of moves are not determined.
        ArrayList<CheckersMove> legalMoves = new ArrayList<>();

        //First rule: must jump whenever can. Search the whole board if there is a jump before normal move.
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player || board[row][col] == playerKing) {
                    if (isJump(player, row, col, row+1, col+1, row+2, col+2))
                        legalMoves.add(new CheckersMove(row, col, row+2, col+2));
                    if (isJump(player, row, col, row+1, col-1, row+2, col-2))
                        legalMoves.add(new CheckersMove(row, col, row+2, col-2));
                    if (isJump(player, row, col, row-1, col+1, row-2, col+2))
                        legalMoves.add(new CheckersMove(row, col, row-2, col+2));
                    if (isJump(player, row, col, row-1, col-1, row-2, col-2))
                        legalMoves.add(new CheckersMove(row, col, row-2, col-2));
                }
            }
        }
        //No jumps, then proceeds to search for normal moves
        if (legalMoves.size() == 0) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if (board[row][col] == player || board[row][col] == playerKing) {
                        if (isMove(player,row,col,row+1,col+1))
                            legalMoves.add(new CheckersMove(row,col,row+1,col+1));
                        if (isMove(player,row,col,row-1,col+1))
                            legalMoves.add(new CheckersMove(row,col,row-1,col+1));
                        if (isMove(player,row,col,row+1,col-1))
                            legalMoves.add(new CheckersMove(row,col,row+1,col-1));
                        if (isMove(player,row,col,row-1,col-1))
                            legalMoves.add(new CheckersMove(row,col,row-1,col-1));
                    }
                }
            }
        }
        //No legal move. Return for terminal.
        if (legalMoves.size() == 0)
            return null;
        else {
            CheckersMove[] legalMovesResult = new CheckersMove[legalMoves.size()];
            for (int i = 0; i < legalMoves.size(); i++)
                legalMovesResult[i] = legalMoves.get(i);
            return legalMovesResult;
        }
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        // Todo: Implement your getLegalJumpsFrom here.
        if (player != RED && player != BLACK)
            return null;
        int playerKing;
        if (player == RED)
            playerKing = RED_KING;
        else
            playerKing = BLACK_KING;

        //The number of moves are not determined.
        ArrayList<CheckersMove> legalJumps = new ArrayList<>();

        if (board[row][col] == player || board[row][col] == playerKing) {
            if (isJump(player, row, col, row+1, col-1, row+2, col-2))
                legalJumps.add(new CheckersMove(row, col, row+2, col-2));
            if (isJump(player, row, col, row+1, col+1, row+2, col+2))
                legalJumps.add(new CheckersMove(row, col, row+2, col+2));
            if (isJump(player, row, col, row-1, col-1, row-2, col-2))
                legalJumps.add(new CheckersMove(row, col, row-2, col-2));
            if (isJump(player, row, col, row-1, col+1, row-2, col+2))
                legalJumps.add(new CheckersMove(row, col, row-2, col+2));
        }
        if (legalJumps.size() == 0)
            return null;
        else {
            CheckersMove[] result = new CheckersMove[legalJumps.size()];
            for (int i = 0; i < legalJumps.size(); i++)
                result[i] = legalJumps.get(i);
            return result;
        }
    }

    /**
     * A checker method to check whether if the move is a moveable move.
     * Conditions: Move cannot exceed outside of the board array.
     *             Move cannot be done if there is a piece on the designated (x,y)
     *             Move cannot be done if normal red or black moves the opposite way
     * @param player The current player. RED = 1, BLACK = 3
     * @param fromRow player current row position
     * @param fromCol player current col position
     * @param toRow move to row
     * @param toCol move to col
     * @return True if the move is valid, false if the move is invalid
     */
    private boolean isMove(int player, int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8 || board[toRow][toCol] != EMPTY)
            return false;  // going off the board or there is a piece blocking.
        if (player == BLACK)
            return board[fromRow][fromCol] != BLACK || toRow >= fromRow;
        else
            return board[fromRow][fromCol] != RED || toRow <= fromRow;
    }

    /**
     * A checker method to check whether if the move is a jumpable move.
     * Conditions: Jump cannot exceed outside of the board array.
     *             Jump cannot be done if there is a piece on the designated (x,y)
     *             Jump over piece cannot be the same color piece as the current player
     *             Jump cannot be done if normal red or black moves the opposite way
     * @param player The current player. RED = 1, BLACK = 3
     * @param fromRow player current row position
     * @param fromCol player current col position
     * @param betweenR The row of the between piece
     * @param betweenC The col of the between piece
     * @param toRow move to row
     * @param toCol move to col
     * @return True if the jump is valid, false if the jump is invalid
     */
    private boolean isJump(int player, int fromRow, int fromCol, int betweenR, int betweenC, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8 || board[toRow][toCol] != EMPTY)
            return false;  //going off the board or there is a piece blocking.
        if (player == BLACK) {
            if (board[fromRow][fromCol] == BLACK && toRow < fromRow)
                return false;  // If black is moving upwards
            return board[betweenR][betweenC] == RED || board[betweenR][betweenC] == RED_KING;  // When the between piece is red we can eat.
        }
        else {
            if (board[fromRow][fromCol] == RED && toRow > fromRow)
                return false;  //If Red is moving downwards.
            return board[betweenR][betweenC] == BLACK || board[betweenR][betweenC] == BLACK_KING;  // When the between piece is black we can eat.
        }
    }
}
