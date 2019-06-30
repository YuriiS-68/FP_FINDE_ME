package com.findme.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "POST")
public class Post extends IdEntity{
    private Long id;
    private String message;
    private String location;
    private Date datePosted;
    private User userPosted;
    private User userPagePosted;
    private List<User> usersTagged;
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

    @Column(name = "LOCATIONS", nullable = false)
    public String getLocation() {
        return location;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_PAGE_POSTED")
    public User getUserPagePosted() {
        return userPagePosted;
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "POST_USERS1", joinColumns = @JoinColumn(name = "POST_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    public List<User> getUsersTagged() {
        return usersTagged;
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
                location.equals(post.location) &&
                datePosted.equals(post.datePosted) &&
                userPosted.equals(post.userPosted) &&
                userPagePosted.equals(post.userPagePosted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, location, datePosted, userPosted, userPagePosted);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("message='" + message + "'")
                .add("location='" + location + "'")
                .add("datePosted=" + datePosted)
                .add("userPosted=" + userPosted)
                .add("userPagePosted=" + userPagePosted)
                .toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setUserPosted(User userPosted) {
        this.userPosted = userPosted;
    }

    public void setUserPagePosted(User userPagePosted) {
        this.userPagePosted = userPagePosted;
    }

    public void setUsersTagged(List<User> usersTagged) {
        this.usersTagged = usersTagged;
    }
}
