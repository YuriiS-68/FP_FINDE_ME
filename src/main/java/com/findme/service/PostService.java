package com.findme.service;

import com.findme.dao.PostDAO;
import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class PostService {

    private PostDAO postDAO;
    private RelationshipDAO relationshipDAO;

    @Autowired
    public PostService(PostDAO postDAO, RelationshipDAO relationshipDAO){
        this.postDAO = postDAO;
        this.relationshipDAO = relationshipDAO;
    }

    public void createPost(Post post)throws BadRequestException, InternalServerError{
        if (!post.getUserPosted().getId().equals(post.getUserPagePosted().getId())){
            checkStatusBetweenUsers(post.getUserPosted().getId(), post.getUserPagePosted().getId());
        }
        validateMessage(post.getMessage());
        Date datePosted = new Date();
        post.setDatePosted(datePosted);

        postDAO.save(post);
    }

    //TODO нет смысла создавать такие короткие по логику методы

    //1. Найти в посте имя или фамилию юзера или все вместе
    //2. Сделать запрос в базу и получить юзера по имени и фамилии
    //Пока не пойму как это делать...
    /*private User findUsersTagged(String message){
        String[] subString = message.split(" ");
        User userTagged = null;

        for (String element : subString){
            if (element != null){

            }
        }

        return userTagged;
    }*/

    private void checkStatusBetweenUsers(Long idUserPosted, Long idUserPagePosted) throws BadRequestException, InternalServerError {
        if (!relationshipDAO.getStatusBetweenUsers(idUserPosted, idUserPagePosted).equalsIgnoreCase("ACCEPTED")){
            throw new BadRequestException("User with ID " + idUserPosted + " and User with ID " + idUserPagePosted + " are not friends.");
        }
    }

    private void validateMessage(String message)throws BadRequestException{
        //TODO что это за странный сплит по / ?
        String[] str = message.split(" ");

        System.out.println(Arrays.toString(str));

        for (String element : str){
            if (element != null && (element.contains("http://") || element.contains("https://"))){
                throw new BadRequestException("Message contains not valid link.");
            }
        }
    }
}
