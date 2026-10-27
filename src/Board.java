import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** The 10x10 grid: holds the hidden ships and the visible cell states, and places ships randomly. */
public class Board {
    public static final int SIZE = 10;
    public static final int[] SHIP_SIZES = {5, 4, 3, 3, 2}; // descending order is more efficient

    private final Ship[][] shipGrid = new Ship[SIZE][SIZE];
    private final CellState[][] cellStates = new CellState[SIZE][SIZE];
    private final List<Ship> ships = new ArrayList<>();
    private final Random random;

    /** Creates a board and randomly places the standard fleet. */
    public Board() {
        this(new Random());
    }

    public Board(Random random) {
        this(random, true);
    }

    private Board(Random random, boolean placeFleet) {
        this.random = random;
        if (placeFleet) {
            reset();
        } else {
            clear();
        }
    }

    /** A board with no ships on it, so tests (or callers) can place ships by hand. */
    public static Board emptyBoard() {
        return new Board(new Random(), false);
    }

    private void clear() {
        ships.clear();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                shipGrid[r][c] = null;
                cellStates[r][c] = CellState.BLANK;
            }
        }
    }

    /** Clear everything and place a fresh random fleet. */
    public void reset() {
        clear();
        placeFleetRandomly();
    }

    /** Places ships in descending order: random orientation, random row/column, random open run. */
    private void placeFleetRandomly() {
        for (int size : SHIP_SIZES) {
            Ship ship = new Ship(size);
            boolean placed = false;
            while (!placed) {
                boolean horizontal = random.nextBoolean();
                int line = random.nextInt(SIZE);
                List<Integer> starts = findOpenRuns(horizontal, line, size);
                if (starts.isEmpty()) {
                    continue; // this row/column has no room; pick another
                }
                int start = starts.get(random.nextInt(starts.size()));
                placed = placeShip(ship, horizontal ? line : start, horizontal ? start : line, horizontal);
            }
        }
    }

    /** Starting offsets in a row/column where 'size' consecutive cells are free of ships. */
    private List<Integer> findOpenRuns(boolean horizontal, int line, int size) {
        List<Integer> starts = new ArrayList<>();
        for (int start = 0; start + size <= SIZE; start++) {
            boolean free = true;
            for (int i = 0; i < size && free; i++) {
                int r = horizontal ? line : start + i;
                int c = horizontal ? start + i : line;
                free = shipGrid[r][c] == null;
            }
            if (free) {
                starts.add(start);
            }
        }
        return starts;
    }

    /**
     * Places a ship with its first cell at (row, col). Fails (returns false, board unchanged)
     * if the ship would leave the board or overlap another ship. Touching is allowed.
     */
    public boolean placeShip(Ship ship, int row, int col, boolean horizontal) {
        int size = ship.getSize();
        int endRow = horizontal ? row : row + size - 1;
        int endCol = horizontal ? col + size - 1 : col;
        if (row < 0 || col < 0 || endRow >= SIZE || endCol >= SIZE) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            if (shipGrid[r][c] != null) {
                return false;
            }
        }
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            shipGrid[r][c] = ship;
            ship.addCell(r, c);
        }
        ships.add(ship);
        return true;
    }

    /** Fire at a cell and update what the player sees. */
    public ShotResult fire(int row, int col) {
        if (cellStates[row][col] != CellState.BLANK) {
            return ShotResult.ALREADY_FIRED;
        }
        Ship ship = shipGrid[row][col];
        if (ship == null) {
            cellStates[row][col] = CellState.MISS;
            return ShotResult.MISS;
        }
        cellStates[row][col] = CellState.HIT;
        return ship.hit() ? ShotResult.SUNK : ShotResult.HIT;
    }

    public CellState getCellState(int row, int col) {
        return cellStates[row][col];
    }

    public Ship getShipAt(int row, int col) {
        return shipGrid[row][col];
    }

    public List<Ship> getShips() {
        return ships;
    }

    public boolean allShipsSunk() {
        for (Ship s : ships) {
            if (!s.isSunk()) {
                return false;
            }
        }
        return !ships.isEmpty();
    }
}
