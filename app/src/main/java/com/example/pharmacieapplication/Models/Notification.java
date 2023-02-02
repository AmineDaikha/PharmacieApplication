package com.example.pharmacieapplication.Models;

public class Notification {

    private int id;
    private int nbOffre;
    private int nbMsg;

    public Notification(){

    }
    public Notification(int id, int nbOffre, int nbMsg) {
        this.id = id;
        this.nbOffre = nbOffre;
        this.nbMsg = nbMsg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNbOffre() {
        return nbOffre;
    }

    public void setNbOffre(int nbOffre) {
        this.nbOffre = nbOffre;
    }

    public int getNbMsg() {
        return nbMsg;
    }

    public void setNbMsg(int nbMsg) {
        this.nbMsg = nbMsg;
    }
}
