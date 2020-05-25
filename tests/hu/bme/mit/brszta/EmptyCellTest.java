package hu.bme.mit.brszta;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class EmptyCellTest {

    @Test
    void revealSetsCellState() {
        Cell cell = new EmptyCell();
        cell.reveal();
        assertEquals(CellState.REVEALED, cell.cellState);
    }

    @Test
    void revealReturnsNoOfNeighbourMines() {
        Cell cell = new EmptyCell();
        for (int i=0; i<4; i++) {
            cell.addNeighbourCell(new MineCell());
        }
        assertEquals(4, cell.reveal());
    }
}