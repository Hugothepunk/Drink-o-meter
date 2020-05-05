package com.sourcey.Drinkometer;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

class Persondata implements Serializable {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private int weight;
    private ArrayList<OwnPair<Date, Double>> alcohol;
    private double remainingTime;
    private char sex;


    public Persondata() {
            id = 0;
            username = "";
            firstName = "";
            lastName = "";
            email = "";
            weight = 0;
            alcohol = new ArrayList<>();
            remainingTime = 0.0;
            sex = 'M';
            }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ArrayList<OwnPair<Date, Double>> getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(ArrayList<OwnPair<Date, Double>> alcohol) {
        this.alcohol = alcohol;
    }

    public void addAlcohol(OwnPair<Date, Double> pair) {  this.alcohol.add(pair);   }

    public double getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(double remainingTime) {
        this.remainingTime = remainingTime;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
