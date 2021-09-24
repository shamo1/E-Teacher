package com.example.eteacher.Model;

public class JoinedByModel {

    String classID, date, joinID, userID;

    public JoinedByModel() {
    }

    public JoinedByModel(String classID, String date, String joinID, String userID) {
        this.classID = classID;
        this.date = date;
        this.joinID = joinID;
        this.userID = userID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJoinID() {
        return joinID;
    }

    public void setJoinID(String joinID) {
        this.joinID = joinID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
