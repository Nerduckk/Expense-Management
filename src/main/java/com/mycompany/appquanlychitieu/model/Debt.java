/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 *
 * @author 
 */
enum DebtType {
    BORROWING, 
    LENDING    
}

enum DebtStatus {
    ACTIVE,    
    COMPLETED, 
    BAD_DEBT   
}
class BaseEntity {
    public Long id;
    public String name;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}

class Debt extends BaseEntity {
    public DebtType type;
    public String personName;
    public BigDecimal principalAmount;
    public Double interestRate;
    public LocalDate startDate;
    public LocalDate dueDate;
    public DebtStatus status;
}
public class Debt {
    
}
