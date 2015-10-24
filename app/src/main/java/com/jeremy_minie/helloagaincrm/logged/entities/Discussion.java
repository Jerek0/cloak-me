package com.jeremy_minie.helloagaincrm.logged.entities;

/**
 * Created by jerek0 on 24/10/15.
 */
public class Discussion {

    private User target;
    private String lastMessage;

    public Discussion(User target, String lastMessage) {
        this.target = target;
        this.lastMessage = lastMessage;
    }

    public User getTarget() {
        return target;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
