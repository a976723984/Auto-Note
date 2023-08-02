package com.dali.autonote;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ChatGptQueryDTO {
    public enum Role {
        SYSTEM, USER;
    }

    private String model;

    private List<ChatGptMessage> messages;

    public static ChatGptQueryDTO of(String model, List<ChatGptMessage> messages) {
        ChatGptQueryDTO query = new ChatGptQueryDTO();
        query.model = model;
        query.messages = messages;
        return query;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ChatGptMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatGptMessage> messages) {
        this.messages = messages;
    }
}
