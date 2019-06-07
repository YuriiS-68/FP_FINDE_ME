package com.findme.handler;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.core.annotation.AnnotationAwareOrderComparator.INSTANCE;

@Component
public class HandlerForUser {

    @Autowired
    private List<Handler> handlers = new ArrayList<>();

    @PostConstruct
    public void init(){
        handlers.sort(INSTANCE);
    }

    public void execute(Relationship relationship, User user, String status, String userId)throws BadRequestException, InternalServerError {
        for (Handler handler : handlers){
            handler.setRelationship(relationship, user, status, userId);
        }
    }
}
