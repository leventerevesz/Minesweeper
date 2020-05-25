package hu.bme.mit.brszta;

import java.io.Serializable;

/**
 * Serializable board initializer matrix to send over the network.
 */
public class MinePlacementMatrix implements Serializable {
    private boolean[][] booleanMatrix;
    public MinePlacementMatrix(boolean[][] booleanMatrix){
        this.booleanMatrix = booleanMatrix;
    }

    public boolean[][] getMatrix() {
        return booleanMatrix;
    }
}