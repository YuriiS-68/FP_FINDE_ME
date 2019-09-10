package com.findme.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.io.IOException;
import java.util.*;

@Entity(name = "User")
@Table(name = "USERS1")
public class User extends IdEntity{
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    private String country;
    private String city;
    private String age;
    private Date dateRegistered;
    private Date dateLastActive;
    private RelationshipType relationship;
    private ReligionType religion;
    private String school;
    private String university;

    private List<Message> messageSent;
    private List<Message> messageReceived;
    private List<Post> posts;
    private Set<Relationship> statuses = new HashSet<>();

    //private String[] interests;

    public User() {
    }

    @Id
    @SequenceGenerator(name = "USER_SQ", sequenceName = "USER_FM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SQ")
    @Column(name = "USER_ID")
    @Override
    public Long getId() {
        return id;
    }

    @Column(name = "USER_FIRST_NAME", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "USER_LAST_NAME", nullable = false)
    public String getLastName() {
        return lastName;
    }

    @Column(name = "PHONE", nullable = false)
    public String getPhone() {
        return phone;
    }

    @Column(name = "EMAIL", nullable = false)
    public String getEmail(){ return email; }

    @Column(name = "PASSWORD_USER")
    public String getPassword() {
        return password;
    }

    @Column(name = "COUNTRY", nullable = false)
    public String getCountry() {
        return country;
    }

    @Column(name = "CITY", nullable = false)
    public String getCity() {
        return city;
    }

    @Column(name = "AGE", nullable = false)
    public String getAge() {
        return age;
    }

    @Column(name = "DATE_REGISTERED", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date getDateRegistered() {
        return dateRegistered;
    }

    @Column(name = "DATE_LAST_ACTIVE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    public Date getDateLastActive() {
        return dateLastActive;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "RELATIONSHIP_TYPE", nullable = false)
    public RelationshipType getRelationship() {
        return relationship;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "RELIGION_TYPE", nullable = false)
    public ReligionType getReligion() {
        return religion;
    }

    @Column(name = "SCHOOL", nullable = false)
    public String getSchool() {
        return school;
    }

    @Column(name = "UNIVERSITY", nullable = false)
    public String getUniversity() {
        return university;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "userFrom", fetch = FetchType.LAZY, targetEntity = Message.class)
    public List<Message> getMessageSent() {
        return messageSent;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "userTo", fetch = FetchType.LAZY, targetEntity = Message.class)
    public List<Message> getMessageReceived() {
        return messageReceived;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "userPosted", fetch = FetchType.LAZY, targetEntity = Post.class)
    public List<Post> getPosts() {
        return posts;
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "USERS1_RELATIONSHIP", joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "RELATIONSHIP_ID"))
    public Set<Relationship> getStatuses() {
        return statuses;
    }

    public void addRelationship(Relationship relationship){
        statuses.add(relationship);
        relationship.getUsers().add(this);
    }

    public void removeRelationship(Relationship relationship){
        statuses.remove(relationship);
        relationship.getUsers().remove(this);
    }

    @JsonCreator
    public static User createFromJson(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;

        try {
            user = objectMapper.readValue(jsonString, User.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                firstName.equals(user.firstName) &&
                lastName.equals(user.lastName) &&
                phone.equals(user.phone) &&
                email.equals(user.email) &&
                password.equals(user.password) &&
                country.equals(user.country) &&
                city.equals(user.city) &&
                age.equals(user.age) &&
                dateRegistered.equals(user.dateRegistered) &&
                dateLastActive.equals(user.dateLastActive) &&
                relationship == user.relationship &&
                religion == user.religion &&
                school.equals(user.school) &&
                university.equals(user.university);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, phone, email, password, country, city, age, dateRegistered, dateLastActive,
                relationship, religion, school, university);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("phone='" + phone + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .add("country='" + country + "'")
                .add("city='" + city + "'")
                .add("age=" + age)
                .add("dateRegistered=" + dateRegistered)
                .add("dateLastActive=" + dateLastActive)
                .add("relationshipStatus=" + relationship)
                .add("religion=" + religion)
                .add("school='" + school + "'")
                .add("university='" + university + "'")
                .toString();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public void setDateLastActive(Date dateLastActive) {
        this.dateLastActive = dateLastActive;
    }

    public void setRelationship(RelationshipType relationship) {
        this.relationship = relationship;
    }

    public void setReligion(ReligionType religion) {
        this.religion = religion;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public void setMessageSent(List<Message> messageSent) {
        this.messageSent = messageSent;
    }

    public void setMessageReceived(List<Message> messageReceived) {
        this.messageReceived = messageReceived;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setStatuses(Set<Relationship> statuses) {
        this.statuses = statuses;
    }


}
