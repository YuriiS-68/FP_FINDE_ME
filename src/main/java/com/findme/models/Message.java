package com.findme.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "MESSAGE")
class Message extends IdEntity {
    private Long id;
    private String text;
    private Date dateSent;
    private Date dateRead;
    private User userFrom;
    private User userTo;

    public Message() {
    }

    @Id
    @SequenceGenerator(name = "MES_SQ", sequenceName = "MESSAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MES_SQ")
    @Column(name = "MESSAGE_ID")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "TEXT_MESSAGE")
    public String getText() {
        return text;
    }

    @Column(name = "DATE_SENT", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date getDateSent() {
        return dateSent;
    }

    @Column(name = "DATE_READ", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date getDateRead() {
        return dateRead;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_FROM")
    public User getUserFrom() {
        return userFrom;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_TO")
    public User getUserTo() {
        return userTo;
    }

    @JsonCreator
    public static Message createFromJson(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = null;

        try {
            message = objectMapper.readValue(jsonString, Message.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id) &&
                text.equals(message.text) &&
                dateSent.equals(message.dateSent) &&
                dateRead.equals(message.dateRead) &&
                userFrom.equals(message.userFrom) &&
                userTo.equals(message.userTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, dateSent, dateRead, userFrom, userTo);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("text='" + text + "'")
                .add("dateSent=" + dateSent)
                .add("dateRead=" + dateRead)
                .add("userFrom=" + userFrom)
                .add("userTo=" + userTo)
                .toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public void setDateRead(Date dateRead) {
        this.dateRead = dateRead;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }
}
