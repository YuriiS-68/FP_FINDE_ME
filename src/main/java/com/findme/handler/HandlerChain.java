package com.findme.handler;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.User;
import org.springframework.stereotype.Component;

@Component
public class HandlerChain {

    private Handler chain;

    public HandlerChain(RelationshipDAO relationshipDAO){
        this.chain = new AcceptedHandler(relationshipDAO);

        Handler declinedHandlerObj = new DeclinedHandler(relationshipDAO);
        Handler canceledHandlerObj = new CanceledHandler(relationshipDAO);
        Handler deletedHandlerObj = new DeletedHandler(relationshipDAO);

        chain.setNextHandler(declinedHandlerObj);
        declinedHandlerObj.setNextHandler(canceledHandlerObj);
        canceledHandlerObj.setNextHandler(deletedHandlerObj);
    }

    public void execute(Relationship relationship, User user, String status, Long idUserTo, Long idUserFrom)throws BadRequestException, InternalServerError{
        chain.setRelationship(relationship, user, status, idUserTo, idUserFrom);
    }
}
