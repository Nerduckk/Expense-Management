/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package user;
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
    public long getID(long ID){
        return ID;
    }
    public String getusername(String username){
        return username;
    } 
    public String getpassword(String password){
        return password;
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
