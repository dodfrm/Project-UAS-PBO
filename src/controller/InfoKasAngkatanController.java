/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import model.Mahasiswa;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import projectuaspbo.Database;
import view.*;

/**
 *
 * @author Dodi
 */
public class InfoKasAngkatanController {
    private final InfoKasAngkatanView view;
    private final Database database;
    private final String nim;

    public InfoKasAngkatanController(InfoKasAngkatanView view, String nim) {
        this.view = view;
        this.nim = view.getNim();
        this.database = new Database();
        this.view.addBackMouseListener(new BackMouseListener());
        this.view.addTableMouseListener(new TableMouseListener());
        this.view.addUpdateActionListener(new UpdateActionListener());
        this.view.addCariButtonActionListener(new CariButtonActionListener());
        this.view.addHapusActionListener(new HapusActionListener());
        this.view.addExportButtonActionListener(new ExportButtonActionListener());
        this.view.addExportCsvButtonActionListener(new ExportCsvButtonActionListener());
        updateUangLabel(nim);
    }
    
    private class TableMouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent evt) {
            int row = view.getSelectedRow();
            String nim = view.getNimAtRow(row);
            String bulan = view.getBulanAtRow(row);
            try {
                byte[] imageBytes = database.getBuktiBayarAngkatan(nim, bulan);
                if (imageBytes != null) {
                    ImageIcon imageIcon = new ImageIcon(imageBytes);
                    view.setBuktiImageIcon(imageIcon);
                } else {
                    view.setBuktiImageIcon(null);
                    view.setBuktiText("No image available");
                }
            } catch (SQLException e) {
                Logger.getLogger(BayarKasKelasController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    private class CariButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            String cariNama = view.getCariNama();

            if (cariNama != null && !cariNama.trim().isEmpty()) {
                try {
                    List<Object[]> kasAngkatan = database.searchKasAngkatanByName(cariNama);
                    view.updateTableData(kasAngkatan);
                    view.setCariNama("");
                } catch (SQLException ex) {
                }
            } else {
                view.dispose();
                InfoKasAngkatanView kasAngkatan = new InfoKasAngkatanView(nim);
                InfoKasAngkatanController kasController = new InfoKasAngkatanController(kasAngkatan, nim);
                kasController.loadKasAngkatan(nim);
                kasAngkatan.setVisible(true);
            }
        }
    }
    
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int row = view.getSelectedRow();
            if (row != -1) {
                String nim = view.getNimAtRow(row);
                String bulan = view.getBulanAtRow(row);
                int status = view.statusComboBox.getSelectedIndex();
                try {
                    database.updateStatusAngkatan(nim, bulan, status);
                    JOptionPane.showMessageDialog(null, "Status updated successfully!");
                    updateUangLabel(nim);
                    loadKasAngkatan(nim);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to update status: " + ex.getMessage());
                }
            }
        }
    }
    
    private class ExportButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            exportTableToExcel(view.getKasAngkatanTable());
        }
    }
    
    private class ExportCsvButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            exportTableToCSV(view.getKasAngkatanTable());
        }
    }
    
    public void exportTableToExcel(JTable table) {
        try {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Specify a file to save");
            int userSelection = jFileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File saveFile = jFileChooser.getSelectedFile();

                if (saveFile != null) {
                    // Append .xlsx if not already present
                    if (!saveFile.toString().endsWith(".xlsx")) {
                        saveFile = new File(saveFile.toString() + ".xlsx");
                    }

                    try (Workbook wb = new XSSFWorkbook();
                        FileOutputStream out = new FileOutputStream(saveFile)) {

                        Sheet sheet = wb.createSheet("DataKasAngkatan");

                        // Create header row
                        org.apache.poi.ss.usermodel.Row rowCol = sheet.createRow(0);
                        for (int i = 0; i < table.getColumnCount(); i++) {
                            Cell cell = rowCol.createCell(i);
                            cell.setCellValue(table.getColumnName(i));
                        }
                        Cell uangHeaderCell = rowCol.createCell(table.getColumnCount());
                        uangHeaderCell.setCellValue("Uang");

                        // Create data rows
                        for (int j = 0; j < table.getRowCount(); j++) {
                            org.apache.poi.ss.usermodel.Row row = sheet.createRow(j + 1);
                            for (int k = 0; k < table.getColumnCount(); k++) {
                                Cell cell = row.createCell(k);
                                if (table.getValueAt(j, k) != null) {
                                    cell.setCellValue(table.getValueAt(j, k).toString());
                                }
                            }
                            Cell uangCell = row.createCell(table.getColumnCount());
                            uangCell.setCellValue(10000);
                        }
                        wb.write(out);
                    }
                    JOptionPane.showMessageDialog(view, "Export berhasil ke: " + saveFile.getAbsolutePath());
                    openFile(saveFile.toString());
                } else {
                    JOptionPane.showMessageDialog(view, "Dibatalkan");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(view, "File tidak ditemukan: " + e.getMessage());
        } catch (IOException io) {
            System.out.println(io);
            JOptionPane.showMessageDialog(view, "Error saat menulis file: " + io.getMessage());
        }
    }
    
    public void exportTableToCSV(JTable table) {
        try {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Specify a file to save");
            int userSelection = jFileChooser.showSaveDialog(view);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File saveFile = jFileChooser.getSelectedFile();

                if (saveFile != null) {
                    // Append .csv if not already present
                    if (!saveFile.toString().endsWith(".csv")) {
                        saveFile = new File(saveFile.toString() + ".csv");
                    }

                    try (FileWriter writer = new FileWriter(saveFile)) {
                        // Write header row
                        for (int i = 0; i < table.getColumnCount(); i++) {
                            writer.append(table.getColumnName(i));
                            writer.append(",");
                        }
                        writer.append("Uang\n");

                        // Write data rows
                        for (int j = 0; j < table.getRowCount(); j++) {
                            for (int k = 0; k < table.getColumnCount(); k++) {
                                Object value = table.getValueAt(j, k);
                                if (value != null) {
                                    writer.append(value.toString());
                                }
                                writer.append(",");
                            }
                            writer.append("10000\n");
                        }
                        JOptionPane.showMessageDialog(view, "Export berhasil ke: " + saveFile.getAbsolutePath());
                        openFile(saveFile.toString());
                    }
                } else {
                    JOptionPane.showMessageDialog(view, "Dibatalkan");
                }
            }
        } catch (IOException io) {
            System.out.println(io);
            JOptionPane.showMessageDialog(view, "Error saat menulis file: " + io.getMessage());
        }
    }

    private void openFile(String toString) {
       try {
            File path = new File(toString);
            Desktop.getDesktop().open(path);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
    
    private class HapusActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int row = view.getSelectedRow();
            if (row != -1) {
                String nim = view.getNimAtRow(row);
                String bulan = view.getBulanAtRow(row);
                try {
                    int option = JOptionPane.showConfirmDialog(view, "Are you sure you want to Delete This?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        database.deleteKasAngkatan(nim, bulan);
                        JOptionPane.showMessageDialog(null, "Delete successfully!");
                        updateUangLabel(nim);
                        loadKasAngkatan(nim);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to Delete: " + ex.getMessage());
                }
            }
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
    
    private void updateUangLabel(String nim) {
        try {
            int countStatusOne = database.countConfirmedPaymentsAngkatan(nim);
            int totalUang = countStatusOne * 10000;
            view.setUangText("Rp." + totalUang);
        } catch (SQLException e) {
        }
    }
    
    public void loadKasAngkatan(String nim) {
        try {
            List<Object[]> data = database.getKasAngkatan(nim);
            view.updateTableData(data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view, "Error fetching data: " + ex.getMessage());
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