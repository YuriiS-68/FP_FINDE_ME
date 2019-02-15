package com.findme.controller;

import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.User;
import com.findme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Controller
public class UserController extends Utils<User> {

    private UserService userService;
    private UserDAO userDAO;

    @Autowired
    public UserController(UserService userService, UserDAO userDAO) {
        this.userService = userService;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.GET)
    public String profile(Model model, @PathVariable String userId) {
        try {
            User user = userDAO.findById(Long.parseLong(userId));
            if (user == null){
                model.addAttribute(userId);
                return "errors/exception_npe";
            }
            model.addAttribute("user", user);
            return "profile";
        }catch (NumberFormatException e){
            model.addAttribute(userId);
            return "errors/exception_number_format";
        }catch (InternalServerError e){
            return "errors/exception_internal_server";
        }
    }

    @RequestMapping(path = "/register-user", method = RequestMethod.POST)
    public ResponseEntity<String> registerUser(@ModelAttribute User user)throws BadRequestException{
        if (user != null){
            if (userDAO.findUserByFields(user)){
                Date dateRegister = new Date();
                user.setDateRegistered(dateRegister);
                user.setDateLastActive(dateRegister);
                userService.save(user);
                return new ResponseEntity<>("User registered success!", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("This user can not registered.", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping (method = RequestMethod.POST, value = "/saveUser", produces = "text/plain")
    public @ResponseBody
    String save(HttpServletRequest req) throws IOException, BadRequestException {
        User user = mappingObject(req);

        try {
            userService.save(user);

        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return "User saved success.";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updateUser", produces = "text/plain")
    public @ResponseBody
    String update(HttpServletRequest req)throws IOException, BadRequestException, InternalServerError{
        User user = mappingObject(req);
        long inputId = Long.parseLong(req.getParameter("userId"));

        try{
            if (userDAO.findById(inputId) == null){
                return "User with ID - " + inputId + " does not exist in the DB";
            }
            else {
                userService.update(user);
            }
        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw  e;
        }
        return "User update success";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteUser", produces = "text/plain")
    public @ResponseBody
    String delete(HttpServletRequest req)throws BadRequestException, InternalServerError{
        User user = userDAO.findById(Long.parseLong(req.getParameter("userId")));
        long userId = Long.parseLong(req.getParameter("userId"));

        try {
            if (user == null){
                return "The User with ID " + userId + " does not exist in the DB.";
            }
            else {
                userService.delete(user.getId());
            }
        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return "User deleted success";
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}