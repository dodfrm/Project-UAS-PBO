/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import model.Donasi;
import model.Mahasiswa;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class DonasiController {
    private final DonasiView view;
    private final Database database;
    private final String nim;
    
    public DonasiController(DonasiView view, String nim) {
        this.view = view;
        this.nim = nim;
        this.database = new Database();
        this.view.addOpenFileActionListener(new OpenFileActionListener());
        this.view.addSubmitActionListener(new SubmitActionListener());
        this.view.addAngkatanActionListener(new AngkatanActionListener());
        this.view.addBackMouseListener(new BackMouseListener());
    }
    
    private class OpenFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            openFile();
        }
    }
    
    private class AngkatanActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            String selectedAngkatan = (String) view.angkatanComboBox.getSelectedItem();
            switch (selectedAngkatan) {
                case "62":
                    view.setRekening("1726358128");
                    break;
                case "63":
                    view.setRekening("8123768172");
                    break;
                case "64":
                    view.setRekening("817368172");
                    break;
                case "65":
                    view.setRekening("9238719283");
                    break;
                default:
                    view.setRekening("");
                    break;
            }
        }
    }
    private class SubmitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                submitForm();
            } catch (IOException ex) {
                Logger.getLogger(DonasiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int getRole(String nim) {
        int role = -1;
        try {
            Mahasiswa user = Database.getInstance().getUserByNim(nim);
            role = user.getRole();
        } catch (SQLException ex) {
            Logger.getLogger(Donasi.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            view.setFilePath(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void goBackMahasiswa() {
        view.dispose();
        MenuMahasiswaView view = new MenuMahasiswaView(nim);
        MenuMahasiswaController controller = new MenuMahasiswaController(view,nim);
        view.setVisible(true);
    }
    
    private void goBackBendaharaAngkatan(){
        view.dispose();
        MenuBendaharaAngkatanView view = new MenuBendaharaAngkatanView(nim);
        MenuBendaharaAngkatanController controller = new MenuBendaharaAngkatanController(view,nim);
        view.setVisible(true);
    }
    
    private void goBackBendaharaKelas(){
        view.dispose();
        MenuBendaharaKelasView view = new MenuBendaharaKelasView(nim);
        MenuBendaharaKelasController controller = new MenuBendaharaKelasController(view,nim);
        view.setVisible(true);
    }
    
     public byte[] getFileData() throws IOException {
        String filePath = view.getFilePath();
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Masukkan Bukti Donasi");
            throw new IOException("Failed to read file: " + e.getMessage(), e);
        }
        return fileData;
    }
     
    private void submitForm() throws IOException {
        String nimMahasiswa = view.getNim();
        byte[] buktiTransfer = getFileData();
        String tujuanDonasi = view.getAngkatan();
        int jumlahDonasi = view.getJumlahDonasi();
        int status = 0;
        
        Donasi donasi = new Donasi(nimMahasiswa, tujuanDonasi, jumlahDonasi, buktiTransfer, status);
        try {
            database.insertDonasi(donasi);
            JOptionPane.showMessageDialog(view, "Donasi Berhasil, Terimakasih ");
            view.dispose();
            DonasiView kirimDonasi = new DonasiView(nim);
            DonasiController donasiController = new DonasiController(kirimDonasi,nim);
            kirimDonasi.setVisible(true);
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Gagal: " + e.getMessage());
        }
    }
    
}
