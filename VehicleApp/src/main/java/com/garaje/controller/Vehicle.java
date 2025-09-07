package com.garaje.controller;


public class Vehicle { 
    private int id; 
    private String brand; 
    private String model; 
    private int year; 
    private String owner; 
 
    public Vehicle(int id, String brand, String model, int year, String owner) { 
        this.id = id; 
        this.brand = brand; 
        this.model = model; 
        this.year = year; 
        this.owner = owner; 
    } 
 
    // Getters y Setters 
    public int getId() { return id; } 
    public void setId(int id) { this.id = id; } 
 
    public String getBrand() { return brand; } 
    public void setBrand(String brand) { this.brand = brand; } 
 
    public String getModel() { return model; } 
    public void setModel(String model) { this.model = model; } 
 
    public int getYear() { return year; } 
    public void setYear(int year) { this.year = year; } 
 
    public String getOwner() { return owner; } 
    public void setOwner(String owner) { this.owner = owner; } 
} 
