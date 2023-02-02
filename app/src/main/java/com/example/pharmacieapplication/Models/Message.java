package com.example.pharmacieapplication.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

    private int id;
    private String senderId;
    private String senderEmail;
    private String subject;
    private String content;
    private int nbReply;
    private String date;
    private boolean byMe = false;
    private boolean isRead = false;
    private ArrayList<Replay> replays = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNbReply() {
        return nbReply;
    }

    public void setNbReply(int nbReply) {
        this.nbReply = nbReply;
    }

    public ArrayList<Replay> getReplays() {
        return replays;
    }

    public void setReplays(ArrayList<Replay> replays) {
        this.replays = replays;
    }

    public boolean isByMe() {
        return byMe;
    }

    public void setByMe(boolean byMe) {
        this.byMe = byMe;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
