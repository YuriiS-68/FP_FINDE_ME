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
    private Handler chain;

    @Autowired
    public DeletedHandler(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    @Override
    public void setRelationship(Relationship relationship, User user, String status, Long idUserTo, Long idUserFrom) throws BadRequestException, InternalServerError {
        if (user != null && (user.getId().equals(idUserFrom) || user.getId().equals(idUserTo)) &&
                checkStatusForChange(relationship, RelationshipStatusType.ACCEPTED, RelationshipStatusType.DELETED, status)){
            relationshipDAO.update(delFromFriends(idUserFrom, idUserTo,  relationship));
        }
        else {
            this.chain.setRelationship(relationship, user, status, idUserTo, idUserFrom);
        }
    }

    public void setNextHandler(Handler nextChain) {
        this.chain = nextChain;
    }

    private Relationship delFromFriends(Long idUserFrom, Long idUserTo, Relationship relationship) throws BadRequestException, InternalServerError {
        if (idUserFrom == null || idUserTo == null || relationship == null){
            throw new BadRequestException("IdUserFrom or idUserTo or relationship is not found.");
        }

        if (checkingAcceptedDate(idUserFrom, idUserTo)){
            relationship.setStatusType(RelationshipStatusType.DELETED);
            relationship.setAcceptedFriends(null);
        }
        else {
            throw new BadRequestException("You can not perform an action delete from friends.");
        }
        return relationship;
    }

    private boolean checkingAcceptedDate(Long idUserFrom, Long idUserTo) throws BadRequestException, InternalServerError{
        if (idUserFrom == null || idUserTo == null){
            throw new BadRequestException("IdUserFrom or idUserTo is not found.");
        }
        return relationshipDAO.getQuantityHoursAfterAccepted(idUserFrom, idUserTo, RelationshipStatusType.ACCEPTED) >= 3;
    }
}
