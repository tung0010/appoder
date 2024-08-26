package com.example.AppOder.Utils;

import com.example.AppOder.Model.SanPhamMuaModel;

import java.util.ArrayList;

public class DsSanPhamMuaUtil {
    private static DsSanPhamMuaUtil instance;
    private ArrayList<SanPhamMuaModel> dsSanPhamMuaUtil;

    private DsSanPhamMuaUtil() {
        dsSanPhamMuaUtil =new ArrayList<SanPhamMuaModel>();
        // Khởi tạo biến globalVariable ở đây nếu cần
    }

    public static synchronized DsSanPhamMuaUtil getInstance() {
        if (instance == null) {
            instance = new DsSanPhamMuaUtil();
        }
        return instance;
    }

    public ArrayList<SanPhamMuaModel> getDsSanPhamMuaUtil() {
        return dsSanPhamMuaUtil;
    }

    public void setDsSanPhamMuaUtil(ArrayList<SanPhamMuaModel>  value) {
        this.dsSanPhamMuaUtil = value;
    }
    public void xoaSanPhamMua(SanPhamMuaModel sp){
        this.dsSanPhamMuaUtil.remove(sp);
    }
    public void addDsSanPhamMuaUtil(SanPhamMuaModel value) {
        this.dsSanPhamMuaUtil.add(value);
    }
    public Integer getSoLuongSP(){
        int soLuong=0;
        for (SanPhamMuaModel i : dsSanPhamMuaUtil){
            soLuong +=i.getSoLuong();
        }
        return soLuong;
    }
}
