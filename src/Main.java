public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            OthelloModel model = new OthelloModel();
            OthelloView view = new OthelloView();
            new OthelloController(model, view);
        });
    }
}