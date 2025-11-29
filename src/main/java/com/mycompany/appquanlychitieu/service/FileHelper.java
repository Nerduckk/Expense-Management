package com.mycompany.appquanlychitieu.service;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Admin
 */
public class FileHelper {

    public static <T> void saveToFile(List<T> data, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
            System.out.println("Đã lưu thành công: " + fileName);
        } catch (IOException e) {
            System.err.println("Lỗi khi lưu file " + fileName + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return new ArrayList<>(); 
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi khi đọc file " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
