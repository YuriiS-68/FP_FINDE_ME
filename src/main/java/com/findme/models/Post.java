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
@Table(name = "POST")
public class Post extends IdEntity{
    private Long id;
    private String message;
    private Date datePosted;
    private User userPosted;
    //TODO
    //levels permissions

    //TODO
    //comments

    public Post() {
    }

    @Id
    @SequenceGenerator(name = "POS_SQ", sequenceName = "POST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POS_SQ")
    @Column(name = "POST_ID")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "MESSAGE", nullable = false)
    public String getMessage() {
        return message;
    }

    @Column(name = "DATE_POSTED", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date getDatePosted() {
        return datePosted;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_POSTED")
    public User getUserPosted() {
        return userPosted;
    }

    @JsonCreator
    public static Post createFromJson(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        Post post = null;

        try {
            post = objectMapper.readValue(jsonString, Post.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id.equals(post.id) &&
                message.equals(post.message) &&
                datePosted.equals(post.datePosted) &&
                userPosted.equals(post.userPosted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, datePosted);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("message='" + message + "'")
                .add("datePosted=" + datePosted)
                .add("userPosted=" + userPosted)
                .toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setUserPosted(User userPosted) {
        this.userPosted = userPosted;
    }
}
