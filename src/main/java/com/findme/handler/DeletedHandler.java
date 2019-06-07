package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.stereotype.Component;

@Component
public class DeletedHandler extends RelationshipHandler {

    private RelationshipDAO relationshipDAO;

    /*public DeletedHandler() {
        super();
        this.relationshipDAO = getRelationshipDAO();
    }*/

    @Override
    public void setRelationship(Relationship relationship, User user, String status, String userId) throws BadRequestException, InternalServerError {
        if (user != null &&
                checkStatusForChange(relationship, RelationshipStatusType.ACCEPTED, RelationshipStatusType.DELETED, status, user.getId(), userId)){
            relationshipDAO.update(delFromFriends(user, relationship));
        }
    }

    private Relationship delFromFriends(User user, Relationship relationship) throws BadRequestException, InternalServerError {
        if (user == null || relationship == null){
            throw new BadRequestException("User or relationship is not found.");
        }

        if (checkingAcceptedDate(user)){
            relationship.setStatusType(RelationshipStatusType.DELETED);
            relationship.setAcceptedFriends(null);
        }
        else {
            throw new BadRequestException("You can not perform an action delete from friends.");
        }
        return relationship;
    }

    private boolean checkingAcceptedDate(User user) throws BadRequestException, InternalServerError{
        if (user == null){
            throw new BadRequestException("User is not found.");
        }
        return relationshipDAO.getQuantityHoursAfterAccepted(user.getId(), RelationshipStatusType.ACCEPTED) >= 3;
    }
}
