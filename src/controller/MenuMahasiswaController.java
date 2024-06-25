/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class MenuMahasiswaController {
    private final MenuMahasiswaView view;
    private String nim;
    
    public MenuMahasiswaController(MenuMahasiswaView view, String nim) {
        this.view = view;
        this.nim = view.getNim();
        this.view.addBayarKasKelasLabelMouseListener(new BayarKasKelasMouseListener());
        this.view.addBayarKasAngkatanLabelMouseListener(new BayarKasAngkatanMouseListener());
        this.view.addDonasiLabelMouseListener(new DonasiMouseListener());
        this.view.addRiwayatBayarMouseListener(new RiwayatBayarMouseListener());
        this.view.addLogoutButtonMouseListener(new LogoutButtonMouseListener());
    }
    
    private class BayarKasKelasMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            bayarKas();
        }
    }
    
    private class RiwayatBayarMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            riwayat();
        }
    }
    
    private class DonasiMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            donasi();
        }
    }
    
    private class BayarKasAngkatanMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            bayarKasAngkatan();
        }
    }
    
    private class LogoutButtonMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            logout();
        }
    }
    
    private void logout() {
        // Display a confirmation dialog
        int option = JOptionPane.showConfirmDialog(view, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);

        // Check the user's choice
        if (option == JOptionPane.YES_OPTION) {
            view.dispose();
            Database database = Database.getInstance();
            LoginView login = new LoginView();
            LoginController loginController = new LoginController(login, database);
            login.setVisible(true);
        }
    }
    
    private void bayarKas(){
        view.dispose();
        KasKelasView bayar = new KasKelasView(nim);
        BayarKasKelasController bayarKasKelasController = new BayarKasKelasController(bayar,nim);
        bayar.setVisible(true);
    }
    
    private void bayarKasAngkatan(){
        view.dispose();
        KasAngkatanView bayar = new KasAngkatanView(nim);
        BayarKasAngkatanController bayarKasAngkatanController = new BayarKasAngkatanController(bayar,nim);
        bayar.setVisible(true);
    }
    
    private void donasi(){
        view.dispose();
        DonasiView kirimDonasi = new DonasiView(nim);
        DonasiController donasiController = new DonasiController(kirimDonasi,nim);
        kirimDonasi.setVisible(true);
    }
    
    private void riwayat(){
        view.dispose();
        RiwayatBayarView riwayatBayar = new RiwayatBayarView(nim);
        RiwayatBayarController riwayatBayarController = new RiwayatBayarController(riwayatBayar,nim);
        riwayatBayarController.loadKasKelasData(nim);
        riwayatBayarController.loadKasAngkatanData(nim);
        riwayatBayar.setVisible(true);
    }
}
