/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projectuaspbo;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.*;

/**
 *
 * @author Dodi
 */
public class Database implements Serializable{
    private static Database instance;
    public Database(){}
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    //connect database
    public static Connection connect() {
        Connection conn = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Connect to the SQLite database (Specify the file path to your database)
            String url = "jdbc:sqlite:F:\\.Kuliah\\STIS\\Semester 4\\PBO\\ProjectUASPBO\\ProjectUAS_db.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQLite JDBC driver not found.");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Failed to connect to SQLite database.");
        }
        return conn;
    }
    
    //fungsi register 
    public void registerMahasiswa(Mahasiswa mahasiswa) throws SQLException {
        Connection conn = Database.connect();
        if (conn != null) {
            String sql = "INSERT INTO mahasiswa(nim, nama, kelas, angkatan, jenisKelamin, password, role) VALUES(?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mahasiswa.getNim());
                pstmt.setString(2, mahasiswa.getNama());
                pstmt.setString(3, mahasiswa.getKelas());
                pstmt.setString(4, mahasiswa.getAngkatan());
                pstmt.setString(5, mahasiswa.getJenisKelamin());
                pstmt.setString(6, mahasiswa.getPassword());
                pstmt.setInt(7, mahasiswa.getRole());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Registration failed: " + e.getMessage());
            }
        }
    }
    
    //hashing password
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    //Login
    public Object[] login(String nim, String password) throws SQLException {
        Connection conn = Database.connect();
        if (conn != null) {
            String sql = "SELECT role, nim FROM mahasiswa WHERE nim = ? AND password = ?";
            ResultSet rs = null;
            Object[] result = new Object[3];
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nim);
                pstmt.setString(2, password);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    result[0] = true; // Login berhasil
                    result[1] = rs.getInt("role"); // status
                    result[2] = rs.getString("nim"); // User's nim
                } else {
                    result[0] = false; // Login gagal
                    throw new SQLException("Gagal Login");
                }
            } finally {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            }
            return result;
        }
        return null;
    }
    
    //mengambil data mahasiswa by nim
    public Mahasiswa getUserByNim(String nim) throws SQLException {
        Connection conn = Database.connect();
        try {
            // Prepare and execute the query
            String query = "SELECT * FROM mahasiswa WHERE nim = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nim);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Extract user information from the result set
                String nama = resultSet.getString("nama");
                String kelas = resultSet.getString("kelas");
                String jenisKelamin = resultSet.getString("jenisKelamin");
                String angkatan = resultSet.getString("angkatan");
                int role = resultSet.getInt("role");

                // Create a User object
                Mahasiswa user = new Mahasiswa(nim,nama,kelas,angkatan,jenisKelamin,role);
                return user;
            } else {
                // User not found
                return null;
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            conn.close();
        }
    }
    
    //pembayaran kas kelas
    public void insertKasKelas(KasKelas kasKelas) throws SQLException {
        Connection conn = connect();
        if (conn != null) {
            String sql = "INSERT INTO kasKelas(user_nim, bulan, buktiBayar, status) VALUES(?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kasKelas.getNimMahasiswa());
                pstmt.setBytes(3, kasKelas.getBuktiBayar());
                pstmt.setInt(4, kasKelas.getStatus());
                for (String bulan : kasKelas.getBulan()) {
                    pstmt.setString(2, bulan);
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new SQLException("Pembayaran Gagal: " + e.getMessage());
            } finally {
                conn.close();
            }
        } else {
            throw new SQLException("Failed to connect to the database.");
        }
    }
    
    //pembayaran kas kelas
    public void insertKasAngkatan(KasAngkatan kasAngkatan) throws SQLException {
        Connection conn = connect();
        if (conn != null) {
            String sql = "INSERT INTO kasAngkatan(user_nim, bulan, buktiBayar, status) VALUES(?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kasAngkatan.getNimMahasiswa());
                pstmt.setBytes(3, kasAngkatan.getBuktiBayar());
                pstmt.setInt(4, kasAngkatan.getStatus());
                for (String bulan : kasAngkatan.getBulan()) {
                    pstmt.setString(2, bulan);
                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new SQLException("Pembayaran Gagal: " + e.getMessage());
            } finally {
                conn.close();
            }
        } else {
            throw new SQLException("Failed to connect to the database.");
        }
    }
    
    //Donasi
    public void insertDonasi(Donasi donasi) throws SQLException{
        Connection conn = connect();
        if (conn != null) {
            String sql = "INSERT INTO donasi(user_nim, tujuanDonasi, jumlahDonasi, buktiTransfer, status) VALUES(?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, donasi.getNimMahasiswa());
                pstmt.setString(2, donasi.getTujuanDonasi());
                pstmt.setInt(3, donasi.getJumlahDonasi());
                pstmt.setBytes(4, donasi.getBuktiTransfer());
                pstmt.setInt(5, donasi.getStatus());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException("Donasi Gagal: " + e.getMessage());
            } finally {
                conn.close();
            }
        } else {
            throw new SQLException("Failed to connect to the database.");
        }
    }
    
    //untuk ppengecekan bulan yang telah ada di database
    public Set<String> getPaidMonths(String nim) throws SQLException {
        Set<String> paidMonths = new HashSet<>();
        String query = "SELECT bulan FROM kasKelas WHERE user_nim = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nim);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paidMonths.add(rs.getString("bulan"));
                }
            }
        }
        return paidMonths;
    }
    
    public Set<String> getPaidMonthsAngkatan(String nim) throws SQLException {
        Set<String> paidMonths = new HashSet<>();
        String query = "SELECT bulan FROM kasAngkatan WHERE user_nim = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nim);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paidMonths.add(rs.getString("bulan"));
                }
            }
        }
        return paidMonths;
    }
    
    //mangambil data untuk table
    public List<Object[]> getKasKelasData(String nim) throws SQLException { 
        List<Object[]> kasKelasData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, m.kelas " +
                       "FROM kasKelas k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE k.user_nim = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nim);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("kelas"),
                            rs.getString("bulan"),
                            status
                    };
                    kasKelasData.add(row);
                }
            }
        }
        return kasKelasData;
    }
    
    public List<Object[]> getKasAngkatanData(String nim) throws SQLException { //Untuk Bendahara Kelas
        List<Object[]> kasAngkatanData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, m.kelas " +
                       "FROM kasAngkatan k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE k.user_nim = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nim);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("kelas"),
                            rs.getString("bulan"),
                            status
                    };
                    kasAngkatanData.add(row);
                }
            }
        }
        return kasAngkatanData;
    }
    
    public String getClassForStudent(String nim) throws SQLException {
        String studentClass = null;
        String query = "SELECT kelas FROM mahasiswa WHERE nim = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nim);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    studentClass = rs.getString("kelas");
                }
            }
        }
        return studentClass;
    }

    public List<Object[]> getKasDataByClass(String studentClass) throws SQLException {
        List<Object[]> kasKelasData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, k.buktiBayar " +
                       "FROM kasKelas k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE m.kelas = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentClass);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("bulan"),
                            rs.getBytes("buktiBayar"),
                            status
                    };
                    kasKelasData.add(row);
                }
            }
        }
        return kasKelasData;
    }

    public List<Object[]> getKasData(String nim) throws SQLException {
        String studentClass = getClassForStudent(nim);
        if (studentClass != null) {
            return getKasDataByClass(studentClass);
        } else {
            throw new SQLException("Student with NIM " + nim + " not found.");
        }
    }
    
    //Menampilkan Bukti Bayar
    public byte[] getBuktiBayar(String nim, String bulan) throws SQLException {
        byte[] buktiBayar = null;
        String sql = "SELECT buktiBayar FROM kasKelas WHERE user_nim = ? AND bulan = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nim);
            pstmt.setString(2, bulan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    buktiBayar = rs.getBytes("buktiBayar");
                }
            }
        }
        return buktiBayar;
    }

    //update status
    public void updateStatus(String userNim, String bulan, int status) throws SQLException {
        String sql = "UPDATE kasKelas SET status = ? WHERE user_nim = ? AND bulan = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setString(2, userNim);
            pstmt.setString(3, bulan);
            pstmt.executeUpdate();
        }
    }
    
    //delete
    public void deleteKasKelas(String userNim, String bulan) throws SQLException{
        String sql = "DELETE FROM kasKelas WHERE user_nim = ? AND bulan = ?";
        
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userNim);
            pstmt.setString(2, bulan);
            pstmt.executeUpdate();
        }
    }
    
    private String getKelas(String nim) throws SQLException {
        return Database.getInstance().getUserByNim(nim).getKelas();
    }

    // menghitung jumlah uang yang masuk untuk Kas Kelas
    public int countConfirmedPayments(String nim) throws SQLException {
        String kelas = getKelas(nim);
        int count = 0;
        String sql = "SELECT COUNT(*) AS count FROM kasKelas "
                   + "JOIN mahasiswa ON kasKelas.user_nim = mahasiswa.nim "
                   + "WHERE kasKelas.status = 1 AND mahasiswa.kelas = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kelas);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            }
        }
        return count;
    }


    
    public int countConfirmedPaymentsAngkatanTotal(String angkatan) throws SQLException {
        int count = 0;
        String sql = "SELECT COUNT(*) AS count FROM kasAngkatan "
                   + "JOIN mahasiswa ON kasAngkatan.user_nim = mahasiswa.nim "
                   + "WHERE kasAngkatan.status = 1 AND mahasiswa.angkatan = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, angkatan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            }
        }
        return count;
    }

    
    
    //Search Mahasiswa
    public List<Object[]> searchKasKelasByName(String name) throws SQLException {
        List<Object[]> kasKelasData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, k.buktiBayar " +
                       "FROM kasKelas k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE m.nama LIKE ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("bulan"),
                            rs.getBytes("buktiBayar"),
                            status
                    };
                    kasKelasData.add(row);
                }
            }
        }
        return kasKelasData;
    }
    
    //Untuk Kas Angkatan
    public List<Object[]> getKasAngkatanDataByClass(String studentClass) throws SQLException {
        List<Object[]> kasAngkatanData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, k.buktiBayar " +
                       "FROM kasAngkatan k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE m.kelas = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentClass);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("bulan"),
                            rs.getBytes("buktiBayar"),
                            status
                    };
                    kasAngkatanData.add(row);
                }
            }
        }
        return kasAngkatanData;
    }

    public List<Object[]> getKasAngkatan(String nim) throws SQLException {
        String studentClass = getClassForStudent(nim);
        if (studentClass != null) {
            return getKasAngkatanDataByClass(studentClass);
        } else {
            throw new SQLException("Student with NIM " + nim + " not found.");
        }
    }
    
    //Menampilkan Bukti Bayar
    public byte[] getBuktiBayarAngkatan(String nim, String bulan) throws SQLException {
        byte[] buktiBayar = null;
        String sql = "SELECT buktiBayar FROM kasAngkatan WHERE user_nim = ? AND bulan = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nim);
            pstmt.setString(2, bulan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    buktiBayar = rs.getBytes("buktiBayar");
                }
            }
        }
        return buktiBayar;
    }

    //update status
    public void updateStatusAngkatan(String userNim, String bulan, int status) throws SQLException {
        String sql = "UPDATE kasAngkatan SET status = ? WHERE user_nim = ? AND bulan = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setString(2, userNim);
            pstmt.setString(3, bulan);
            pstmt.executeUpdate();
        }
    }
    
    //delete
    public void deleteKasAngkatan(String userNim, String bulan) throws SQLException{
        String sql = "DELETE FROM kasAngkatan WHERE user_nim = ? AND bulan = ?";
        
       try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userNim);
            pstmt.setString(2, bulan);
            pstmt.executeUpdate();
        }
    }
    
    //menghitung jumlah uang yang masuk Angkatan per kelas
    public int countConfirmedPaymentsAngkatan(String nim) throws SQLException {
        String kelas = getKelas(nim);
        int count = 0;
        String sql = "SELECT COUNT(*) AS count FROM kasAngkatan "
                   + "JOIN mahasiswa ON kasAngkatan.user_nim = mahasiswa.nim "
                   + "WHERE kasAngkatan.status = 1 AND mahasiswa.kelas = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kelas);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            }
        }
        return count;
    }
    
    //Search Mahasiswa
    public List<Object[]> searchKasAngkatanByName(String name) throws SQLException {
        List<Object[]> kasAngkatanData = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, k.buktiBayar " +
                       "FROM kasAngkatan k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE m.nama LIKE ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("bulan"),
                            rs.getBytes("buktiBayar"),
                            status
                    };
                    kasAngkatanData.add(row);
                }
            }
        }
        return kasAngkatanData;
    }
    
    //Untuk Menu Angkatan
    public List<Object[]> getAllMahasiswaByAngkatan(String angkatan) throws SQLException {
        List<Object[]> kasPerAngkatan = new ArrayList<>();
        String query = "SELECT k.user_nim, k.bulan, k.status, m.nama, m.kelas " +
                       "FROM kasAngkatan k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE m.angkatan = ? AND status = 1";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, angkatan);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getString("kelas"),
                            rs.getString("bulan"),
                            status
                    };
                    kasPerAngkatan.add(row);
                }
            }
        }
        return kasPerAngkatan;
    }
    
    //Menampilakn Donasi
    public List<Object[]> getDonasiByAngkatan(String angkatan) throws SQLException {
        List<Object[]> donasi = new ArrayList<>();
        String query = "SELECT k.id, k.user_nim, k.jumlahDonasi, k.buktiTransfer, k.status, m.nama, m.kelas " +
                       "FROM donasi k " +
                       "JOIN mahasiswa m ON k.user_nim = m.nim " +
                       "WHERE k.tujuanDonasi = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, angkatan);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status;
                    int statusValue = rs.getInt("status");
                    if (statusValue == 0) {
                        status = "Belum dikonfirmasi";
                    } else {
                        status = "Sudah dikonfirmasi";
                    }

                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("user_nim"),
                            rs.getString("nama"),
                            rs.getInt("jumlahDonasi"),
                            rs.getBytes("buktiTransfer"),
                            status
                    };
                    donasi.add(row);
                }
            }
        }
        return donasi;
    }
    
    //update status Donasi
    public void updateStatusDonasi(String userNim, String id, int status) throws SQLException {
        String sql = "UPDATE donasi SET status = ? WHERE user_nim = ? AND id = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setString(2, userNim);
            pstmt.setString(3, id);
            pstmt.executeUpdate();
        }
    }
    
    // Delete Donasi
    public void deleteDonasi(String userNim, String id) throws SQLException{
        String sql = "DELETE FROM donasi WHERE user_nim = ? AND id = ?";
       try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userNim);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        }
    }
    
    //bukti Donasi
    public byte[] getBuktiDonasi(String nim, String id) throws SQLException {
        byte[] buktiTransfer = null;
        String sql = "SELECT buktiTransfer FROM donasi WHERE user_nim = ? AND id = ?";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nim);
            pstmt.setString(2, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    buktiTransfer = rs.getBytes("buktiTransfer");
                }
            }
        }
        return buktiTransfer;
    }
    
    // hitung Donasi
    public int countConfirmedDonasi(String angkatan) throws SQLException {
        int count = 0;
        String sql = "SELECT SUM(jumlahDonasi) AS totalDonasi FROM donasi WHERE tujuanDonasi = ? AND status = 1";

        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, angkatan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("totalDonasi");
                }
            }
        }
        return count;
    }
}
