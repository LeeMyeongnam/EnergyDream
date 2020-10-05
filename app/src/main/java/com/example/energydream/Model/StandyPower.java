package com.example.energydream.Model;


// 대기전력 객체
public class StandyPower {

    String id;
    boolean isExist;        // 대기전력 발생 여부
    boolean isCalc;         // 대기전력 계산 여부
    long start_time;        // 대기전력 차단 시작 시간 (전기 차단 시간)
    long end_time;          // 대기전력 차단 종료 시간 (다시 켠 시간)
    int unit_power;         // 단위시간 당 대기전력량
    int save_power;         // 절약한 대기전력량



    public StandyPower() {
        id = "-1";
        isExist = false;
        isCalc = false;
        start_time = end_time = 0;
        unit_power = save_power = 0;
    }

    public long calcSavePower(){
        // 절약한 전기량 계산
        return ((end_time - start_time)/60000) * unit_power;
    }


    // setter
    public void setId(String res) { this.id = res; }
    public void setStart_time() {
        this.start_time = System.currentTimeMillis();
    }
    public void setEnd_time() {
        this.end_time = System.currentTimeMillis();
    }
    public void setUnit_power(int unit_power) {
        this.unit_power = unit_power;
    }
    public void setExist(boolean isExist) {
        this.isExist = isExist;
    }
    public void setisCalc(boolean isCalc) {this.isCalc = isCalc; }
    // getter

    public boolean isCalc() { return isCalc; }
    public boolean isExist() {
        return isExist;
    }
    public long getStart_time() {
        return start_time;
    }
    public long getEnd_time() {
        return end_time;
    }
    public int getUnit_power() {
        return unit_power;
    }
    public int getSave_power() {
        return save_power;
    }
    public String getId() {
        return id;
    }

}
