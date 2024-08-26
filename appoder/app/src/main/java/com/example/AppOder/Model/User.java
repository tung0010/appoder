package com.example.AppOder.Model;

import java.util.List;

public class User {
    private String phoneNumber;
    private String gender;
    private String birthday;
    private List<String> voucherList;

    public List<String> getVoucherList() {
        return voucherList;
    }
    public void setVoucherList(List<String> voucherList) {
        this.voucherList = voucherList;
    }

    public User(List<String> voucherList) {
        this.voucherList = voucherList;
    }

    public User() {
        // Constructor rỗng được yêu cầu cho Firebase Realtime Database
    }

    public User(String birthday, String gender, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }
}

