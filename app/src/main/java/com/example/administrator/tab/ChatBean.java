package com.example.administrator.tab;

/**
 * Created by Administrator on 2016/7/26.
 * 作用是：
 */
public class ChatBean {
    public boolean isAsker() {
        return isAsker;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAsker(boolean isAsker) {
        this.isAsker = isAsker;
    }

    public String text;
    public boolean isAsker;

    public ChatBean(String text, boolean isAsker) {
        this.text = text;
        this.isAsker = isAsker;
    }
}
