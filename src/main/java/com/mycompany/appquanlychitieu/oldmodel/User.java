/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.appquanlychitieu.oldmodel;
import java.util.Scanner;
/**
 *
 * @author Duong Hoang
 */
public class User {
    private long ID;
    private String username;
    private String password;
    public User(long ID, String username, String password ){
        this.ID=ID;
        this.username=username;
        this.password=password;
    }
    
    public long getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setID(long ID) {
        if (ID>0)
            this.ID = ID;
    }

    public void setUsername(String username) {
        if (!username.equals(""))
            this.username = username;
    }

    public void setPassword(String password) {
        if (!password.equals(""))
            this.password = password;
    }
    
    public void login(){
        Scanner sc= new Scanner(System.in);
        System.out.print("username: ");
        username=sc.nextLine();
        System.out.print("password: ");
        password=sc.nextLine();
        System.out.println("Da dang nhap");
    }
    public void logout(){
        System.out.println("Da dang xuat");
    }
}
