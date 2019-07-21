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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
            if (user == null) {
                model.addAttribute(userId);
                return "errors/exception_npe";
            }
            model.addAttribute("user", user);
            return "profile";
        } catch (NumberFormatException e) {
            model.addAttribute(userId);
            return "errors/exception_number_format";
        } catch (InternalServerError e) {
            return "errors/exception_internal_server";
        }
    }

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String mainePage() {
        return "index";
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String registerPage() {
        return "register";
    }

    @RequestMapping(path = "/user-page", method = RequestMethod.GET)
    public String userPage() {
        return "user-page";
    }

    @RequestMapping(path = "/register-user", method = RequestMethod.POST)
    public ResponseEntity<String> registerUser(@ModelAttribute User user) {
        if (user == null) {
            return new ResponseEntity<>("Input is not correct.", HttpStatus.BAD_REQUEST);
        }

        System.out.println("Input user for registration: - " + user);

        try {
            if (userDAO.findUserByFields(user)) {
                Date dateRegister = new Date();
                user.setDateRegistered(dateRegister);
                user.setDateLastActive(dateRegister);
                userService.save(user);
                return new ResponseEntity<>("User registered success!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User with such email or phone number is already registered.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("This user can not registered.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/login-in", method = RequestMethod.GET)
    public String loginPage() {
        return "login-in";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logoutPage() {
        return "logout";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public ResponseEntity<String> loginUser(HttpSession session, @RequestParam(value = "email") String login,
                                            @RequestParam(value = "password") String pass) {
        if (login == null || pass == null) {
            return new ResponseEntity<>("Input is not correct.", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = userDAO.findUserByEmail(login);
            if (user == null) {
                return new ResponseEntity<>("Login or password is not correct.", HttpStatus.BAD_REQUEST);
            }

            User userInSession = (User) session.getAttribute(String.valueOf(user.getId()));

            if (userInSession != null) {
                return new ResponseEntity<>("User is already logged in.", HttpStatus.BAD_REQUEST);
            }

            if (user.getEmail().equals(login) && user.getPassword().equals(pass)) {
                session.setAttribute(String.valueOf(user.getId()), user);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("This user can not login.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/logout-user", method = RequestMethod.GET)
    public ResponseEntity<String> logout(HttpSession session, @RequestParam(value = "email") String login){
        if (login == null){
            return new ResponseEntity<>("Input is not correct.", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = userDAO.findUserByEmail(login);

            if (user == null){
                return new ResponseEntity<>("Login is not correct.", HttpStatus.BAD_REQUEST);
            }

            if (session.getAttribute(String.valueOf(user.getId())) != null){
                session.removeAttribute(String.valueOf(user.getId()));
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("User with ID " + user.getId() + " already logged out.", HttpStatus.BAD_REQUEST);
            }
        }
        catch (InternalServerError e){
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/income-requests", method = RequestMethod.GET)
    public List<User> getIncomeRequests(String userId)throws BadRequestException, InternalServerError{
        List<User> users;
        try {
            users = userService.getIncomeRequests(userId);
            return users;
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            throw new BadRequestException("Something is wrong with the input.");
        }catch (InternalServerError e) {
            System.err.println(e.getMessage());
            throw  new InternalServerError("Something went wrong...");
        }
    }

    @RequestMapping(path = "/outcome-requests", method = RequestMethod.GET)
    public List<User> getOutcomeRequests(String userId) throws BadRequestException, InternalServerError {
        List<User> users;
        try {
            users = userService.getOutcomeRequests(userId);
            return users;
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            throw new BadRequestException("Something is wrong with the input.");
        }catch (InternalServerError e) {
            System.err.println(e.getMessage());
            throw  new InternalServerError("Something went wrong...");
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
    String update(HttpServletRequest req)throws IOException, BadRequestException{
        User user = mappingObject(req);
        long inputId = Long.parseLong(req.getParameter("userId"));

        try{
            if (userDAO.findById(inputId) == null){
                return "User with ID - " + inputId + " does not exist in the DB";
            }
            else {
                userService.update(user);
            }
        } catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw  e;
        } catch (InternalServerError e) {
            System.err.println(e.getMessage());
            return "Something went wrong...";
        }
        return "User update success";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteUser", produces = "text/plain")
    public @ResponseBody
    String delete(HttpServletRequest req)throws BadRequestException{
        try {
            User user = userDAO.findById(Long.parseLong(req.getParameter("userId")));
            long userId = Long.parseLong(req.getParameter("userId"));
            if (user == null){
                return "The User with ID " + userId + " does not exist in the DB.";
            }
            else {
                userService.delete(user.getId());
            }
        } catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw e;
        } catch (InternalServerError e) {
            System.err.println(e.getMessage());
            return "Something went wrong...";
        }
        return "User deleted success";
    }
}
