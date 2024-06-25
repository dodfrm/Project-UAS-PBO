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
public class Mahasiswa implements Serializable{
    private String nim;
    private String nama;
    private String kelas;
    private String angkatan;
    private String jenisKelamin;
    private String password;
    private int role;

    public Mahasiswa(String nim, String nama, String kelas,String angkatan, String jenisKelamin, String password) {
        this.nim = nim;
        this.nama = nama;
        this.kelas = kelas;
        this.angkatan = angkatan;
        this.jenisKelamin = jenisKelamin;
        this.password = password;
    }
    public Mahasiswa(String nim, String nama, String kelas,String angkatan, String jenisKelamin, int role){
        this.nim = nim;
        this.nama = nama;
        this.kelas = kelas;
        this.angkatan = angkatan;
        this.jenisKelamin = jenisKelamin;
        this.role = role;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(String angkatan) {
        this.angkatan = angkatan;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
