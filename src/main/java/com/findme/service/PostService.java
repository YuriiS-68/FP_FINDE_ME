package com.findme.service;

import com.findme.dao.PostDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private PostDAO postDAO;

    @Autowired
    public PostService(PostDAO postDAO){
        this.postDAO = postDAO;
    }

    public void setPostDAO(PostDAO postDAO) {
        this.postDAO = postDAO;
    }
}
