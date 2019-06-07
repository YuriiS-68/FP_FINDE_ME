package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.stereotype.Component;

@Component
public class CanceledHandler extends RelationshipHandler {

    private RelationshipDAO relationshipDAO;

    /*public CanceledHandler() {
        super();
        this.relationshipDAO = getRelationshipDAO();
    }*/

    @Override
    public void setRelationship(Relationship relationship, User user, String status, String userId) throws BadRequestException, InternalServerError {
        if (user != null &&
                checkStatusForChange(relationship, RelationshipStatusType.REQUESTED, RelationshipStatusType.CANCELED, status, user.getId(), userId)){
            relationship.setStatusType(RelationshipStatusType.CANCELED);
            relationshipDAO.update(relationship);
        }
    }
}
