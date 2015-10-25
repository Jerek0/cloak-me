package com.jeremy_minie.helloagaincrm.logged.entities;

/**
 * Created by jerek0 on 24/10/15.
 */
public class Message {

    private String author_uid;
    private String content;
    private int color;
    private String alignment;

    public Message() {

    }

    public Message(String author_uid, String content, int color) {
        this.author_uid = author_uid;
        this.content = content;
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public int getColor() {
        return color;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }
}
