import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/** The Swing view/controller: title, 10x10 board, status counters, Play Again and Quit. */
public class BattleshipFrame extends JFrame {
    private final BattleshipGame game = new BattleshipGame();
    private final BoardCellButton[][] cells = new BoardCellButton[Board.SIZE][Board.SIZE];

    private final JLabel missLabel = new JLabel();
    private final JLabel strikeLabel = new JLabel();
    private final JLabel totalMissLabel = new JLabel();
    private final JLabel totalHitLabel = new JLabel();

    public BattleshipFrame() {
        super("Battleship");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel title = new JLabel("BATTLESHIP", JLabel.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        add(buildBoardPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        refreshDisplay();
        setMinimumSize(new Dimension(560, 700));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildBoardPanel() {
        JPanel panel = new JPanel(new GridLayout(Board.SIZE, Board.SIZE));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        panel.setPreferredSize(new Dimension(520, 520));
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                BoardCellButton cell = new BoardCellButton(r, c);
                cell.addActionListener(e -> cellClicked((BoardCellButton) e.getSource()));
                cells[r][c] = cell;
                panel.add(cell);
            }
        }
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel status = new JPanel(new GridLayout(2, 2, 10, 4));
        status.add(missLabel);
        status.add(strikeLabel);
        status.add(totalMissLabel);
        status.add(totalHitLabel);

        JButton playAgain = new JButton("Play Again");
        playAgain.addActionListener(e -> playAgainClicked());
        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> quitClicked());
        JPanel buttons = new JPanel();
        buttons.add(playAgain);
        buttons.add(quit);

        JPanel bottom = new JPanel(new BorderLayout(4, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        bottom.add(status, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.SOUTH);
        return bottom;
    }

    private void cellClicked(BoardCellButton cell) {
        ShotResult result = game.fire(cell.getRow(), cell.getCol());
        if (result == ShotResult.ALREADY_FIRED) {
            return;
        }
        cell.setState(game.getBoard().getCellState(cell.getRow(), cell.getCol()));
        cell.setEnabled(false);
        refreshDisplay();

        if (result == ShotResult.SUNK && !game.isWon()) {
            JOptionPane.showMessageDialog(this, "You sunk a ship!", "Sunk!", JOptionPane.INFORMATION_MESSAGE);
        }
        if (game.isWon()) {
            endGame("You sunk the final ship. YOU WIN!");
        } else if (game.isLost()) {
            endGame("Three strikes. YOU LOSE!");
        } else if (game.lastShotWasStrike()) {
            JOptionPane.showMessageDialog(this,
                    "Five misses in a row: that's a strike! (" + game.getStrikeCount() + "/" + BattleshipGame.MAX_STRIKES + ")",
                    "Strike", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Win/loss message, then prompt to replay. */
    private void endGame(String message) {
        int choice = JOptionPane.showConfirmDialog(this, message + "\nPlay again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }

    private void playAgainClicked() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to start a new game?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    private void quitClicked() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Quit this game. Would you like to play again instead of exiting?",
                "Quit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        } else {
            System.exit(0);
        }
    }

    private void startNewGame() {
        game.newGame();
        for (BoardCellButton[] row : cells) {
            for (BoardCellButton cell : row) {
                cell.setState(CellState.BLANK);
                cell.setEnabled(true);
            }
        }
        refreshDisplay();
    }

    private void refreshDisplay() {
        missLabel.setText("Miss: " + game.getMissCount() + " / " + BattleshipGame.MISSES_PER_STRIKE);
        strikeLabel.setText("Strikes: " + game.getStrikeCount() + " / " + BattleshipGame.MAX_STRIKES);
        totalMissLabel.setText("Total Misses: " + game.getTotalMissCount());
        totalHitLabel.setText("Total Hits: " + game.getTotalHitCount());
    }
}
