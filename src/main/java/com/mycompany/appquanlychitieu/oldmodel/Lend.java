/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.oldmodel;
import java.time.LocalDate;
import java.math.BigDecimal;
/**
 *
 * @author Duc
 */
public class Lend {

    private long id;
    private long userId;
    private String name;
    private LocalDate startDate;
    private BigDecimal principal;
    private String note;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = (name != null && !name.trim().isEmpty()) ? name.trim() : "Khoan Cho Vay"; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = (startDate != null && !startDate.isAfter(LocalDate.now())) ? startDate : LocalDate.now(); }
    
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) {
        if (principal != null && principal.compareTo(BigDecimal.ZERO) > 0) {
            this.principal = principal;
        } else {
            this.principal = BigDecimal.ZERO; 
        }
    }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
