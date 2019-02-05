package com.findme.controller;

import com.findme.dao.PostDAO;
import com.findme.exception.BadRequestException;
import com.findme.models.Post;
import com.findme.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
    String update(HttpServletRequest req)throws IOException, BadRequestException{
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
        }
        return "User update success";
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/deletePost", produces = "text/plain")
    public @ResponseBody
    String delete(HttpServletRequest req)throws BadRequestException{
        Post post = postDAO.findById(Long.parseLong(req.getParameter("postId")));
        long postId = Long.parseLong(req.getParameter("postId"));

        try {
            if (post == null){
                return "The Post with ID " + postId + " does not exist in the DB.";
            }
            else {
                postService.delete(post.getId());
            }
        }catch (BadRequestException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return "User deleted success";
    }

    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    public void setPostDAO(PostDAO postDAO) {
        this.postDAO = postDAO;
    }
}
