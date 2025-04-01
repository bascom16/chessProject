package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    public String toSimpleString() {
        char[] colVals = {'A', 'B','C', 'D', 'E', 'F', 'G', 'H'};
        int[] rowVals = {1, 2, 3, 4, 5, 6, 7, 8};
        char newCol = ' ';
        for (int i = 0; i < rowVals.length; i++) {
            if (col == rowVals[i]) {
                newCol = colVals[i];
            }
        }
        String newRow = String.valueOf(this.row);
        return newCol + newRow;
    }
}
