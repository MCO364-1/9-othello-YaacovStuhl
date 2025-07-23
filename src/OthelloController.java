import javax.swing.*;
import java.awt.event.*;

public class OthelloController {
    private final OthelloModel model;
    private final OthelloView view;

    public OthelloController(OthelloModel model, OthelloView view) {
        this.model = model;
        this.view = view;

        // Add single event handler for all buttons
        OthelloButton[][] cells = view.getCells();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                cells[r][c].addActionListener(e -> handleButtonClick((OthelloButton) e.getSource()));
            }
        }

        // Add action listeners for save and reset buttons
        view.getSaveButton().addActionListener(e -> handleSave());
        view.getResetButton().addActionListener(e -> handleReset());

        view.updateBoard(model.getBoard());
        view.setVisible(true);
    }

    private void handleButtonClick(OthelloButton button) {
        int row = button.getRow();
        int col = button.getCol();
        handleMove(row, col);
    }

    private void handleMove(int row, int col) {
        if (model.isValidMove(row, col, model.getCurrentPlayer())) {
            model.makeMove(row, col);
            view.updateBoard(model.getBoard());
            updateStatus();
            
            // Check for game over after player move
            checkGameOver();
            
            // CPU always plays as White
            if (model.getCurrentPlayer() == OthelloModel.WHITE) {
                // Add a small delay to make CPU move visible
                Timer timer = new Timer(500, e -> {
                    model.makeGreedyCPUMove();
                    view.updateBoard(model.getBoard());
                    updateStatus();
                    
                    // Check for game over after CPU move
                    checkGameOver();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void handleSave() {
        model.saveGameState();
        JOptionPane.showMessageDialog(view, "Game saved successfully!", "Save Game", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleReset() {
        if (model.hasSavedState()) {
            int choice = JOptionPane.showConfirmDialog(view, 
                "Are you sure you want to reset to the saved game state?", 
                "Reset Game", 
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                model.restoreGameState();
                view.updateBoard(model.getBoard());
                updateStatus();
            }
        } else {
            JOptionPane.showMessageDialog(view, "No saved game state found!", "Reset Game", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkGameOver() {
        if (model.isGameOver()) {
            String result = model.getGameResult();
            int choice = JOptionPane.showConfirmDialog(view, 
                result + "\n\nWould you like to start a new game?", 
                "Game Over", 
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                model.resetGame();
                view.updateBoard(model.getBoard());
                updateStatus();
            }
        }
    }

    private void updateStatus() {
        String playerTurn = model.getCurrentPlayer() == OthelloModel.BLACK ? "Black" : "White";
        view.setStatusText(playerTurn + "'s Turn");
    }
}