package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StrengthRecord {
    private int id;
    private String exercise;
    private int oneRepMax;
    private int enteredWeight;
    private int enteredReps;
    private String date;
    private boolean isLocalOnly;

    public StrengthRecord(String exercise, int oneRepMax, int enteredWeight, int enteredReps) {
        this.exercise = exercise;
        this.oneRepMax = oneRepMax;
        this.enteredWeight = enteredWeight;
        this.enteredReps = enteredReps;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        this.isLocalOnly = true;
    }

    public StrengthRecord(String exercise, int oneRepMax) {
        this.exercise = exercise;
        this.oneRepMax = oneRepMax;
        this.enteredWeight = 0;
        this.enteredReps = 0;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        this.isLocalOnly = true;
    }

    public StrengthRecord(int id, String exercise, int oneRepMax, int enteredWeight, int enteredReps, String date) {
        this.id = id;
        this.exercise = exercise;
        this.oneRepMax = oneRepMax;
        this.enteredWeight = enteredWeight;
        this.enteredReps = enteredReps;
        this.date = date;
        this.isLocalOnly = false;
    }

    public StrengthRecord(int id, String exercise, int oneRepMax, String date) {
        this.id = id;
        this.exercise = exercise;
        this.oneRepMax = oneRepMax;
        this.enteredWeight = 0;
        this.enteredReps = 0;
        this.date = date;
        this.isLocalOnly = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public int getOneRepMax() {
        return oneRepMax;
    }

    public void setOneRepMax(int oneRepMax) {
        this.oneRepMax = oneRepMax;
    }

    public int getEnteredWeight() {
        return enteredWeight;
    }

    public void setEnteredWeight(int enteredWeight) {
        this.enteredWeight = enteredWeight;
    }

    public int getEnteredReps() {
        return enteredReps;
    }

    public void setEnteredReps(int enteredReps) {
        this.enteredReps = enteredReps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isLocalOnly() {
        return isLocalOnly;
    }

    public void setLocalOnly(boolean localOnly) {
        isLocalOnly = localOnly;
    }
}