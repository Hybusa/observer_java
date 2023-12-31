package pro.sky.observer_java.model;

import java.time.LocalDateTime;

public class Message {
    private Long messageId;
    private String sender;
    private LocalDateTime sendTime;
    private String messageText;
    private static long messageCounter = 0;

    public Message(String sender, LocalDateTime sendTime, String messageText) {
        this.messageId = messageCounter++;
        this.sender = sender;
        this.sendTime = sendTime;
        this.messageText = messageText;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
