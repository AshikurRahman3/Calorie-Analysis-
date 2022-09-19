package com.example.calorieanalysis;

/*
to show consumed foods in diary, this class holds all necessary attributes
 */

public class DiaryFoodDetails {

    int id,meal_type;
    String food_name;
    String food_unit_name;
    String database_key;
    int food_per_unit_calorie,selected_meal;
    double food_quantity;
    int day,month,year;
    int food_totoal_calories,total_calorie,breakfast_total_calorie,lunch_total_calorie,dinner_total_calorie,snacks_total_calorie;


    public DiaryFoodDetails() {

    }
    public int getMeal_type() {
        return meal_type;
    }

    public void setMeal_type(int meal_type) {
        this.meal_type = meal_type;
    }

    public String getDatabase_key() {
        return database_key;
    }

    public void setDatabase_key(String database_key) {
        this.database_key = database_key;
    }

    public int getFood_totoal_calories() {
        return food_totoal_calories;
    }

    public void setFood_totoal_calories(int food_totoal_calories) {
        this.food_totoal_calories = food_totoal_calories;
    }

    public DiaryFoodDetails(String food_name, String food_unit_name, double food_quantity, int food_per_unit_calorie, int selected_meal, int day, int month, int year) {
        this.food_name = food_name;
        this.food_unit_name = food_unit_name;
        this.food_quantity = food_quantity;
        this.food_per_unit_calorie = food_per_unit_calorie;
        this.selected_meal = selected_meal;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public DiaryFoodDetails(int meal_type, String food_name, String food_unit_name, String database_key, int food_per_unit_calorie, double food_quantity, int day, int month, int year, int food_totoal_calories) {
        this.meal_type = meal_type;
        this.food_name = food_name;
        this.food_unit_name = food_unit_name;
        this.database_key = database_key;
        this.food_per_unit_calorie = food_per_unit_calorie;
        this.food_quantity = food_quantity;
        this.day = day;
        this.month = month;
        this.year = year;
        this.food_totoal_calories = food_totoal_calories;
    }

    public DiaryFoodDetails(String food_name, double food_quantity, int food_per_unit_calorie) {
        this.food_name = food_name;
        this.food_quantity = food_quantity;
        this.food_per_unit_calorie = food_per_unit_calorie;
    }

    public DiaryFoodDetails(String food_name, String food_unit_name, int food_per_unit_calorie, double food_quantity) {
        this.food_name = food_name;
        this.food_unit_name = food_unit_name;
        this.food_per_unit_calorie = food_per_unit_calorie;
        this.food_quantity = food_quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_unit_name() {
        return food_unit_name;
    }

    public void setFood_unit_name(String food_unit_name) {
        this.food_unit_name = food_unit_name;
    }

    public double getFood_quantity() {
        return food_quantity;
    }


    public void setFood_quantity(double food_quantity) {
        this.food_quantity = food_quantity;
    }

    public int getFood_per_unit_calorie() {
        return food_per_unit_calorie;
    }

    public void setFood_per_unit_calorie(int food_per_unit_calorie) {
        this.food_per_unit_calorie = food_per_unit_calorie;
    }

    public int getSelected_meal() {
        return selected_meal;
    }

    public void setSelected_meal(int selected_meal) {
        this.selected_meal = selected_meal;
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

    public int getTotal_calorie() {
        return total_calorie;
    }

    public void setTotal_calorie(int total_calorie) {
        this.total_calorie = total_calorie;
    }

    public int getBreakfast_total_calorie() {
        return breakfast_total_calorie;
    }

    public void setBreakfast_total_calorie(int breakfast_total_calorie) {
        this.breakfast_total_calorie = breakfast_total_calorie;
    }

    public int getLunch_total_calorie() {
        return lunch_total_calorie;
    }

    public void setLunch_total_calorie(int lunch_total_calorie) {
        this.lunch_total_calorie = lunch_total_calorie;
    }

    public int getDinner_total_calorie() {
        return dinner_total_calorie;
    }

    public void setDinner_total_calorie(int dinner_total_calorie) {
        this.dinner_total_calorie = dinner_total_calorie;
    }

    public int getSnacks_total_calorie() {
        return snacks_total_calorie;
    }

    public void setSnacks_total_calorie(int snacks_total_calorie) {
        this.snacks_total_calorie = snacks_total_calorie;
    }
}
