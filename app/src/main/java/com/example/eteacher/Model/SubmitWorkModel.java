package com.example.eteacher.Model;

public class SubmitWorkModel {
    String userID, classID, workID, submittedTime, submittedDate, fileURI;

    public SubmitWorkModel(String userID, String classID, String workID, String submittedTime, String submittedDate, String fileURI) {
        this.userID = userID;
        this.classID = classID;
        this.workID = workID;
        this.submittedTime = submittedTime;
        this.submittedDate = submittedDate;
        this.fileURI = fileURI;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getWorkID() {
        return workID;
    }

    public void setWorkID(String workID) {
        this.workID = workID;
    }

    public String getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        this.submittedTime = submittedTime;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getFileURI() {
        return fileURI;
    }

    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }
}
