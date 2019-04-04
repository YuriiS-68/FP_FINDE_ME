package com.findme.service;

import com.findme.dao.RelationshipDAO;
import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
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

    @Autowired
    public RelationshipService(RelationshipDAO relationshipDAO, UserDAO userDAO) {
        this.relationshipDAO = relationshipDAO;
        this.userDAO = userDAO;
    }

    public void setRelationship(String userIdTo, String userIdFrom, HttpSession session)throws BadRequestException, InternalServerError{

        User userFrom = (User) session.getAttribute(userIdFrom);
        if (userFrom == null){
            throw new BadRequestException("User with ID " + userIdFrom + " is not logged in.");
        }

        User userTo = userDAO.findById(Long.parseLong(userIdTo));
        if (userTo == null){
            throw new BadRequestException("User with ID " + userIdTo + " is not found in DB.");
        }

        Relationship relationshipFind = relationshipDAO.getRelationship(userFrom.getId(), userTo.getId());
        try {
            if (relationshipFind == null){
                Relationship relationship = new Relationship();
                relationship.setUserFrom(userFrom);
                relationship.setUserTo(userTo);
                relationship.setStatusType(RelationshipStatusType.REQUESTED);
                save(relationship);
            }
            else if (relationshipFind.getStatusType().equals(RelationshipStatusType.CANCELED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DECLINED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DELETED)){
                relationshipFind.setStatusType(RelationshipStatusType.REQUESTED);
                relationshipDAO.update(relationshipFind);
            }
            else {
                throw new BadRequestException("Something is wrong with the input.");
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    public void setRelationshipByStatus(String status, String userIdTo, String userIdFrom, HttpSession session) throws BadRequestException, InternalServerError{
        if (status == null){
            throw  new BadRequestException("Status is not exist.");
        }

        User userFrom = (User) session.getAttribute(userIdFrom);
        User userTo = (User) session.getAttribute(userIdTo);
        Relationship relationshipFind = relationshipDAO.getRelationship(Long.parseLong(userIdFrom), Long.parseLong(userIdTo));
        try {
            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.REQUESTED) &&
                    status.equals(RelationshipStatusType.ACCEPTED.toString()) && userTo.getId().equals(Long.parseLong(userIdTo))){
                relationshipFind.setStatusType(RelationshipStatusType.ACCEPTED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.REQUESTED) &&
                    status.equals(RelationshipStatusType.DECLINED.toString()) && userTo.getId().equals(Long.parseLong(userIdTo))){
                relationshipFind.setStatusType(RelationshipStatusType.DECLINED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.ACCEPTED) &&
                    status.equals(RelationshipStatusType.DELETED.toString()) && userTo.getId().equals(Long.parseLong(userIdTo))){
                relationshipFind.setStatusType(RelationshipStatusType.DELETED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.REQUESTED) &&
                    status.equals(RelationshipStatusType.CANCELED.toString()) && userFrom.getId().equals(Long.parseLong(userIdFrom))){
                relationshipFind.setStatusType(RelationshipStatusType.CANCELED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.ACCEPTED) &&
                    status.equals(RelationshipStatusType.DELETED.toString()) && userFrom.getId().equals(Long.parseLong(userIdFrom))){
                relationshipFind.setStatusType(RelationshipStatusType.DELETED);
                relationshipDAO.update(relationshipFind);
            }
            else {
                throw new BadRequestException("Something is wrong with the input. Method setRelationshipByStatus");
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    public void validationInputData(String idUserFrom, String idUserTo, HttpSession session)throws BadRequestException{
        if (idUserFrom == null || idUserTo == null){
            throw  new BadRequestException("UserFrom or userTo is not exist.");
        }

        if (idUserFrom.equals(idUserTo)){
            throw  new BadRequestException("Actions between the same user are not possible.");
        }

        if (session == null){
            throw  new BadRequestException("Session is not exist.");
        }
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
