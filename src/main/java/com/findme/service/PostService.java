package com.findme.service;

import com.findme.dao.PostDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private PostDAO postDAO;

    @Autowired
    public PostService(PostDAO postDAO){
        this.postDAO = postDAO;
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
}
