package com.findme.handler;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;

public abstract class RelationshipHandler implements Handler{

    public abstract void setRelationship(Relationship relationship, User user, String status, Long userIdTo, Long idUserFrom)throws BadRequestException, InternalServerError;

    boolean checkStatusForChange(Relationship relationship, RelationshipStatusType currentStatus, RelationshipStatusType newStatus, String status){
        return relationship != null && relationship.getStatusType().equals(currentStatus) && status.equals(newStatus.toString());
    }
}
