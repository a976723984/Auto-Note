package com.dali.autonote;

import java.util.Locale;

public class ChatGptMessage {
    private String role;

    private String content;

    public static ChatGptMessage of(ChatGptQueryDTO.Role role, String content) {
        ChatGptMessage message = new ChatGptMessage();
        message.role = role.name().toLowerCase(Locale.ROOT);
        message.content = content;
        return message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
