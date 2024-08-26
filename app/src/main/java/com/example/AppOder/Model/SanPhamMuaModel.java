package com.example.AppOder.Model;

public class SanPhamMuaModel {
    String nameSP, size, ghiChu,maSP;
    Integer gia,soLuong;

    public SanPhamMuaModel() {
    }

    public SanPhamMuaModel(String nameSP, String size, String ghiChu, String maSP, Integer gia, Integer soLuong) {
        this.nameSP = nameSP;
        this.size = size;
        this.ghiChu = ghiChu;
        this.maSP = maSP;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getNameSP() {
        return nameSP;
    }

    public void setNameSP(String nameSP) {
        this.nameSP = nameSP;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Integer getGia() {
        return gia;
    }

    public void setGia(Integer gia) {
        this.gia = gia;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }

}
