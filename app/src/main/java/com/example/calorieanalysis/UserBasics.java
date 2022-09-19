package com.example.calorieanalysis;


/*
this class used to hold necessary attributes of every users


 */
public class UserBasics {
    String gender;
    double start_weight,current_weight,target_weight;
    int birth_day,birth_month,birth_year,daily_calorie_goal,feet_height,inch_height;
    int current_weight_day,current_weight_month,current_weight_year;

    public UserBasics() {

    }

    public UserBasics(String gender, double start_weight, double current_weight, double target_weight, int birth_day,
                      int birth_month, int birth_year, int daily_calorie_goal, int feet_height, int inch_height
                    ,int current_weight_day,int current_weight_month,int current_weight_year) {
        this.gender = gender;
        this.start_weight = start_weight;
        this.current_weight = current_weight;
        this.target_weight = target_weight;
        this.birth_day = birth_day;
        this.birth_month = birth_month;
        this.birth_year = birth_year;
        this.daily_calorie_goal = daily_calorie_goal;
        this.feet_height = feet_height;
        this.inch_height = inch_height;
        this.current_weight_day = current_weight_day;
        this.current_weight_month = current_weight_month;
        this.current_weight_year = current_weight_year;
    }

    public int getCurrent_weight_day() {
        return current_weight_day;
    }

    public void setCurrent_weight_day(int current_weight_day) {
        this.current_weight_day = current_weight_day;
    }

    public int getCurrent_weight_month() {
        return current_weight_month;
    }

    public void setCurrent_weight_month(int current_weight_month) {
        this.current_weight_month = current_weight_month;
    }

    public int getCurrent_weight_year() {
        return current_weight_year;
    }

    public void setCurrent_weight_year(int current_weight_year) {
        this.current_weight_year = current_weight_year;
    }



    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getStart_weight() {
        return start_weight;
    }

    public void setStart_weight(double start_weight) {
        this.start_weight = start_weight;
    }

    public double getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(double current_weight) {
        this.current_weight = current_weight;
    }

    public double getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(double target_weight) {
        this.target_weight = target_weight;
    }

    public int getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(int birth_day) {
        this.birth_day = birth_day;
    }

    public int getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(int birth_month) {
        this.birth_month = birth_month;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }

    public int getDaily_calorie_goal() {
        return daily_calorie_goal;
    }

    public void setDaily_calorie_goal(int daily_calorie_goal) {
        this.daily_calorie_goal = daily_calorie_goal;
    }

    public int getFeet_height() {
        return feet_height;
    }

    public void setFeet_height(int feet_height) {
        this.feet_height = feet_height;
    }

    public int getInch_height() {
        return inch_height;
    }

    public void setInch_height(int inch_height) {
        this.inch_height = inch_height;
    }
}
