package hu.bme.mit.brszta;
import java.util.List;
import java.util.ArrayList;

/**
 * Possible states of a Cell.
 */
enum CellState {
    DEFAULT,
    FLAGGED,
    REVEALED
}

/**
 * One cell on the Minesweeper board. Either contains a mine, or not. A cell can be revealed or flagged.
 */
public abstract class Cell {

    /**
     * Current state of the cell.
     */
    public CellState cellState;
    /**
     * Number displayed after revealing the cell.
     */
    public int displayNumber;
    /**
     * A list of the cells adjacent to this cell.
     */
    protected List<Cell> neighbourCells;

    public Cell() {
        this.cellState = CellState.DEFAULT;
        this.neighbourCells = new ArrayList<Cell>(); // 2D matrix
    }

    /**
     * Add a neighbour cell to this cell. The cell counts the neighbour mines among its neighbours, and it can
     * reveal them in case of a recursive reveal.
     * @param neighbourCell The cell added as a neighbour.
     */
    public void addNeighbourCell(Cell neighbourCell) {
        neighbourCells.add(neighbourCell);
    }
    /**
     * Reveal the cell.
     *
     * @return Value of the cell. See specific overrides.
     */
    public abstract int reveal();

    //the method queries if the Cell contains a mine or not

    public abstract boolean isMine();

    /**
     * Reveal all the neighbour cells. Happens when the cell is revealed and it has no mines among its neighbours, ie.
     * its display value is blank.
     */

    protected void revealNeighbours() {
        // Return value is not important because none of them are mines.
        for (Cell cell : neighbourCells) {
            if (cell.cellState == CellState.DEFAULT) {
                cell.reveal();
            }
        }
    }

    /**
     * Flag the cell, or remove flag from the cell. Cycles FLAGGED and DEFAULT state.
     *
     * @return The new state of the cell.
     */
    public CellState flag() {
        if (cellState == CellState.DEFAULT) {
            cellState = CellState.FLAGGED;
        }
        else if (cellState == CellState.FLAGGED) {
            cellState = CellState.DEFAULT;
        }

        return cellState;
    }
}