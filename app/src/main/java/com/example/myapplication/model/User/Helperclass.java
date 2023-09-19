package com.example.myapplication.model.User;

public class Helperclass {
    private String nama, pekerjaan, username, password;

    public Helperclass(){

    }
    public Helperclass(String nama, String pekerjaan, String username, String password){
        this.nama = nama;
        this.pekerjaan = pekerjaan;
        this.username = username;
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPekerjaan() {
        return pekerjaan;
    }

    public void setPekerjaan(String pekerjaan) {
        this.pekerjaan = pekerjaan;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
