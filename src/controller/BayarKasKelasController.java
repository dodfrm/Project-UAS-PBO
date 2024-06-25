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
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import model.KasKelas;
import model.Mahasiswa;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class BayarKasKelasController {

    private final KasKelasView view;
    private final Database database;
    private final String nim;
    

    public BayarKasKelasController(KasKelasView view, String nim) {
        this.view = view;
        this.nim = view.getNim();
        this.database = new Database();
        this.view.addCheckboxActionListener(new CheckboxActionListener());
        this.view.addOpenFileActionListener(new OpenFileActionListener());
        this.view.addSubmitActionListener(new SubmitActionListener());
        this.view.addBackMouseListener(new BackMouseListener());
        checkPaidMonths();
    }
    
    private void checkPaidMonths() {
        try {
            Set<String> paidMonths = database.getPaidMonths(nim);
            for (String month : paidMonths) {
                switch (month) {
                    case "Januari":
                        view.setJanuariEnabled(false);
                        break;
                    case "Februari":
                        view.setFebruariEnabled(false);
                        break;
                    case "Maret":
                        view.setMaretEnabled(false);
                        break;
                    case "April":
                        view.setAprilEnabled(false);
                        break;
                    case "Mei":
                        view.setMeiEnabled(false);
                        break;
                    case "Juni":
                        view.setJuniEnabled(false);
                        break;
                    case "Juli":
                        view.setJuliEnabled(false);
                        break;
                    case "Agustus":
                        view.setAgustusEnabled(false);
                        break;
                    case "September":
                        view.setSeptemberEnabled(false);
                        break;
                    case "Oktober":
                        view.setOktoberEnabled(false);
                        break;
                    case "November":
                        view.setNovemberEnabled(false);
                        break;
                    case "Desember":
                        view.setDesemberEnabled(false);
                        break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Error checking paid months: " + e.getMessage());
        }
    }
    
    private class CheckboxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            updateTotal();
        }
    }

    private class OpenFileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            openFile();
        }
    }

    private class SubmitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                submitForm();
            } catch (IOException ex) {
                Logger.getLogger(BayarKasKelasController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int getRole(String nim) {
        int role = -1;
        try {
            Mahasiswa user = Database.getInstance().getUserByNim(nim);
            role = user.getRole();
        } catch (SQLException ex) {
            Logger.getLogger(KasKelasView.class.getName()).log(Level.SEVERE, null, ex);
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

    private void updateTotal() {
        int total = 0;
        if (view.isJanuariSelected()) total += 5000;
        if (view.isFebruariSelected()) total += 5000;
        if (view.isMaretSelected()) total += 5000;
        if (view.isAprilSelected()) total += 5000;
        if (view.isMeiSelected()) total += 5000;
        if (view.isJuniSelected()) total += 5000;
        if (view.isJuliSelected()) total += 5000;
        if (view.isAgustusSelected()) total += 5000;
        if (view.isSeptemberSelected()) total += 5000;
        if (view.isOktoberSelected()) total += 5000;
        if (view.isNovemberSelected()) total += 5000;
        if (view.isDesemberSelected()) total += 5000;
        view.setTotalAmount(total);
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(view);
        File f = fileChooser.getSelectedFile();
        String path = f.getAbsolutePath();
        view.setFilePath(path);
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
    
    public byte[] getFileData() throws IOException {
        String filePath = view.getFilePath();
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];
        
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Masukkan Bukti Bayar");
            throw new IOException("Failed to read file: " + e.getMessage(), e);
        }
        return fileData;
    }
    private void submitForm() throws IOException {
        String nimMahasiswa = view.getNim();
        String[] bulan = getSelectedMonths();
        byte[] buktiBayar = getFileData();
        int status = 0;
        
        KasKelas kasKelas = new KasKelas(nimMahasiswa, bulan, buktiBayar, status);
        try {
            database.insertKasKelas(kasKelas);
            JOptionPane.showMessageDialog(view, "Pembayaran Berhasil");
            checkPaidMonths();
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Gagal : " + e.getMessage());
        }
    }
    
    private String[] getSelectedMonths() {
        ArrayList<String> selectedMonths = new ArrayList<>();
        if (view.isJanuariSelected()) selectedMonths.add("Januari");
        if (view.isFebruariSelected()) selectedMonths.add("Februari");
        if (view.isMaretSelected()) selectedMonths.add("Maret");
        if (view.isAprilSelected()) selectedMonths.add("April");
        if (view.isMeiSelected()) selectedMonths.add("Mei");
        if (view.isJuniSelected()) selectedMonths.add("Juni");
        if (view.isJuliSelected()) selectedMonths.add("Juli");
        if (view.isAgustusSelected()) selectedMonths.add("Agustus");
        if (view.isSeptemberSelected()) selectedMonths.add("September");
        if (view.isOktoberSelected()) selectedMonths.add("Oktober");
        if (view.isNovemberSelected()) selectedMonths.add("November");
        if (view.isDesemberSelected()) selectedMonths.add("Desember");
        return selectedMonths.toArray(new String[0]);
    }
}
