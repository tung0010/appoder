package com.example.AppOder.Model;

public class CuaHangModel {
    String nameCH, diachi, location, imgUrl,sdt;
    double kinhdo, vido;
    float distance ;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public double getKinhdo() {
        return kinhdo;
    }

    public void setKinhdo(double kinhdo) {
        this.kinhdo = kinhdo;
    }

    public double getVido() {
        return vido;
    }

    public void setVido(double vido) {
        this.vido = vido;
    }

    public CuaHangModel(String nameCH, String diachi, String location, String imgUrl, String sdt, double kinhdo, double vido, float distance) {
        this.nameCH = nameCH;
        this.diachi = diachi;
        this.location = location;
        this.imgUrl = imgUrl;
        this.sdt = sdt;
        this.kinhdo = kinhdo;
        this.vido = vido;
        this.distance = distance;
    }

    public CuaHangModel() {
    }

    public String getNameCH() {
        return nameCH;
    }

    public void setNameCH(String nameCH) {
        this.nameCH = nameCH;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
}
