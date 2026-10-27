import java.util.ArrayList;
import java.util.List;

/** A single ship: knows its size, where it sits, and how many hits it has taken. */
public class Ship {
    private final int size;
    private final List<int[]> cells = new ArrayList<>();
    private int hits = 0;

    public Ship(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Ship size must be at least 1");
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    /** Record that this ship occupies the given cell. */
    public void addCell(int row, int col) {
        cells.add(new int[]{row, col});
    }

    public List<int[]> getCells() {
        return cells;
    }

    /** Register a hit; returns true if this hit sank the ship. */
    public boolean hit() {
        if (hits < size) {
            hits++;
        }
        return isSunk();
    }

    public int getHits() {
        return hits;
    }

    public boolean isSunk() {
        return hits >= size;
    }
}
