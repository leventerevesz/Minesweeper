package hu.bme.mit.brszta;

/**
 * A cell which does not contain a mine.
 */
public class EmptyCell extends Cell {

    /**
     * Reveal the cell, and initialize the {@link Cell#displayNumber}. If there are no mines in the neighbour cells,
     * all the neighbour cells are recursively revealed.
     * @return The {@link Cell#displayNumber}.
     * @see Cell#revealNeighbours()
     */
    @Override
    public int reveal() {
        if (cellState == CellState.DEFAULT){
            cellState = CellState.REVEALED;
            displayNumber = countNeighbourMines();
            if (displayNumber == 0) revealNeighbours();

            return displayNumber;
        }
        else {
            return 0;
        }
    }

    private int countNeighbourMines() {
        int neighbourMines = 0;
        for (Cell cell : neighbourCells) {
            if (cell instanceof MineCell) neighbourMines++;
        }
        return neighbourMines;
    }
    @Override
    public boolean isMine() {
        return false;
    }
}
