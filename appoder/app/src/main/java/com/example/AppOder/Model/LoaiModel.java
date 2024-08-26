package com.example.AppOder.Model;

public class LoaiModel {
    String nameLoai, imgLoai;

    LoaiModel(){

    }

    public LoaiModel(String nameLoai, String imgLoai) {
        this.nameLoai = nameLoai;
        this.imgLoai = imgLoai;
    }

    public String getNameLoai() {
        return nameLoai;
    }

    public void setNameLoai(String nameLoai) {
        this.nameLoai = nameLoai;
    }

    public String getImgLoai() {
        return imgLoai;
    }

    public void setImgLoai(String imgLoai) {
        this.imgLoai = imgLoai;
    }
}
