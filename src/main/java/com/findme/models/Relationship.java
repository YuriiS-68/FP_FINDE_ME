package com.findme.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

@Entity(name = "Relationship")
@Table(name = "RELATIONSHIP")
public class Relationship extends IdEntity{
    private Long id;
    private User userFrom;
    private User userTo;
    private RelationshipStatusType statusType;
    private Set<User> users = new HashSet<>();

    public Relationship() {
    }

    @Id
    @SequenceGenerator(name = "R_SHIP_SQ", sequenceName = "RELATIONSHIP_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "R_SHIP_SQ")
    @Column(name = "RELATIONSHIP_ID")
    @Override
    public Long getId() {
        return id;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_TYPE")
    public RelationshipStatusType getStatusType() {
        return statusType;
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "statuses")
    Set<User> getUsers() {
        return users;
    }

    @JsonCreator
    public static Relationship createFromJson(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        Relationship relationship = null;

        try {
            relationship = objectMapper.readValue(jsonString, Relationship.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        return id.equals(that.id) &&
                userFrom.equals(that.userFrom) &&
                userTo.equals(that.userTo) &&
                statusType == that.statusType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userFrom, userTo, statusType);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Relationship.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("userFrom=" + userFrom.getId())
                .add("userTo=" + userTo.getId())
                .add("statusType=" + statusType)
                .toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public void setStatusType(RelationshipStatusType statusType) {
        this.statusType = statusType;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
