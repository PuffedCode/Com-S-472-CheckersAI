package edu.iastate.cs472.proj1;

/**
 * A class that does the alpha beta search for the AI
 * @author Brendan Yeong
 */
public class AlphaBetaSearch {
    private CheckersData board;
    public final static int SEARCH_DEPTH = 5;
    private int player;
    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    public void setCheckersData(CheckersData board) {
        this.board = board;
    }

    // Todo: You can implement your helper methods here

    /**
     * Check if it is a terminal state. Terminal state when there is no legal actions for the player during player turn.
     * @param board The current board
     * @param player Which player
     * @return True if it is at the leaf node.
     */
    private boolean isTerminal(CheckersData board, int player) {
        return board.getLegalMoves(player) == null;
    }

    /**
     * Returns the numbers of target piece. ie. RED = 1, RED_KING = 2, BLACK = 3, BLACK_KING = 4
     * @param piece RED = 1, RED_KING = 2, BLACK = 3, BLACK_KING = 4
     * @return number of the target pieces
     */
    private int getNumberOfPieces(CheckersData board, int piece) {
        int count = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board.pieceAt(row, col) == piece)
                    count++;
            }
        }
        return count;
    }

    /**
     * A simple score evaluator for the leaf state.
     * @param board the board that is to be evaluated
     * @param player the current player
     * @return The score that is evaluated with a simple formula.
     */
    private double evaluateScore(CheckersData board, int player){
        int playerKing;  // The constant representing a King belonging to player.
        if (player == 1)
            playerKing = 2;
        else
            playerKing = 4;
        if(isTerminal(board, player)){
            if(getNumberOfPieces(board, player) == 0 || getNumberOfPieces(board, playerKing) == 0)
                return Double.NEGATIVE_INFINITY;
            else if(board.getLegalMoves(player) == null)
                return Double.NEGATIVE_INFINITY;
            else
                return Double.POSITIVE_INFINITY;
        }
//=====================basic evaluation======================================
//        double basicScore = 0;
//        int reds = getNumberOfPieces(board, 1);
//        int redKings = getNumberOfPieces(board, 2);
//        int blacks = getNumberOfPieces(board, 3);
//        int blackKings = getNumberOfPieces(board, 4);
//
//        if(player == 1 || player == 2)
//            basicScore = (reds + redKings) - (blacks + blackKings);
//        else
//            basicScore = (blacks + blackKings) - (reds + redKings);
//        return basicScore;

//=====================improved evaluation======================================
        double score = 0;
        int reds = getNumberOfPieces(board, 1);
        int redKings = getNumberOfPieces(board, 2);
        int blacks = getNumberOfPieces(board, 3);
        int blackKings = getNumberOfPieces(board, 4);

        if(player == 1 || player == 2)
            score = (reds + redKings*10) - (blacks + blackKings*10);
        else
            score = (blacks + blackKings*10) - (reds + redKings*10);

        return score;
    }

    /**
     * A method that returns a future board or resulting board
     * @param currBoard current board that is to be process
     * @param move The given action to be move
     * @return A resulting board of the move.
     */
    private CheckersData futureBoard(CheckersData currBoard, CheckersMove move){
        CheckersData temp = new CheckersData();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                temp.board[row][col] = currBoard.board[row][col];
            }
        }
        temp.makeMove(move);
        return temp;
    }

    /**
     * Getting the max value for alpha
     * @param board board that is to be evaluated(not the actual board) ie. future board
     * @param player the current player
     * @param alpha the max value
     * @param beta the min value
     * @param depth the search depth
     * @return The max value
     */
    private double maxValue(CheckersData board, int player, double alpha, double beta, int depth){
        if (isTerminal(board, player) || depth == 0)
            return evaluateScore(board, player);
        double value = Double.NEGATIVE_INFINITY;
        for (CheckersMove action : board.getLegalMoves(player)) {
            value =  Math.max(value, minValue( //
                    futureBoard(board, action), 1, alpha, beta, depth - 1));
            if (value >= beta)
                return value;
            alpha = Math.max(alpha, value);
        }
        return value;
    }

    /**
     * Getting the min value for Beta
     * @param board board that is to be evaluated(not the actual board) ie. future board
     * @param player the current player
     * @param alpha the max value
     * @param beta the min value
     * @param depth the search depth
     * @return The min value
     */
    private double minValue(CheckersData board, int player, double alpha, double beta, int depth){
        if (isTerminal(board, player) || depth == 0)
            return evaluateScore(board, player);
        double value = Double.POSITIVE_INFINITY;
        for (CheckersMove action : board.getLegalMoves(player)) {
            value = Math.min(value, maxValue(//
                    futureBoard(board, action), 3, alpha, beta, depth - 1));
            if (value <= alpha)
                return value;
            beta = Math.min(beta, value);
        }
        return value;
    }

    /**
     * You need to implement the Alpha-Beta pruning algorithm here to
     * find the best move at current stage.
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();

        // Todo: return the move for the current state
        int searchDepth = SEARCH_DEPTH;
        CheckersMove optimalMove = legalMoves[0];
        double AB_ResultValue = Double.NEGATIVE_INFINITY;
        for(CheckersMove action : legalMoves){
            double value = minValue(futureBoard(board, action), 1, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, searchDepth);
            if (value > AB_ResultValue) {
                optimalMove = action;
                AB_ResultValue = value;
            }
        }
        // Here, we simply return the first legal move for demonstration.
        //return legalMoves[0];
        return optimalMove;
    }
}