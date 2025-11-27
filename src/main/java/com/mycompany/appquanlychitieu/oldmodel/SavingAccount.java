/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.oldmodel;
import java.math.BigDecimal;
/**
 *
 * @author Duck
 */
public class SavingAccount {
    private long id;
    private String name;
    private BigDecimal goal;
    private BigDecimal openingBalance;
    private String note;

    public Saving(long id,String name, BigDecimal goal, BigDecimal openingBalance, String note) {
        this.id = id;
        this.name = name;
        this.goal = goal;
        this.openingBalance = openingBalance;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getGoal() {
        return goal;
    }

    public void setGoal(BigDecimal goal) {
        this.goal = goal;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal tienHienTai() {
        return openingBalance;
    }


    public void datMucTieu(BigDecimal muc) {
        if (muc == null || muc.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Goal must be >= 0");
        }
        this.goal = muc;
    }

    @Override
    public String toString() {
        return "SavingAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", goal=" + goal +
                ", openingBalance=" + openingBalance +
                ", note='" + note + '\'' +
                '}';
    }
}
