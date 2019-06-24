package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AcceptedHandler extends RelationshipHandler {

    private final RelationshipDAO relationshipDAO;
    private Handler chain;

    @Autowired
    public AcceptedHandler(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    @Override
    public void setRelationship(Relationship relationship, User user, String status, Long idUserTo, Long idUserFrom) throws BadRequestException, InternalServerError{
        if (user != null && user.getId().equals(idUserTo) &&
                checkStatusForChange(relationship, RelationshipStatusType.REQUESTED, RelationshipStatusType.ACCEPTED, status)){
            relationshipDAO.update(addFriendsByRequest(user, relationship));
        }
        else {
            this.chain.setRelationship(relationship, user, status, idUserTo, idUserFrom);
        }
    }

    public void setNextHandler(Handler nextChain) {
        this.chain = nextChain;
    }

    private Relationship addFriendsByRequest(User user, Relationship relationship) throws BadRequestException, InternalServerError {
        if (user == null || relationship == null){
            throw new BadRequestException("User or relationship is not found.");
        }

        if (checkingNumberFriends(user.getId())){
            Date acceptedRequest = new Date();
            relationship.setAcceptedFriends(acceptedRequest);
            relationship.setStatusType(RelationshipStatusType.ACCEPTED);
        }
        else {
            throw new BadRequestException("Friends limit exceeded.");
        }
        return relationship;
    }

    private boolean checkingNumberFriends(Long id) throws BadRequestException, InternalServerError {
        if (id == null){
            throw new BadRequestException("ID does not exist.");
        }
        return relationshipDAO.getQuantityFriends(id, RelationshipStatusType.ACCEPTED) < 10;
    }
}
