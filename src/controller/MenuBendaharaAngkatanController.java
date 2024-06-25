/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import projectuaspbo.Database;
import view.DonasiView;
import view.InfoDonasiView;
import view.InfoKasPerAngkatanView;
import view.KasAngkatanView;
import view.KasKelasView;
import view.LoginView;
import view.MenuBendaharaAngkatanView;
import view.RiwayatBayarView;

/**
 *
 * @author Dodi
 */
public class MenuBendaharaAngkatanController {
    private final MenuBendaharaAngkatanView view;
    private String nim;
    
    public MenuBendaharaAngkatanController(MenuBendaharaAngkatanView view, String nim){
        this.view = view;
        this.nim = view.getNim();
        this.view.addBayarKasKelasLabelMouseListener(new BayarKasKelasMouseListener());
        this.view.addBayarKasAngkatanLabelMouseListener(new BayarKasAngkatanMouseListener());
        this.view.addDonasiLabelMouseListener(new DonasiMouseListener());
        this.view.addInformasiDonasiMouseListener(new InformasiDonasiMouseListener());
        this.view.addInformasiKasAngkatanMouseListener(new InformasiKasAngkatanMouseListener());
        this.view.addRiwayatBayarMouseListener(new RiwayatBayarMouseListener());
        this.view.addLogoutButtonMouseListener(new LogoutButtonMouseListener());
    }
    
    private class BayarKasKelasMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            KasKelasView bayar = new KasKelasView(nim);
            BayarKasKelasController bayarKasKelasController = new BayarKasKelasController(bayar,nim);
            bayar.setVisible(true);
        }
    }
    
    private class RiwayatBayarMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            RiwayatBayarView riwayatBayar = new RiwayatBayarView(nim);
            RiwayatBayarController riwayatBayarController = new RiwayatBayarController(riwayatBayar,nim);
            riwayatBayarController.loadKasKelasData(nim);
            riwayatBayarController.loadKasAngkatanData(nim);
            riwayatBayar.setVisible(true);
        }
    }
    
    private class DonasiMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            DonasiView kirimDonasi = new DonasiView(nim);
            DonasiController donasiController = new DonasiController(kirimDonasi,nim);
            kirimDonasi.setVisible(true);
        }
    }
    
    private class BayarKasAngkatanMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            KasAngkatanView bayar = new KasAngkatanView(nim);
            BayarKasAngkatanController bayarKasAngkatanController = new BayarKasAngkatanController(bayar,nim);
            bayar.setVisible(true);
        }
    }
    
    private class InformasiDonasiMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            InfoDonasiView donasi = new InfoDonasiView(nim);
            InfoDonasiController donasiController = new InfoDonasiController(donasi, nim);
            donasiController.loadDonasi(nim);
            donasi.setVisible(true);
        }
    }
    
    private class InformasiKasAngkatanMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            InfoKasPerAngkatanView kasAngkatan = new InfoKasPerAngkatanView(nim);
            InfoKasPerAngkatanController kasController = new InfoKasPerAngkatanController(kasAngkatan, nim);
            kasController.loadKasPerAngkatan(nim);
            kasAngkatan.setVisible(true);
        }
    }
    
    private class LogoutButtonMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
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
    }
}
