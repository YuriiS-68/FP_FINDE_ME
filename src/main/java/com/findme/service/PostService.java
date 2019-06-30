package com.findme.service;

import com.findme.dao.PostDAO;
import com.findme.dao.RelationshipDAO;
import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@Service
public class PostService {

    private PostDAO postDAO;
    private UserDAO userDAO;
    private RelationshipDAO relationshipDAO;

    @Autowired
    public PostService(PostDAO postDAO, UserDAO userDAO, RelationshipDAO relationshipDAO){
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.relationshipDAO = relationshipDAO;
    }

    public void createPost(HttpSession session, String idUserPosted, String idUserPagePosted, String message, String location)throws BadRequestException, InternalServerError{
        User userPosted = (User) session.getAttribute(idUserPosted);
        if (userPosted == null){
            throw new BadRequestException("User with ID " + idUserPosted + " is not logged in.");
        }

        User userPagePosted = checkUserPagePosted(idUserPagePosted);

        validateMessage(message);

        Post post = new Post();
        post.setMessage(message);
        post.setLocation(location);
        Date datePosted = new Date();
        post.setDatePosted(datePosted);
        post.setUserPosted(userPosted);
        if (!idUserPosted.equals(idUserPagePosted)){
            checkStatusBetweenUsers(idUserPosted, idUserPagePosted);
            post.setUserPagePosted(userPagePosted);
        }
        else {
            post.setUserPagePosted(userPosted);
        }
    }

    public void validationInputData(HttpSession session, String idUserPosted, String idUserPagePosted, String message, String location)throws BadRequestException{
        if (idUserPosted == null || idUserPagePosted == null){
            throw  new BadRequestException("UserPosted or UserPagePosted does not exist.");
        }

        if (session == null){
            throw  new BadRequestException("Session is not exist.");
        }

        if (message == null){
            throw  new BadRequestException("Message is not exist.");
        }

        if (location == null){
            throw  new BadRequestException("Location is not exist.");
        }
    }

    public Post save(Post post)throws BadRequestException{
        if (post != null && post.getId() != null){
            throw new BadRequestException("This Post with ID - " + post.getId() + " can not save in DB.");
        }
        else {
            postDAO.save(post);
        }
        return post;
    }

    public void update(Post post)throws BadRequestException, InternalServerError{
        if (post == null){
            throw new BadRequestException("Post is not exist");
        }
        postDAO.update(post);
    }

    public void delete(Long id)throws BadRequestException, InternalServerError {
        if (id == null){
            throw new BadRequestException("The ID entered does not exist");
        }
        postDAO.delete(id);
    }

    //1. Найти в посте имя или фамилию юзера или все вместе
    //2. Сделать запрос в базу и получить юзера по имени и фамилии
    //Пока не пойму как это делать...
    private User findUsersTagged(String message){
        String[] subString = message.split(" ");
        User userTagged = null;

        for (String element : subString){
            if (element != null){

            }
        }

        return userTagged;
    }

    private User checkUserPagePosted(String idUserPagePosted)throws BadRequestException, InternalServerError{
        User userPagePosted = userDAO.findById(Long.parseLong(idUserPagePosted));
        if (userPagePosted == null){
            throw new BadRequestException("User with ID " + idUserPagePosted + " is not found in DB.");
        }
        return userPagePosted;
    }

    private void checkStatusBetweenUsers(String idUserPosted, String idUserPagePosted) throws BadRequestException, InternalServerError {
        if (!relationshipDAO.getStatusBetweenUsers(Long.parseLong(idUserPosted), Long.parseLong(idUserPagePosted)).equalsIgnoreCase("ACCEPTED")){
            throw new BadRequestException("User with ID " + idUserPosted + " and User with ID " + idUserPagePosted + " are not friends.");
        }
    }

    private void validateMessage(String message)throws BadRequestException{
        String[] str = message.split("/");

        System.out.println(Arrays.toString(str));

        for (String element : str){
            if (element != null && (element.equalsIgnoreCase("http:") || element.equalsIgnoreCase("https:"))){
                throw new BadRequestException("Message contains not valid link.");
            }
        }
    }
}
