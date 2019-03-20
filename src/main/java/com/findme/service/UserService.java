package com.findme.service;

import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void update(User user)throws BadRequestException{
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

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
