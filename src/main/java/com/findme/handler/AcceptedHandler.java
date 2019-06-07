package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AcceptedHandler extends RelationshipHandler {

    private RelationshipDAO relationshipDAO;

    /*public AcceptedHandler() {
        super();
        this.relationshipDAO = getRelationshipDAO();
    }*/

    @Override
    public void setRelationship(Relationship relationship, User user, String status, String userId) throws BadRequestException, InternalServerError{
        if (user != null &&
                checkStatusForChange(relationship, RelationshipStatusType.REQUESTED, RelationshipStatusType.ACCEPTED, status, user.getId(), userId)){
            relationshipDAO.update(addFriendsByRequest(user, relationship));
        }
    }

    private Relationship addFriendsByRequest(User user, Relationship relationship) throws BadRequestException, InternalServerError {
        if (user == null || relationship == null){
            throw new BadRequestException("User or relationship is not found.");
        }

        if (checkingNumberFriends(user)){
            Date acceptedRequest = new Date();
            relationship.setAcceptedFriends(acceptedRequest);
            relationship.setStatusType(RelationshipStatusType.ACCEPTED);
        }
        else {
            throw new BadRequestException("Friends limit exceeded.");
        }
        return relationship;
    }

    private boolean checkingNumberFriends(User user) throws BadRequestException, InternalServerError {
        if (user == null || user.getId() == null){
            throw new BadRequestException("User or his ID is not found.");
        }
        return relationshipDAO.getQuantityFriends(user.getId(), RelationshipStatusType.ACCEPTED) < 100;
    }
}
