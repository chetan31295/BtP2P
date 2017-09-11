package com.apps.iankchristie.bttictactoe;

import android.widget.ImageButton;

/**
 * Created by iankc on 11/4/16.
 */

public class TicTacToe {
    public int[][] board;
    private final int EMPTY_SPOT = 0;
    private final int PLAYER_1 = 1;
    private final int PLAYER_2 = 2;

    public TicTacToe() {
        this.board = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    }

    public boolean acceptsChange(Delta change) {
        return board[change.getRow()][change.getColumn()] == EMPTY_SPOT;
    }

    public int updateBoard(Delta change) {
        board[change.getRow()][change.getColumn()] = change.getEntry();
        return (change.getEntry() == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }

    public boolean hasWinner() {
        return this.checkRows() || this.checkColumns() || this.checkDiagonals();
    }

    public boolean isCatsGame() {
        for(int i = 0; i < this.board.length; i++) {
            for(int j = 0; j < this.board[0].length; j++) {
                if(this.board[i][j] == EMPTY_SPOT) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRows() {
        return this.checkRow(0) || this.checkRow(1) || this.checkRow(2);
    }

    private boolean checkRow(int row) {
        boolean rowSame = ((this.board[row][0] == this.board[row][1]) &&
                (this.board[row][1] == this.board[row][2]));
        return this.board[row][0] != 0 && rowSame;
    }

    private boolean checkColumns() {
        return this.checkColumn(0) || this.checkColumn(1) || this.checkColumn(2);
    }

    private boolean checkColumn(int column) {
        boolean ColumnSame = ((this.board[0][column] == this.board[1][column]) &&
                (this.board[1][column] == this.board[2][column]));
        return this.board[0][column] != 0 && ColumnSame;
    }

    private boolean checkDiagonals() {
        return this.checkLeftRight() || this.checkRightLeft();
    }

    private boolean checkLeftRight() {
        boolean not0 = this.board[0][0] != 0;
        boolean diagSame = ((this.board[0][0] == this.board[1][1]) &&
                (this.board[1][1] == this.board[2][2]));
        return not0 && diagSame;
    }

    private boolean checkRightLeft() {
        boolean not0 = this.board[2][0] != 0;
        boolean diagSame = ((this.board[2][0] == this.board[1][1]) &&
                (this.board[1][1] == this.board[0][2]));
        return not0 && diagSame;
    }

    public int getPLAYER_1() {
        return PLAYER_1;
    }

    public int getPLAYER_2() {
        return PLAYER_2;
    }

    public int getEMPTY_SPOT() {
        return EMPTY_SPOT;
    }
}

