package com.findme.handler;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.User;

public class HandlerForUserFrom {

    //private static DeletedHandler deletedHandler = new DeletedHandler();
    //private static DeletedHandler relationshipCanceledHandler = new DeletedHandler();

    /*static {
        deletedHandler.nextHandler(relationshipCanceledHandler);
        relationshipCanceledHandler.nextHandler(deletedHandler);
    }*/

    /*public void handlerForUserFrom(Relationship relationship, User user, String status, String userId)throws BadRequestException, InternalServerError {
        deletedHandler.setRelationship(relationship, user, status, userId);
    }*/
}
