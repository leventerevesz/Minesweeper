package hu.bme.mit.brszta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builds boards with the desired parameters.
 */
public class BoardBuilder {

    private boolean[][] boolean_board;
    /**
     * Create a board with random mine placement.
     * @param sizeX Horizontal size of the board.
     * @param sizeY Vertical size of the board.
     * @param numberOfMines Number of mines to place on the board.
     * @return Generated board.
     */
    public Board getRandomBoard(int sizeX, int sizeY, int numberOfMines) {
        boolean[][] mineMatrix = createRandomBooleanMatrix(sizeX, sizeY, numberOfMines);
        Cell[][] cellMatrix = createCellMatrix(mineMatrix);
        return new Board(cellMatrix);
    }

    /**
     * Create a board from a 2D array of boolean values.
     * @param booleanMatrix 2D array of boolean values. Contains {@code true} where mines should be placed,
     *      {@code false} everywhere else. Must be a valid rectangular matrix.
     * @return Generated board.
     */
    public Board getBoardFromBooleanMatrix(boolean[][] booleanMatrix) {
        Cell[][] cellMatrix = createCellMatrix(booleanMatrix);
        return new Board(cellMatrix);
    }

    public boolean[][] getBooleanMatrix() {
        return boolean_board;
    }

    private boolean[][] createRandomBooleanMatrix(int sizeX, int sizeY, int trues){
        // 1D lists can be easily shuffled.
        List<Boolean> initList = new ArrayList<Boolean>();
        for (int i = 0; i < sizeX * sizeY; i++) {
            if (i < trues) {
                initList.add(true);
            } else {
                initList.add(false);
            }
        }
        Collections.shuffle(initList);

        boolean[][] matrix = new boolean[sizeY][sizeX];
        for (int row = 0; row < sizeY; row++) {
            for (int col = 0; col < sizeX; col++) {
                matrix[row][col] = initList.get(row * sizeX + col);
            }
        }
        boolean_board = matrix;
        return matrix;
    }

    private Cell[][] createCellMatrix(boolean[][] mineMatrix)
    {
        int sizeX = mineMatrix[0].length;
        int sizeY = mineMatrix.length;

        Cell[][] cellMatrix = new Cell[sizeY][sizeX];
        for (int row = 0; row < sizeY; row++) {
            for (int col = 0; col < sizeX; col++) {
                Cell cell;
                if (mineMatrix[row][col]) {
                    cell = new MineCell();
                }
                else {
                    cell = new EmptyCell();
                }
                cellMatrix[row][col] = cell;
            }
        }
        return cellMatrix;
    }
}
