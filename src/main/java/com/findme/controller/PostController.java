package com.findme.controller;

import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import com.findme.models.User;
import com.findme.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class PostController extends Utils<Post> {

    private PostService postService;
    private UserDAO userDAO;

    @Autowired
    public PostController(PostService postService, UserDAO userDAO) {
        this.postService = postService;
        this.userDAO = userDAO;
    }

    //TODO produces/consumes лучше не юзать, тебе подходят те что по дефолту. Из-за тоже может и не попадать сюда запрос
    @RequestMapping(method = RequestMethod.POST, path = "/createPost"/*, produces = "text/plain", consumes = "application/json"*/)
    //TODO тут как раз используй @ModelAttribute , посколько data: $('#post-form').serialize(), шлет данные не в JSON а в параметрах
    public ResponseEntity<String> addPost(@ModelAttribute Post post, HttpSession session, @RequestParam(value = "idUser") String idUser){
        User userPosted = (User) session.getAttribute("userLogged");
        try {
            if(userPosted == null){
                return new ResponseEntity<>("The post can not be posted. User is not logged.", HttpStatus.BAD_REQUEST);
            }
            User userPagePosted = userDAO.findById(Long.valueOf(idUser));
            post.setUserPagePosted(userPagePosted);
            post.setUserPosted(userPosted);
            postService.createPost(post);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
