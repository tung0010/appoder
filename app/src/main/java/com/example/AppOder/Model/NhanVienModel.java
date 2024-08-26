package com.example.AppOder.Model;

public class NhanVienModel {
    public String maNV,hoTen,chucVu;
    public double hsl,lcb,phuCap;
    public NhanVienModel(){}
    public NhanVienModel(String maNV, String hoTen, String chucVu, double hsl, double lcb, double phuCap) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
        this.hsl = hsl;
        this.lcb = lcb;
        this.phuCap = phuCap;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public double getHsl() {
        return hsl;
    }

    public void setHsl(float hsl) {
        this.hsl = hsl;
    }

    public double getLcb() {
        return lcb;
    }

    public void setLcb(float lcb) {
        this.lcb = lcb;
    }

    public double getPhuCap() {
        return phuCap;
    }

    public void setPhuCap(float phuCap) {
        this.phuCap = phuCap;
    }
}
