package com.arghadeep.chatapp;

/**
 * Created by Arghadeep on 05-02-2018.
 */

public class Message {

    private String content, username, time;

    public Message(){}

    public Message(String content, String username, String time) {
        this.content = content;
        this.username = username;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
