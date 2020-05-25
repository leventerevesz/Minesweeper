package hu.bme.mit.brszta;

/**
 * Playing board of the Minesweeper. Contains a matrix of cells. To create a board, use a {@link BoardBuilder}.
 */
public class Board {
    private Cell[][] cellMatrix;

    /**
     * Construct a board from a 2D array of cells.
     * @param cellMatrix 2D array of cells. Must be a valid matrix, ie. must contain a rectangular arrangement of cells.
     */
    public Board(Cell[][] cellMatrix){
        this.cellMatrix = cellMatrix;
        initNeighbourCells();
    }

    private void initNeighbourCells() {
        // Connect the adjacent cells to each cell in the cellMatrix.

        int sizeX = getSizeX();
        int sizeY = getSizeY();
        for (int row = 0; row < sizeY; row++) {
            for (int col = 0; col < sizeX; col++) {
                Cell cell = cellMatrix[row][col];
                if (row != 0 && col != 0)
                    cell.addNeighbourCell(cellMatrix[row-1][col-1]);
                if (row != 0)
                    cell.addNeighbourCell(cellMatrix[row-1][col]);
                if (row != 0 && col != sizeX-1)
                    cell.addNeighbourCell(cellMatrix[row-1][col+1]);
                if (col != 0)
                    cell.addNeighbourCell(cellMatrix[row][col-1]);
                if (col != sizeX-1)
                    cell.addNeighbourCell(cellMatrix[row][col+1]);
                if (row != sizeY-1 && col != 0)
                    cell.addNeighbourCell(cellMatrix[row+1][col-1]);
                if (row != sizeY-1)
                    cell.addNeighbourCell(cellMatrix[row+1][col]);
                if (row != sizeY-1 && col != sizeX-1)
                    cell.addNeighbourCell(cellMatrix[row+1][col+1]);
            }
        }
    }

    /**
     * Get horizontal size of the board, ie. how many cells are in a row.
     * @return Horizontal size of the board.
     */
    public int getSizeX(){
        return cellMatrix[0].length;
    }

    /**
     * Get vertical size of the board, ie. how many cells are in a column.
     * @return Vertical size of the board.
     */
    public int getSizeY(){
        return cellMatrix.length;
    }

    /**
     * Count every mine on the board.
     * @return Count of mines.
     */
    public int countMines() {
        int count = 0;
        for (Cell[] cellRow : cellMatrix) {
            for (Cell cell : cellRow) {
                if (cell instanceof MineCell) count++;
            }
        }
        return count;
    }

    /**
     * Count every flag on the board.
     * @return Count of flags.
     */
    public int countFlags() {
        int count = 0;
        for (Cell[] cellRow : cellMatrix) {
            for (Cell cell : cellRow) {
                if (cell.cellState == CellState.FLAGGED) count++;
            }
        }
        return count;
    }

    /**
     * Count revealed cells on the board.
     * @return Count of revealed cells.
     */
    public int countRevealed() {
        int count = 0;
        for (Cell[] cellRow : cellMatrix) {
            for (Cell cell : cellRow) {
                if (cell.cellState == CellState.REVEALED) count++;
            }
        }
        return count;
    }

    /**
     * Get cell on the given position.
     * @param row Row of the selected cell. Indexing starts from 0.
     * @param col Column of the selected cell. Indexing starts from 0.
     * @return The selected cell.
     */
    public Cell getCell(int row, int col) {
        return cellMatrix[row][col];
    }

    //count the number of mines revealed to check in GUI the win condition

    public int countRevealedMines() {
        int count = 0;
        for (Cell[] cellRow : cellMatrix) {
            for (Cell cell : cellRow) {
                if ((cell.cellState == CellState.FLAGGED) && cell.isMine()) count++;
            }
        }
        return count;
    }
}