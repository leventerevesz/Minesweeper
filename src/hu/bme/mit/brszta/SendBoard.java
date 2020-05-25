package hu.bme.mit.brszta;

import java.io.Serializable;

public class SendBoard implements Serializable {
    private boolean[][] board;
    public SendBoard (boolean[][] board){
        this.board=board;
    }

    public boolean[][] getBoard() {
        return board;
    }
}