import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** One clickable board cell. Paints itself so colors look the same on every platform. */
public class BoardCellButton extends JButton {
    private static final Color WATER = new Color(20, 60, 110);
    private static final Color WAVE = new Color(173, 216, 230);   // light blue
    private static final Color MISS_COLOR = new Color(255, 215, 0); // yellow
    private static final Color HIT_COLOR = new Color(220, 20, 20);  // red

    private final int row;
    private final int col;
    private CellState state = CellState.BLANK;

    public BoardCellButton(int row, int col) {
        this.row = row;
        this.col = col;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(javax.swing.BorderFactory.createLineBorder(new Color(10, 30, 60)));
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public void setState(CellState state) {
        this.state = state;
        repaint();
    }

    public CellState getState() { return state; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(WATER);
        g2.fillRect(0, 0, getWidth(), getHeight());

        String text;
        Color color;
        switch (state) {
            case MISS:
                text = "M";
                color = MISS_COLOR;
                break;
            case HIT:
                text = "X";
                color = HIT_COLOR;
                break;
            default:
                text = "~";
                color = WAVE;
                break;
        }
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, getHeight() / 2)));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(color);
        g2.drawString(text, x, y);
        g2.dispose();
    }
}
