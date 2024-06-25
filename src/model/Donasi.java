/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Dodi
 */
public class Donasi implements Serializable{
    private String nimMahasiswa;
    private String tujuanDonasi;
    private byte[] buktiTransfer;
    private int jumlahDonasi;
    private int status;
    
    public Donasi(String nimMahasiswa, String tujuanDonasi,int jumlahDonasi, byte[] buktiTransfer, int status) {
        this.nimMahasiswa = nimMahasiswa;
        this.tujuanDonasi = tujuanDonasi;
        this.buktiTransfer = buktiTransfer;
        this.jumlahDonasi = jumlahDonasi;
        this.status = status;
    }

    public int getJumlahDonasi() {
        return jumlahDonasi;
    }

    public void setJumlahDonasi(int jumlahDonasi) {
        this.jumlahDonasi = jumlahDonasi;
    }
        
    public String getNimMahasiswa() {
        return nimMahasiswa;
    }

    public void setNimMahasiswa(String nimMahasiswa) {
        this.nimMahasiswa = nimMahasiswa;
    }

    public String getTujuanDonasi() {
        return tujuanDonasi;
    }

    public void setTujuanDonasi(String tujuanDonasi) {
        this.tujuanDonasi = tujuanDonasi;
    }

    public byte[] getBuktiTransfer() {
        return buktiTransfer;
    }

    public void setBuktiTransfer(byte[] buktiTransfer) {
        this.buktiTransfer = buktiTransfer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
