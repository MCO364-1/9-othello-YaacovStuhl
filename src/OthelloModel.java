public class OthelloModel {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private final int[][] board = new int[8][8];
    private int currentPlayer = BLACK;
    
    // Saved game state
    private int[][] savedBoard = new int[8][8];
    private int savedCurrentPlayer = BLACK;
    private boolean hasSavedState = false;

    public OthelloModel() {
        board[3][3] = WHITE;
        board[4][4] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == BLACK) ? WHITE : BLACK;
    }

    public boolean isValidMove(int row, int col, int player) {
        if (board[row][col] != EMPTY) return false;
        return getFlippableDiscs(row, col, player).length > 0;
    }

    public void makeMove(int row, int col) {
        if (!isValidMove(row, col, currentPlayer)) return;
        board[row][col] = currentPlayer;

        for (int[] dir : getFlippableDiscs(row, col, currentPlayer)) {
            int r = row + dir[0], c = col + dir[1];
            while (board[r][c] == getOpponent(currentPlayer)) {
                board[r][c] = currentPlayer;
                r += dir[0];
                c += dir[1];
            }
        }
        switchPlayer();
    }

    private int[][] getFlippableDiscs(int row, int col, int player) {
        int[][] directions = {
                {-1,-1},{-1,0},{-1,1},
                { 0,-1},       { 0,1},
                { 1,-1},{ 1,0},{ 1,1}
        };

        java.util.List<int[]> validDirs = new java.util.ArrayList<>();
        for (int[] d : directions) {
            int r = row + d[0], c = col + d[1];
            boolean hasOpponentBetween = false;
            while (isInBounds(r, c) && board[r][c] == getOpponent(player)) {
                r += d[0];
                c += d[1];
                hasOpponentBetween = true;
            }
            if (hasOpponentBetween && isInBounds(r, c) && board[r][c] == player) {
                validDirs.add(d);
            }
        }
        return validDirs.toArray(new int[0][]);
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    private int getOpponent(int player) {
        return (player == BLACK) ? WHITE : BLACK;
    }

    public java.util.List<int[]> getValidMoves(int player) {
        java.util.List<int[]> validMoves = new java.util.ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (isValidMove(r, c, player)) {
                    validMoves.add(new int[]{r, c});
                }
            }
        }
        return validMoves;
    }

    public void makeRandomCPUMove() {
        java.util.List<int[]> validMoves = getValidMoves(currentPlayer);
        if (!validMoves.isEmpty()) {
            int randomIndex = (int) (Math.random() * validMoves.size());
            int[] move = validMoves.get(randomIndex);
            makeMove(move[0], move[1]);
        }
    }

    public void makeGreedyCPUMove() {
        java.util.List<int[]> validMoves = getValidMoves(currentPlayer);
        if (!validMoves.isEmpty()) {
            int[] bestMove = null;
            int maxCaptures = -1;
            
            for (int[] move : validMoves) {
                int captures = countCapturesForMove(move[0], move[1], currentPlayer);
                if (captures > maxCaptures) {
                    maxCaptures = captures;
                    bestMove = move;
                }
            }
            
            if (bestMove != null) {
                makeMove(bestMove[0], bestMove[1]);
            }
        }
    }

    private int countCapturesForMove(int row, int col, int player) {
        int totalCaptures = 0;
        int[][] directions = {
                {-1,-1},{-1,0},{-1,1},
                { 0,-1},       { 0,1},
                { 1,-1},{ 1,0},{ 1,1}
        };

        for (int[] d : directions) {
            int r = row + d[0], c = col + d[1];
            int capturesInDirection = 0;
            boolean hasOpponentBetween = false;
            
            while (isInBounds(r, c) && board[r][c] == getOpponent(player)) {
                r += d[0];
                c += d[1];
                capturesInDirection++;
                hasOpponentBetween = true;
            }
            
            if (hasOpponentBetween && isInBounds(r, c) && board[r][c] == player) {
                totalCaptures += capturesInDirection;
            }
        }
        
        return totalCaptures;
    }

    public void saveGameState() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                savedBoard[r][c] = board[r][c];
            }
        }
        savedCurrentPlayer = currentPlayer;
        hasSavedState = true;
    }

    public void restoreGameState() {
        if (hasSavedState) {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    board[r][c] = savedBoard[r][c];
                }
            }
            currentPlayer = savedCurrentPlayer;
        }
    }

    public boolean hasSavedState() {
        return hasSavedState;
    }

    public boolean isGameOver() {
        return getValidMoves(BLACK).isEmpty() && getValidMoves(WHITE).isEmpty();
    }

    public String getGameResult() {
        if (!isGameOver()) {
            return null;
        }

        int blackCount = 0;
        int whiteCount = 0;
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == BLACK) {
                    blackCount++;
                } else if (board[r][c] == WHITE) {
                    whiteCount++;
                }
            }
        }

        if (blackCount > whiteCount) {
            return "Black wins! Score: " + blackCount + " - " + whiteCount;
        } else if (whiteCount > blackCount) {
            return "White wins! Score: " + whiteCount + " - " + blackCount;
        } else {
            return "It's a draw! Score: " + blackCount + " - " + whiteCount;
        }
    }

    public void resetGame() {
        // Clear the board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = EMPTY;
            }
        }
        
        // Set up initial pieces
        board[3][3] = WHITE;
        board[4][4] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        
        currentPlayer = BLACK;
    }
}