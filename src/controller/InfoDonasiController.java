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
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import projectuaspbo.Database;
import view.InfoDonasiView;
import view.MenuBendaharaAngkatanView;

/**
 *
 * @author Dodi
 */
public class InfoDonasiController {
    private final InfoDonasiView view;
    private final Database database;
    private String nim;

    public InfoDonasiController(InfoDonasiView view, String nim) {
        this.view = view;
        this.nim = view.getNim();
        this.database = new Database();
        this.view.addBackMouseListener(new BackMouseListener());
        this.view.addUpdateActionListener(new UpdateActionListener());
        this.view.addHapusActionListener(new HapusActionListener());
        this.view.addTableMouseListener(new TableMouseListener());
        this.view.addExportButtonActionListener(new ExportButtonActionListener()); 
        this.view.addExportCsvButtonActionListener(new ExportCsvButtonActionListener());
        updateUangLabel(nim);
    }
    
    
    private class ExportButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            exportTableToExcel(view.getKasDonasiTable());
        }
    }
    
    private class ExportCsvButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            exportTableToCSV(view.getKasDonasiTable());
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

                        // Create data rows
                        for (int j = 0; j < table.getRowCount(); j++) {
                            org.apache.poi.ss.usermodel.Row row = sheet.createRow(j + 1);
                            for (int k = 0; k < table.getColumnCount(); k++) {
                                Cell cell = row.createCell(k);
                                if (table.getValueAt(j, k) != null) {
                                    cell.setCellValue(table.getValueAt(j, k).toString());
                                }
                            }
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
                            if (i < table.getColumnCount() - 1) {
                                writer.append(",");
                            } else {
                                writer.append("\n");
                            }
                        }

                        // Write data rows
                        for (int j = 0; j < table.getRowCount(); j++) {
                            for (int k = 0; k < table.getColumnCount(); k++) {
                                Object value = table.getValueAt(j, k);
                                if (value != null) {
                                    writer.append(value.toString());
                                }
                                if (k < table.getColumnCount() - 1) {
                                    writer.append(",");
                                } else {
                                    writer.append("\n");
                                }
                            }
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
                String id = view.getIdAtRow(row);
                try {
                    int option = JOptionPane.showConfirmDialog(view, "Are you sure you want to Delete This?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        database.deleteDonasi(nim, id);
                        JOptionPane.showMessageDialog(null, "Delete successfully!");
                        loadDonasi(nim);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to Delete: " + ex.getMessage());
                }
            }
        }
    }
    
    public void loadDonasi(String nim) {
        try {
            String angkatan = getAngkatan(nim);
            List<Object[]> mahasiswaList = Database.getInstance().getDonasiByAngkatan(angkatan);
            DefaultTableModel tableModel = (DefaultTableModel) view.getKasDonasiTable().getModel();
            tableModel.setRowCount(0);

            for (Object[] row : mahasiswaList) {
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
        }
    }

    private String getAngkatan(String nim) throws SQLException {
        return Database.getInstance().getUserByNim(nim).getAngkatan();
    }
    
    private class UpdateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int row = view.getSelectedRow();
            if (row != -1) {
                String nim = view.getNimAtRow(row);
                String id = view.getIdAtRow(row);
                int status = view.statusComboBox.getSelectedIndex();
                try {
                    database.updateStatusDonasi(nim,id,status);
                    JOptionPane.showMessageDialog(null, "Status updated successfully!");
                    loadDonasi(nim);
                    updateUangLabel(nim);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to update status: " + ex.getMessage());
                }
            }
        }
    }
    
    private class TableMouseListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent evt) {
            int row = view.getSelectedRow();
            String nim = view.getNimAtRow(row);
            String id = view.getIdAtRow(row);
            try {
                byte[] imageBytes = database.getBuktiDonasi(nim,id);
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
    
    private class BackMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            view.dispose();
            MenuBendaharaAngkatanView menu = new MenuBendaharaAngkatanView(nim);
            MenuBendaharaAngkatanController controller = new MenuBendaharaAngkatanController(menu,nim);
            menu.setVisible(true);  
        }
    }
    
    private void updateUangLabel(String nim) {
        try {
            String angkatan = getAngkatan(nim);
            int countStatusOne = database.countConfirmedDonasi(angkatan);
            int totalUang = countStatusOne;
            view.setUangText("Rp." + totalUang);
        } catch (SQLException e) {
        }
    }
}
