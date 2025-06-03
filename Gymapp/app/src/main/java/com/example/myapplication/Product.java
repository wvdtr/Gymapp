package com.example.myapplication;

public class Product
{
    private int id;
    private String name;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    
    public Product(int id, String name, double calories, double protein, double fat, double carbs)
    {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
    
    public Product(String name, double calories, double protein, double fat, double carbs)
    {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getCalories() {
        return calories;
    }
    
    public double getProtein() {
        return protein;
    }
    
    public double getFat() {
        return fat;
    }
    
    public double getCarbs() {
        return carbs;
    }
}
