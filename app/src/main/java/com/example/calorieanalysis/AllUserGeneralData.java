package com.example.calorieanalysis;

/*
it is a java class object that holds users general informations, which is need to show all users general information in app.
 */

public class AllUserGeneralData {
    String name, user_id;
    double current_weight,start_weight,lost_weight;

    public AllUserGeneralData(String name, String user_id, double current_weight, double start_weight, double lost_weight) {
        this.name = name;
        this.user_id = user_id;
        this.current_weight = current_weight;
        this.start_weight = start_weight;
        this.lost_weight = lost_weight;
    }

    public AllUserGeneralData() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public double getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(double current_weight) {
        this.current_weight = current_weight;
    }

    public double getStart_weight() {
        return start_weight;
    }

    public void setStart_weight(double start_weight) {
        this.start_weight = start_weight;
    }

    public double getLost_weight() {
        return lost_weight;
    }

    public void setLost_weight(double lost_weight) {
        this.lost_weight = lost_weight;
    }
}
