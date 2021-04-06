package com.example.study;

public class Content {
    private String studyName;
    private String studyKind;
    private String head;
    private String headUid;
    private String maxMembers;
    private String currMembers;
    private String content;
    private String myChat;
    private String myNoti;

    public Content(){}

    public String getHeadUid() {
        return headUid;
    }

    public void setHeadUid(String headUid) {
        this.headUid = headUid;
    }

    public Content(String contentTitle, String studyKind, String head, String uid, String maxMembers, String currMembers, String content){
        this.studyName = contentTitle;
        this.studyKind = studyKind;
        this.head = head;
        this.headUid = uid;
        this.maxMembers = maxMembers;
        this.currMembers = currMembers;
        this.content = content;
    }

    public void setContentTitle(String contentTitle){
        this.studyName = contentTitle;
    }
    public String getContentTitle(){
        return this.studyName;
    }
    public void setStudyKind(String studyKind){
        this.studyKind = studyKind;
    }
    public String getStudyKind(){
        return this.studyKind;
    }
    public void setHead(String head){
        this.head = head;
    }
    public String getHead(){
        return this.head;
    }
    public void setMaxMembers(String maxMembers){
        this.maxMembers = maxMembers;
    }
    public String getMaxMembers(){
        return maxMembers;
    }
    public void setCurrMembers(String currMembers){
        this.currMembers = currMembers;
    }
    public String getCurrMembers(){
        return currMembers;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }
    public String getMyChat() { return myChat; }
    public String getMyNoti() { return myNoti; }
    public void setMyNoti(String myNoti) { this.myNoti = myNoti; }
    public void setMyChat(String myChat) { this.myChat = myChat; }
}

