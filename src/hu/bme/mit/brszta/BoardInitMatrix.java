package hu.bme.mit.brszta;

import java.io.Serializable;

/**
 * Serializable board initializer matrix to send over the network.
 */
public class BoardInitMatrix implements Serializable {
    private boolean[][] booleanMatrix;
    public BoardInitMatrix(boolean[][] booleanMatrix){
        this.booleanMatrix = booleanMatrix;
    }

    public boolean[][] getMatrix() {
        return booleanMatrix;
    }
}