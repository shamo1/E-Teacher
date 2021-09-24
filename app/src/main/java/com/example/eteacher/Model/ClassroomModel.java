package com.example.eteacher.Model;

public class ClassroomModel {
    String classID, joinCode, subjectName, section, semester, department, classTime, classPushID, teacherID;

    public ClassroomModel() {
    }

    public ClassroomModel(String classID, String joinCode, String subjectName, String section,
                          String semester, String department, String classTime, String classPushID, String teacherID) {
        this.classID = classID;
        this.joinCode = joinCode;
        this.subjectName = subjectName;
        this.section = section;
        this.semester = semester;
        this.department = department;
        this.classTime = classTime;
        this.classPushID = classPushID;
        this.teacherID = teacherID;
    }


    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassPushID() {
        return classPushID;
    }

    public void setClassPushID(String classPushID) {
        this.classPushID = classPushID;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }
}
