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
public class KasKelas implements Serializable{
    private String nimMahasiswa;
    private String[] bulan;
    private byte[] buktiBayar;
    private int status;

    public KasKelas(String nimMahasiswa, String[] bulan, byte[] buktiBayar, int status) {
        this.nimMahasiswa = nimMahasiswa;
        this.bulan = bulan;
        this.buktiBayar = buktiBayar;
        this.status = status;
    }

    public String getNimMahasiswa() {
        return nimMahasiswa;
    }

    public void setNimMahasiswa(String nimMahasiswa) {
        this.nimMahasiswa = nimMahasiswa;
    }

    public String[] getBulan() {
        return bulan;
    }

    public void setBulan(String[] bulan) {
        this.bulan = bulan;
    }

    public byte[] getBuktiBayar() {
        return buktiBayar;
    }

    public void setBuktiBayar(byte[] buktiBayar) {
        this.buktiBayar = buktiBayar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
