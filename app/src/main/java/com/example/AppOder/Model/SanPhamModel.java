package com.example.AppOder.Model;

import android.util.Log;

public class SanPhamModel {
    String nameSP, imgSP, moTa, idLoai;
    float gia;
    boolean isBanChay;

    SanPhamModel(){

    }

    public SanPhamModel(String nameSP, String imgSP, String moTa, float gia, String idLoai, boolean isBanChay) {
        this.nameSP = nameSP;
        this.imgSP = imgSP;
        this.moTa = moTa;
        this.gia = gia;
        this.idLoai = idLoai;
        this.isBanChay = isBanChay;
    }

    public String getNameSP() {
        return nameSP;
    }

    public void setNameSP(String nameSP) {
        this.nameSP = nameSP;
    }

    public String getImgSP() {
        return imgSP;
    }

    public void setImgSP(String imgSP) {
        this.imgSP = imgSP;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public float getGia() {
        Log Log = null;
        Log.d("SanPhamModel", "Get Gia: " + gia);
        return gia;
    }

    public void setGia(float gia) {
        this.gia = gia;
    }

    public String getIdLoai() {
        return idLoai;
    }

    public void setIdLoai(String idLoai) {
        this.idLoai = idLoai;
    }

    public boolean isBanChay() {
        return isBanChay;
    }

    public void setBanChay(boolean banChay) {
        isBanChay = banChay;
    }
}
