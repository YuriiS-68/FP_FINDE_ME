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
public class CanceledHandler extends RelationshipHandler {

    private final RelationshipDAO relationshipDAO;
    private Handler chain;

    @Autowired
    public CanceledHandler(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    @Override
    public void setRelationship(Relationship relationship, User user, String status, Long idUserTo, Long idUserFrom) throws BadRequestException, InternalServerError {
        if (user != null && user.getId().equals(idUserFrom) &&
                checkStatusForChange(relationship, RelationshipStatusType.REQUESTED, RelationshipStatusType.CANCELED, status)){
            relationship.setStatusType(RelationshipStatusType.CANCELED);
            relationshipDAO.update(relationship);
        }
        else {
            this.chain.setRelationship(relationship, user, status, idUserTo, idUserFrom);
        }
    }

    public void setNextHandler(Handler nextChain) {
        this.chain = nextChain;
    }

    /*@Override
    public int getOrder() {
        return 3;
    }*/
}
