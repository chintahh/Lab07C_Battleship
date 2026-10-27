import javax.swing.SwingUtilities;

/** Entry point. */
public class BattleshipRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipFrame().setVisible(true));
    }
}
