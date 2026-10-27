/** Game rules and counters: miss streak, strikes, total misses, total hits, win/loss. */
public class BattleshipGame {
    public static final int MISSES_PER_STRIKE = 5;
    public static final int MAX_STRIKES = 3;

    public enum Status { PLAYING, WON, LOST }

    private Board board;
    private int missCount;
    private int strikeCount;
    private int totalMissCount;
    private int totalHitCount;
    private boolean lastShotWasStrike;
    private Status status;

    public BattleshipGame() {
        this(new Board());
    }

    /** Lets tests supply a board with known ship positions. */
    public BattleshipGame(Board board) {
        this.board = board;
        resetCounters();
    }

    /** Start over with a freshly randomized board. */
    public void newGame() {
        board = new Board();
        resetCounters();
    }

    private void resetCounters() {
        missCount = 0;
        strikeCount = 0;
        totalMissCount = 0;
        totalHitCount = 0;
        lastShotWasStrike = false;
        status = Status.PLAYING;
    }

    /** Fire at a cell. Clicks on already-fired cells or after the game ends do not count. */
    public ShotResult fire(int row, int col) {
        lastShotWasStrike = false;
        if (status != Status.PLAYING) {
            return ShotResult.ALREADY_FIRED;
        }
        ShotResult result = board.fire(row, col);
        switch (result) {
            case HIT:
            case SUNK:
                totalHitCount++;
                missCount = 0;
                if (board.allShipsSunk()) {
                    status = Status.WON;
                }
                break;
            case MISS:
                missCount++;
                totalMissCount++;
                if (missCount >= MISSES_PER_STRIKE) {
                    strikeCount++;
                    missCount = 0;
                    lastShotWasStrike = true;
                    if (strikeCount >= MAX_STRIKES) {
                        status = Status.LOST;
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

    public Board getBoard() { return board; }
    public int getMissCount() { return missCount; }
    public int getStrikeCount() { return strikeCount; }
    public int getTotalMissCount() { return totalMissCount; }
    public int getTotalHitCount() { return totalHitCount; }
    public boolean lastShotWasStrike() { return lastShotWasStrike; }
    public Status getStatus() { return status; }
    public boolean isWon() { return status == Status.WON; }
    public boolean isLost() { return status == Status.LOST; }
    public boolean isOver() { return status != Status.PLAYING; }
}
