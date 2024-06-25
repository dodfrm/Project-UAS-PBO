/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.Mahasiswa;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class RiwayatBayarController {
    private final RiwayatBayarView view;
    private final Database database;
    private final String nim;
    
    public RiwayatBayarController(RiwayatBayarView view, String nim){
        this.view = view;
        this.nim = view.getNim();
        this.database = new Database();
        this.view.addBackMouseListener(new RiwayatBayarController.BackMouseListener());
    }
    
    public void loadKasKelasData(String nim) {
        try {
            List<Object[]> data = database.getKasKelasData(nim);
            view.updateTableData(data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error fetching data: " + ex.getMessage());
        }
    }
    
    void loadKasAngkatanData(String nim) {
        try {
            List<Object[]> data = database.getKasAngkatanData(nim);
            view.updateTableDataAngkatan(data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error fetching data: " + ex.getMessage());
        }
    }
    
    private int getRole(String nim) {
        int role = -1;
        try {
            Mahasiswa user = Database.getInstance().getUserByNim(nim);
            role = user.getRole();
        } catch (SQLException ex) {
            Logger.getLogger(RiwayatBayarView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return role;
    }
    
    private class BackMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
        int role = getRole(nim);
        switch (role) {
                    case 0 ->{
                        goBackMahasiswa();
                    }
                    case 1 -> {
                        goBackBendaharaKelas();
                    }
                    case 2 -> {
                        goBackBendaharaAngkatan();
                    }
            }
        }
    }
    
    private void goBackMahasiswa() {
        view.dispose();
        MenuMahasiswaView view = new MenuMahasiswaView(nim);
        MenuMahasiswaController controller = new MenuMahasiswaController(view,nim);
        view.setVisible(true);
    }
    
    private void goBackBendaharaKelas(){
        view.dispose();
        MenuBendaharaKelasView view = new MenuBendaharaKelasView(nim);
        MenuBendaharaKelasController controller = new MenuBendaharaKelasController(view,nim);
        view.setVisible(true);
    }
    
    private void goBackBendaharaAngkatan(){
        view.dispose();
        MenuBendaharaAngkatanView view = new MenuBendaharaAngkatanView(nim);
        MenuBendaharaAngkatanController controller = new MenuBendaharaAngkatanController(view,nim);
        view.setVisible(true);
    }
}
