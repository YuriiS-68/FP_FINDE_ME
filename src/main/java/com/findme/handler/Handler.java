package com.findme.handler;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.User;

public interface Handler {

    void setNextHandler(Handler next);

    void setRelationship(Relationship relationship, User user, String status, Long userIdTo, Long idUserFrom)throws BadRequestException, InternalServerError;
}
