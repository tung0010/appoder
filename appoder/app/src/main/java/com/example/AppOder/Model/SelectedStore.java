package com.example.AppOder.Model;

public class SelectedStore {
    private static SelectedStore instance;
    private CuaHangModel store;

    private SelectedStore() {
        store=new CuaHangModel();
    }

    public static SelectedStore getInstance() {
        if (instance == null) {
            instance = new SelectedStore();
        }
        return instance;
    }

    public String getStoreName() {
        return store.getNameCH();
    }
    public CuaHangModel getStore(){return store;};

    public void setStoreName(CuaHangModel store) {
        this.store = store;
    }
}
