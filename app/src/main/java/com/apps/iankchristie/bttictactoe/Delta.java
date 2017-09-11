package com.apps.iankchristie.bttictactoe;

/**
 * Created by iankc on 11/7/16.
 */

public class Delta {
    private int row;
    private int column;
    private int entry;

    public Delta(int r, int c, int e) {
        this.row = r;
        this.column = c;
        this.entry = e;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getEntry() {
        return this.entry;
    }

    public byte[] toBytes() {
        byte[] buf = new byte[3];
        buf[0] = (byte) row;
        buf[1] = (byte) column;
        buf[2] = (byte) entry;
        return buf;
    }

    @Override
    public String toString() {
        return "entry: " + this.getEntry() + "\trow: " + this.getRow() + "\tcolumn: " + this.getColumn();
    }

}
