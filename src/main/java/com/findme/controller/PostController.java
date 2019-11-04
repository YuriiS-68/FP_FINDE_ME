package com.findme.controller;

import com.findme.dao.PostDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import com.findme.models.PostInfo;
import com.findme.models.User;
import com.findme.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class PostController extends Utils<Post> {

    private PostService postService;
    private PostDAO postDAO;

    @Autowired
    public PostController(PostService postService, PostDAO postDAO) {
        this.postService = postService;
        this.postDAO = postDAO;
    }

    //1. Проверить находится ли юзер делающий пост онлайн
    //2. Проверить статус пользователя на странице которого создается пост
    //3. Если друзья или своя страница, то создаем пост
    @RequestMapping(method = RequestMethod.POST, path = "/createPost")
    public ResponseEntity<String> addPost(@ModelAttribute PostInfo postInfo, HttpSession session){

        User userPosted = (User) session.getAttribute(String.valueOf(postInfo.getIdUserPosted()));
        System.out.println("User posted - " + userPosted);

        try {
            if (userPosted == null){
                return new ResponseEntity<>("The post can not be posted. User is not logged.", HttpStatus.BAD_REQUEST);
            }

            postService.createPost(post);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/savePost", produces = "text/plain")
    public @ResponseBody
    String save(HttpServletRequest req) throws IOException, BadRequestException {
        Post post = mappingObject(req);

        try {
            postService.save(post);
        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return "Post saved success.";
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/updatePost", produces = "text/plain")
    public @ResponseBody
    String update(HttpServletRequest req)throws IOException, BadRequestException {
        Post post = mappingObject(req);
        long inputId = Long.parseLong(req.getParameter("postId"));

        try{
            if (postDAO.findById(inputId) == null){
                return "Post with ID - " + inputId + " does not exist in the DB";
            }
            else {
                postService.update(post);
            }
        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw  e;
        } catch (InternalServerError e) {
            System.err.println(e.getMessage());
            return "Something went wrong...";
        }
        return "User update success";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deletePost", produces = "text/plain")
    public @ResponseBody
    String delete(HttpServletRequest req)throws BadRequestException {
        try {
            Post post = postDAO.findById(Long.parseLong(req.getParameter("postId")));
            long postId = Long.parseLong(req.getParameter("postId"));
            if (post == null){
                return "The Post with ID " + postId + " does not exist in the DB.";
            }
            else {
                postService.delete(post.getId());
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
