package com.example.energydream.Model;

import java.util.ArrayList;

public class Member {

    private String email;
    private String pw;
    private String name;
    private String region;

    private int total_mileage;  // 전체 마일리지 (현재 마일리지 + 기부 마일리지)
    private int mileage;        // 현재 마일리지
    private long power;         // 절약전기량
    private int money;          // 절약 금액
    private ArrayList<Donation_user> donationList;

    public static class Donation_user {
        String company_name;
        String business_name;
        int dona_mileage;
        boolean is_venture;

        public Donation_user(){}
        public Donation_user(String comp_name, String busi_name, int mile, boolean isVenture){
            this.company_name = comp_name;
            this.business_name = busi_name;
            this.dona_mileage = mile;
            this.is_venture = isVenture;
        }

        public void add_mileage(int mileage) {this.dona_mileage += mileage;}

        public String get_business_name(){return business_name;}
        public String get_company_name() {return company_name;}
        public int get_mileage() {return dona_mileage;}
        public boolean getIs_venture() { return is_venture; }
    }

    public Member() {
        if(donationList == null)
            donationList = new ArrayList<>();
    }

    public Member(String email, String pw, String name, String region) {
        this.email = email;
        this.pw = pw;
        this.name = name;
        this.region = region;

        if(donationList == null)
            donationList = new ArrayList<>();
    }

    public void savePower(long elec) {
        this.power += elec;
    }

    public void addMoney(int money){
        this.money += money;
    }

    public void addMileage(int mileage){
        this.mileage += mileage;
        this.total_mileage += mileage;
    }

    // 기부내역 추가
    public void donate(String comp_name, String busi_name, int mileage, boolean isVenture){

        if(donationList == null)
            donationList = new ArrayList<>();

        donationList.add(new Donation_user(comp_name, busi_name, mileage, isVenture));
        this.mileage -= mileage;

    }



    // Getter
    public String getEmail() {
        return email;
    }
    public String getPw() {
        return pw;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public int getTotal_mileage() {
        return total_mileage;
    }
    public int getMileage() {
        return mileage;
    }
    public long getPower() {
        return power;
    }
    public int getMoney() {
        return money;
    }
    public ArrayList<Donation_user> getDonationList() {
        return donationList;
    }


}
