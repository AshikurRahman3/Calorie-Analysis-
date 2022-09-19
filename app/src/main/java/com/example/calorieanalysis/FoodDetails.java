package com.example.calorieanalysis;

/*
this java objects holds all necessary attributes for food items
 */
public class FoodDetails {

    private int id;
    private String name;

    public String getDatabase_key() {
        return database_key;
    }

    public void setDatabase_key(String database_key) {
        this.database_key = database_key;
    }

    private String database_key;

    public int getFood_type() {
        return food_type;
    }

    public void setFood_type(int food_type) {
        this.food_type = food_type;
    }

    private int calories;
    private String unit;
    private int food_type;

    public FoodDetails() {

    }

    public FoodDetails(String name, int calories, String unit, int food_type) {
        this.name = name;
        this.calories = calories;
        this.unit = unit;
        this.food_type = food_type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public String getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
    }
}
