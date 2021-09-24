package com.example.eteacher.Model;

public class ClassworkModel {
    String title, tpoic, desc, pdfURl, dueDate, marks, classID, pushID, type, userID;

    public ClassworkModel() {
    }

    public ClassworkModel(String title, String tpoic, String desc, String pdfURl, String dueDate, String marks, String classID, String pushID, String type, String userID) {
        this.title = title;
        this.tpoic = tpoic;
        this.desc = desc;
        this.pdfURl = pdfURl;
        this.dueDate = dueDate;
        this.marks = marks;
        this.classID = classID;
        this.pushID = pushID;
        this.type = type;
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTpoic() {
        return tpoic;
    }

    public void setTpoic(String tpoic) {
        this.tpoic = tpoic;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPdfURl() {
        return pdfURl;
    }

    public void setPdfURl(String pdfURl) {
        this.pdfURl = pdfURl;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getPushID() {
        return pushID;
    }

    public void setPushID(String pushID) {
        this.pushID = pushID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
