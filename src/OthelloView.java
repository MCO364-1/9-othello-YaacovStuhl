import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class OthelloButton extends JButton {
    private final int row;
    private final int col;

    public OthelloButton(int row, int col) {
        this.row = row;
        this.col = col;
        setBackground(new Color(0, 128, 0));
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

public class OthelloView extends JFrame {
    private final OthelloButton[][] cells = new OthelloButton[8][8];
    private final JLabel statusLabel = new JLabel("Black's Turn");
    private final JButton saveButton = new JButton("Save Game");
    private final JButton resetButton = new JButton("Reset to Saved");

    public OthelloView() {
        setTitle("Othello");
        setSize(600, 700);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Control panel for save and reset buttons
        JPanel controlPanel = new JPanel();
        controlPanel.add(saveButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                OthelloButton cell = new OthelloButton(r, c);
                cells[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }

    public OthelloButton[][] getCells() {
        return cells;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public void updateBoard(int[][] board) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                OthelloButton cell = cells[r][c];
                if (board[r][c] == OthelloModel.BLACK) {
                    cell.setIcon(makeDisc(Color.BLACK));
                } else if (board[r][c] == OthelloModel.WHITE) {
                    cell.setIcon(makeDisc(Color.WHITE));
                } else {
                    cell.setIcon(null);
                }
            }
        }
    }

    private Icon makeDisc(Color color) {
        BufferedImage img = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillOval(5, 5, 50, 50);
        g2.dispose();
        return new ImageIcon(img);
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }
}