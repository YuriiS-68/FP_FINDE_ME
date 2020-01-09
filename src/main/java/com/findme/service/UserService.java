package com.findme.service;

import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.RelationshipType;
import com.findme.models.ReligionType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public User save(User user)throws BadRequestException{
        if (user != null && user.getId() != null){
            throw new BadRequestException("This User with ID - " + user.getId() + " can not save in DB.");
        }
        else {
            userDAO.save(user);
        }
        return user;
    }

    public boolean setDateRegisterUser(User user)throws BadRequestException {
        if (validateEnums(user)){
            Date dateRegister = new Date();
            user.setDateRegistered(dateRegister);
            user.setDateLastActive(dateRegister);
            try {
                save(user);
            } catch (BadRequestException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            throw new BadRequestException("Relationship or religion data entered incorrectly.");
        }
    }

    private boolean validateEnums(User user){
        return user.getReligion().equals(ReligionType.Christian) || user.getReligion().equals(ReligionType.Muslim) ||
                user.getReligion().equals(ReligionType.Catholic) || user.getReligion().equals(ReligionType.Buddhist) &&
                user.getRelationship().equals(RelationshipType.married) || user.getRelationship().equals(RelationshipType.single);
    }

    public void update(User user)throws BadRequestException, InternalServerError{
        if (user == null){
            throw new BadRequestException("User is not exist");
        }
        userDAO.update(user);
    }

    public void delete(Long id)throws BadRequestException, InternalServerError {
        if (id == null){
            throw new BadRequestException("The ID entered does not exist");
        }
        userDAO.delete(id);
    }

    public List<User> getIncomeRequests(String userId)throws BadRequestException, InternalServerError{
        if (userId == null){
            throw new BadRequestException("Input data is wrong.");
        }

        String word = "income";
        return findListUsers(userId, word);
    }

    public List<User> getOutcomeRequests(String userId)throws BadRequestException, InternalServerError{
        if (userId == null){
            throw new BadRequestException("Input data is wrong.");
        }

        String word = "outcome";
        return findListUsers(userId, word);
    }

    private List<User> findListUsers(String userId, String word) throws BadRequestException, InternalServerError {
        if (userId == null || word == null){
            throw new BadRequestException("Input data is wrong.");
        }

        List<User> users;
        try {
            users = userDAO.getUsers(Long.parseLong(userId), word);
        } catch (InternalServerError e) {
            System.err.println(e.getMessage());
            throw e;
        }
        return users;
    }
}
