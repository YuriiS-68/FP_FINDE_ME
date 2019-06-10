package com.findme.handler;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.core.Ordered;

public abstract class RelationshipHandler implements Handler, Ordered {

    public abstract void setRelationship(Relationship relationship, User user, String status, String userId, Long idUserFrom)throws BadRequestException, InternalServerError;

    boolean checkStatusForChange(Relationship relationship, RelationshipStatusType currentStatus, RelationshipStatusType newStatus, String status, Long id, String userId){
        return relationship != null && relationship.getStatusType().equals(currentStatus) && status.equals(newStatus.toString()) && id.toString().equals(userId);
    }
}
