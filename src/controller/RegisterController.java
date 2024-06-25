/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import model.Mahasiswa;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class RegisterController {
    private final RegisterView register;
    private final Database database;

    public RegisterController(RegisterView register, Database database) {
        this.register = register;
        this.database = database;

         this.register.addRegisterButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                handleRegister();
            }
        });
        
         this.register.addLoginLabelMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleLoginLabelClick();
            }
        });
    }

    private void handleRegister() {
        String nim = register.getNim();
        String nama = register.getNama();
        String kelas = register.getKelas();
        String angkatan = register.getAngkatan();
        String jenisKelamin = register.getJenisKelamin();
        String password = register.getPassword();
        String confirmPassword = register.getConfirmPassword();

        // Validasi input
        if (nim.isEmpty() || nama.isEmpty() || kelas.isEmpty() || jenisKelamin == null || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(register, "Semua field harus diisi.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(register, "Password dan konfirmasi password tidak cocok.");
            return;
        }

        if (!nim.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(register, "NIM harus berisi angka dengan panjang 9 karakter.");
            return;
        }

        if (!Pattern.matches("^[a-zA-Z\\s]+$", nama)) {
            JOptionPane.showMessageDialog(register, "Nama hanya boleh berisi huruf.");
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(register, "Password harus minimal 8 karakter.");
            return;
        }

        Mahasiswa mahasiswa = new Mahasiswa(nim, nama, kelas, angkatan, jenisKelamin, Database.hashPassword(password));
        try {
            database.registerMahasiswa(mahasiswa);
            register.clearForm();
            JOptionPane.showMessageDialog(register, "Registrasi berhasil!");
            register.dispose();
            Database database = new Database();
            LoginView login = new LoginView();
            new LoginController(login, database);
            login.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(register, "Gagal Melakukan Register: " + e.getMessage());
        }
    }
    
    private void handleLoginLabelClick() {
        register.dispose();
        LoginView login = new LoginView();
        LoginController loginController = new LoginController(login, database);
        login.setVisible(true);
    }
}
