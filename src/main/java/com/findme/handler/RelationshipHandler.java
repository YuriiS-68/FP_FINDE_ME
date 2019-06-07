package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.core.Ordered;

public abstract class RelationshipHandler implements Handler {

    //RelationshipDAO relationshipDAO;

    public abstract void setRelationship(Relationship relationship, User user, String status, String userId)throws BadRequestException, InternalServerError;

    boolean checkStatusForChange(Relationship relationship, RelationshipStatusType currentStatus, RelationshipStatusType newStatus, String status, Long id, String userId){
        return relationship != null && relationship.getStatusType().equals(currentStatus) && status.equals(newStatus.toString()) && id.equals(Long.parseLong(userId));
    }

    /*public RelationshipDAO getRelationshipDAO() {
        return relationshipDAO;
    }*/
}
