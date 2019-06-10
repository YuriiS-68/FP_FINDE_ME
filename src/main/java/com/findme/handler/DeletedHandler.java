package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeletedHandler extends RelationshipHandler {

    private final RelationshipDAO relationshipDAO;

    @Autowired
    public DeletedHandler(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    @Override
    public void setRelationship(Relationship relationship, User user, String status, String userId, Long idUserFrom) throws BadRequestException, InternalServerError {
        if (user != null &&
                checkStatusForChange(relationship, RelationshipStatusType.ACCEPTED, RelationshipStatusType.DELETED, status, user.getId(), userId)){
            relationshipDAO.update(delFromFriends(idUserFrom, relationship));
        }
    }

    private Relationship delFromFriends(Long idUser, Relationship relationship) throws BadRequestException, InternalServerError {
        if (idUser == null || relationship == null){
            throw new BadRequestException("IdUser or relationship is not found.");
        }

        if (checkingAcceptedDate(idUser)){
            relationship.setStatusType(RelationshipStatusType.DELETED);
            relationship.setAcceptedFriends(null);
        }
        else {
            throw new BadRequestException("You can not perform an action delete from friends.");
        }
        return relationship;
    }

    private boolean checkingAcceptedDate(Long id) throws BadRequestException, InternalServerError{
        if (id == null){
            throw new BadRequestException("ID is not found.");
        }
        return relationshipDAO.getQuantityHoursAfterAccepted(id, RelationshipStatusType.ACCEPTED) >= 3;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
