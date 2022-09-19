package com.example.calorieanalysis;

/*
this class holds necessary attributes to show data in weight history fragment


 */
public class WeightDetails {

    int id,day,month,year;
    double weight,weight_difference;
    String database_key;

    public WeightDetails() {

    }

    public String getDatabase_key() {
        return database_key;
    }

    public void setDatabase_key(String database_key) {
        this.database_key = database_key;
    }

    public WeightDetails(int day, int month, int year, double weight) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.weight = weight;
    }

    public double getWeight_difference() {
        return weight_difference;
    }

    public void setWeight_difference(double weight_difference) {
        this.weight_difference = weight_difference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
