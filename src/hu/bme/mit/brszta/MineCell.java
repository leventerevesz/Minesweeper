package hu.bme.mit.brszta;

/**
 * A cell which contains a mine.
 */
public class MineCell extends Cell {
    /**
     * Reveal the cell, detonate the mine.
     * @return -1, indicating that the cell was a mine.
     */
    @Override
    public int reveal() {
        if (cellState == CellState.DEFAULT) {
            cellState = CellState.REVEALED;
            displayNumber = -1;
            return -1;
        }
        else {
            return 0;
        }
    }
    @Override
    public boolean isMine() {
        return true;
    }
}

