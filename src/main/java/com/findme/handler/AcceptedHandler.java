package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AcceptedHandler extends RelationshipHandler {

    private final RelationshipDAO relationshipDAO;

    @Autowired
    public AcceptedHandler(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    @Override
    public void setRelationship(Relationship relationship, User user, String status, String userId, Long idUserFrom) throws BadRequestException, InternalServerError{
        if (user != null &&
                checkStatusForChange(relationship, RelationshipStatusType.REQUESTED, RelationshipStatusType.ACCEPTED, status, user.getId(), userId)){
            relationshipDAO.update(addFriendsByRequest(idUserFrom, relationship));
        }
    }

    private Relationship addFriendsByRequest(Long idUser, Relationship relationship) throws BadRequestException, InternalServerError {
        if (idUser == null || relationship == null){
            throw new BadRequestException("IdUser or relationship is not found.");
        }

        if (checkingNumberFriends(idUser)){
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
        return relationshipDAO.getQuantityFriends(id, RelationshipStatusType.ACCEPTED) < 4;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
