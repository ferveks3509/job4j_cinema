package model;

public class Ticket {
    private int id;
    private int row;
    private int cell;
    private int account_id;

    public Ticket(int id, int row, int cell, int account_id) {
        this.id = id;
        this.row = row;
        this.cell = cell;
        this.account_id = account_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCell() {
        return cell;
    }

    public void setCell(int cell) {
        this.cell = cell;
    }

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

}
