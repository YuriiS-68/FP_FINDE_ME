package com.findme.service;

import com.findme.dao.RelationshipDAO;
import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.handler.*;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class RelationshipService {

    private RelationshipDAO relationshipDAO;
    private UserDAO userDAO;
    private HandlerChain handlerChain;

    @Autowired
    public RelationshipService(RelationshipDAO relationshipDAO, UserDAO userDAO, HandlerChain handlerChain) {
        this.relationshipDAO = relationshipDAO;
        this.userDAO = userDAO;
        this.handlerChain = handlerChain;
    }

    public void setRelationship(String idUserTo, String idUserFrom, HttpSession session)throws BadRequestException, InternalServerError{
        User userFrom = (User) session.getAttribute("userLogged");
        System.out.println("User in session userFrom - " + userFrom);
        if (userFrom == null){
            throw new BadRequestException("User with ID " + idUserFrom + " is not logged in.");
        }

        User userTo = userDAO.findById(Long.parseLong(idUserTo));
        if (userTo == null){
            throw new BadRequestException("User with ID " + idUserTo + " is not found in DB.");
        }

        Relationship relationshipFind = relationshipDAO.getRelationship(userFrom.getId(), userTo.getId());
        System.out.println("relationshipFind - " + relationshipFind);
        try {
            if (relationshipFind == null){
                Relationship relationship = new Relationship();
                relationship.setUserFrom(userFrom);
                relationship.setUserTo(userTo);
                relationship.setStatusType(RelationshipStatusType.REQUESTED);
                save(sendRequest(Long.parseLong(idUserFrom), relationship));
            }
            else if (relationshipFind.getStatusType().equals(RelationshipStatusType.CANCELED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DECLINED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DELETED)){
                relationshipFind.setStatusType(RelationshipStatusType.REQUESTED);
                relationshipDAO.update(sendRequest(Long.valueOf(idUserFrom), relationshipFind));
            }
            else {
                throw new BadRequestException("Something is wrong with the input.");
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    public void setRelationshipByStatus(String status, String idUserTo, String idUserFrom, HttpSession session) throws BadRequestException, InternalServerError {
        if (status == null || idUserTo == null || idUserFrom == null){
            throw  new BadRequestException("Status or userIdTo or userIdFrom is not exist.");
        }

        User userInSession = (User) session.getAttribute("userLogged");
        User userFrom = null;
        User userTo = null;
        if (userInSession.getId().equals(Long.parseLong(idUserFrom))){
            userFrom = userInSession;
        }
        if (userInSession.getId().equals(Long.parseLong(idUserTo))){
            userTo = userInSession;
        }

        Relationship relationshipFind = relationshipDAO.getRelationship(Long.valueOf(idUserFrom), Long.valueOf(idUserTo));

        try {
            if (userTo != null){
                handlerChain.execute(relationshipFind, userTo, status, Long.valueOf(idUserTo), Long.valueOf(idUserFrom));
            }

            if (userFrom != null){
                handlerChain.execute(relationshipFind, userFrom, status, Long.valueOf(idUserTo), Long.valueOf(idUserFrom));
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    public void validationInputData(String idUserFrom, String idUserTo, HttpSession session)throws BadRequestException{
        if (idUserFrom == null || idUserTo == null){
            throw  new BadRequestException("UserFrom or userTo does not exist.");
        }

        if (idUserFrom.equals(idUserTo)){
            throw  new BadRequestException("Actions between the same user are not possible.");
        }

        if (session == null){
            throw  new BadRequestException("Session is not exist.");
        }
    }

    /*public User choiceUsers(String idUserTo, String idUserFrom, HttpSession session)throws BadRequestException{
        User userInSession = (User) session.getAttribute("userLogged");

        if (userInSession.getId().equals(Long.parseLong(idUserFrom)) || userInSession.getId().equals(Long.parseLong(idUserTo))) {
            return userInSession;
        }
        else{
            throw new BadRequestException("User is not selected because he is not in session.");
        }
    }*/

    private Relationship sendRequest(Long idUser, Relationship relationship) throws BadRequestException, InternalServerError {
        if (idUser == null || relationship == null){
            throw new BadRequestException("User or relationship is not found.");
        }

        if (checkingQuantityRequested(idUser)){
            relationship.setStatusType(RelationshipStatusType.REQUESTED);
        }
        else {
            throw new BadRequestException("Limit on sent requests exceeded.");
        }
        return relationship;
    }

    private boolean checkingQuantityRequested(Long id) throws BadRequestException, InternalServerError{
        if (id == null){
            throw new BadRequestException("ID does not exist.");
        }
        return relationshipDAO.getQuantityRequests(id, RelationshipStatusType.REQUESTED) < 10;
    }

    private Relationship save(Relationship relationship)throws BadRequestException {
        if (relationship != null && relationship.getId() != null){
            throw new BadRequestException("This Relationship with ID - " + relationship.getId() + " can not save in DB.");
        }
        else {
            relationshipDAO.save(relationship);
        }
        return relationship;
    }
}
