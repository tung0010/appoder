package com.example.AppOder.Model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoaDonModel {
    String maHd,maKh,ngayL,gioL,nameCH,diachiCH,location;

    Integer tongTien,trangThaiD;

    List<SanPhamMuaModel> sanPhamMua = new ArrayList<>();

    public Integer getTongTien() {
        return tongTien;
    }

    public void setTongTien(Integer tongTien) {
        this.tongTien = tongTien;
    }

    public List<SanPhamMuaModel> getSanPhamMua() {
        return sanPhamMua;
    }

    public void setSanPhamMua(List<SanPhamMuaModel> sanPhamMua) {
        this.sanPhamMua = sanPhamMua;
    }

    public String getSanPhamMua_TenSP(){
        String SSmua=sanPhamMua.get(0).nameSP;
        for (int i=1;i<sanPhamMua.size();i++) {
            SSmua=SSmua +", "+sanPhamMua.get(i).nameSP;
        }
        return SSmua;
    }

    public Integer getSoSanPhamMua(){
        return sanPhamMua.size();
    }
    public HoaDonModel() {
    }
    public HoaDonModel(String maHd, String maKh, String ngayL, String gioL, String nameCH, String diachiCH, String location, Integer trangThaiD, SanPhamMuaModel sanPhamMua) {
        this.maHd = maHd;
        this.maKh = maKh;
        this.ngayL = ngayL;
        this.gioL = gioL;
        this.nameCH = nameCH;
        this.diachiCH = diachiCH;
        this.location = location;
        this.trangThaiD = trangThaiD;
        this.sanPhamMua = Arrays.asList(new SanPhamMuaModel[]{sanPhamMua});
    }

    public String getMaHd() {
        return maHd;
    }

    public void setMaHd(String maHd) {
        this.maHd = maHd;
    }

    public String getMaKh() {
        return maKh;
    }

    public void setMaKh(String maKh) {
        this.maKh = maKh;
    }

    public String getNgayL() {
        return ngayL;
    }

    public void setNgayL(String ngayL) {
        this.ngayL = ngayL;
    }

    public String getGioL() {
        return gioL;
    }

    public void setGioL(String gioL) {
        this.gioL = gioL;
    }

    public String getNameCH() {
        return nameCH;
    }

    public void setNameCH(String nameCH) {
        this.nameCH = nameCH;
    }

    public String getDiachiCH() {
        return diachiCH;
    }

    public void setDiachiCH(String diachiCH) {
        this.diachiCH = diachiCH;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer isTrangThaiD() {
        return trangThaiD;
    }

    public void setTrangThaiD(Integer  trangThaiD) {
        this.trangThaiD = trangThaiD;
    }

}
