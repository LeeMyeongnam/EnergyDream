package com.example.energydream.Model;


public class Business {

    private boolean isVenture;      // true(소셜벤처기부) false(에너지빈곤층기부)
    private String businessID;      // 사업 아이디
    private String businessName;    // 사업명
    private String companyName;     // 기업명
    private int businessGoal;       // 목표마일리지
    private String businessIMG;     // 사업이미지
    private String businessContents;// 사업 내용(설명글)
    private int mileage;            // 현재 모금마일리지
    private String goalDate;        // 사업마감일
    private int state;              // 사업승인여부 (0-확인안함 / 1-승인)


    public Business(){

    }

    public Business(boolean isVenture, String businessName, String compName, int businessGoal, String businessIMG, String businessContents, String goalDate) {
        this.isVenture = isVenture;
        this.businessID = String.valueOf((int)(Math.random()*1000+1));
        this.businessName = businessName;
        this.companyName = compName;
        this.businessGoal = businessGoal;
        this.businessIMG = businessIMG;
        this.businessContents = businessContents;
        this.mileage = 0;
        this.goalDate = goalDate;
        this.state = 0;
    }


    public void addMileage(int mileage){
        this.mileage += mileage;
    }

    // getter
    public String getBusinessID(){ return businessID;}
    public String getBusinessName(){ return businessName;}
    public String getCompanyName() { return companyName;}
    public int getBusinessGoal(){ return businessGoal;}
    public String getBusinessIMG(){ return businessIMG;}
    public String getBusinessContents(){ return businessContents;}
    public String getGoalDate(){ return goalDate;}
    public int getState(){ return state;}
    public boolean getIsVenture() {
        return isVenture;
    }
    public int getMileage(){ return mileage;}

    //setter
    public void setBusinessID(String businessID){ this.businessID = businessID;}
    public void setBusinessName(String businessName){ this.businessName = businessName;}
    public void setCompanyName(String compName) { this.companyName = compName;}
    public void setBusinessGoal(int businessGoal){ this.businessGoal = businessGoal;}
    public void setBusinessIMG(String businessIMG){ this.businessIMG= businessIMG;}
    public void setBusinessContents(String businessContents){ this.businessContents = businessContents;}
    public void setMileage(int mileage){ this.mileage = mileage;}
    public void setGoalDate(String goalDate){ this.goalDate = goalDate;}
    public void setState(int state){ this.state = state;}
    public void setIsVenture(boolean venture) {
        isVenture = venture;
    }

}