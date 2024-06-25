/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class LoginController {
    private final LoginView login;
    private final Database database;

    public LoginController(LoginView login, Database database) {
        this.login = login;
        this.database = database;

        this.login.addLoginButtonListener((ActionEvent evt) -> {
            handleLogin();
        });
        
        this.login.addRegisterLabelMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                handleRegisterLabelClick();
            }
        });
    }

    private void handleLogin() {
        String nim = login.getNim();
        String password = login.getPassword();

        // Check for empty fields
        if (nim.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(login, "Please enter your NIM and password.", "Empty fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Hash the password before sending it for verification
        String hashedPassword = Database.hashPassword(password);

        try {
            Object[] loginResult = database.login(nim, hashedPassword);
            if (loginResult != null) {
                boolean isLoggedIn = (Boolean) loginResult[0];
                int role = (int) loginResult[1];
                if (isLoggedIn) {
                    switch (role) {
                        case 0 -> {
                            JOptionPane.showMessageDialog(login, "Login berhasil sebagai mahasiswa.", "Login berhasil", JOptionPane.INFORMATION_MESSAGE);
                            login.dispose();
                            MenuMahasiswaView view = new MenuMahasiswaView(nim);
                            MenuMahasiswaController controller = new MenuMahasiswaController(view,nim);
                            view.setVisible(true);
                        }

                        case 1 -> {
                            JOptionPane.showMessageDialog(login, "Login berhasil sebagai bendahara kelas.", "Login berhasil", JOptionPane.INFORMATION_MESSAGE);
                            login.dispose();
                            MenuBendaharaKelasView view = new MenuBendaharaKelasView(nim);
                            MenuBendaharaKelasController controller = new MenuBendaharaKelasController(view,nim);
                            view.setVisible(true);
                        }
                        case 2 -> {
                            JOptionPane.showMessageDialog(login, "Login berhasil sebagai bendahara angkatan.", "Login berhasil", JOptionPane.INFORMATION_MESSAGE);
                            login.dispose();
                            MenuBendaharaAngkatanView view = new MenuBendaharaAngkatanView(nim);
                            MenuBendaharaAngkatanController controller = new MenuBendaharaAngkatanController(view,nim);
                            view.setVisible(true);
                        }
                        default -> JOptionPane.showMessageDialog(login, "Role tidak dikenali.", "Login gagal", JOptionPane.ERROR_MESSAGE);
                    }
                    login.dispose();
                } else {
                    JOptionPane.showMessageDialog(login, "Invalid NIM or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(login, "Failed to connect to the database.", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(login, "Gagal login", "gagal", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRegisterLabelClick() {
        login.dispose();
        RegisterView register = new RegisterView();
        RegisterController registerController = new RegisterController(register, database);
        register.setVisible(true);
    }
}