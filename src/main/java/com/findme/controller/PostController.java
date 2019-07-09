package com.findme.controller;

import com.findme.dao.PostDAO;
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

    @RequestMapping(method = RequestMethod.POST, value = "/createPost", produces = "text/plain")
    public ResponseEntity<String> addPost(HttpSession session, @ModelAttribute Post post){

        try {
            User user = (User) session.getAttribute(String.valueOf(post.getUserPosted().getId()));
            if (user == null) {
                return new ResponseEntity<>("The post can not be posted. User is not logged.", HttpStatus.BAD_REQUEST);
            }

            if (postDAO.findPostByUser(post)) {
                postService.createPost(post);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Post is already exist.", HttpStatus.BAD_REQUEST);
            }
        } catch (BadRequestException e) {
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
