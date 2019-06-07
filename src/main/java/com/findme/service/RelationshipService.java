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
    private HandlerForUser handlerForUser;

    @Autowired
    public RelationshipService(RelationshipDAO relationshipDAO, UserDAO userDAO, HandlerForUser handlerForUser) {
        this.relationshipDAO = relationshipDAO;
        this.userDAO = userDAO;
        this.handlerForUser = handlerForUser;
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
                save(sendRequest(userFrom, relationship));
            }
            else if (relationshipFind.getStatusType().equals(RelationshipStatusType.CANCELED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DECLINED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.DELETED)){
                relationshipFind.setStatusType(RelationshipStatusType.REQUESTED);
                relationshipDAO.update(sendRequest(userFrom, relationshipFind));
            }
            else {
                throw new BadRequestException("Something is wrong with the input.");
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    public void setRelationshipByStatus(String status, String userIdTo, String userIdFrom, HttpSession session) throws BadRequestException, InternalServerError {
        if (status == null || userIdTo == null || userIdFrom == null){
            throw  new BadRequestException("Status or userIdTo or userIdFrom is not exist.");
        }

        User userFrom = (User) session.getAttribute(userIdFrom);
        User userTo = (User) session.getAttribute(userIdTo);
        Relationship relationshipFind = relationshipDAO.getRelationship(Long.parseLong(userIdFrom), Long.parseLong(userIdTo));
        try {
            if (userTo != null){
                handlerForUser.execute(relationshipFind, userTo, status, userIdTo);
            }

            if (userFrom != null){
                handlerForUser.execute(relationshipFind, userFrom, status, userIdFrom);
            }
            /*if (userTo != null &&
                    checkStatusForChange(relationshipFind, RelationshipStatusType.REQUESTED, RelationshipStatusType.ACCEPTED, status, userTo.getId(), userIdTo)){
                relationshipDAO.update(addFriendsByRequest(userTo, relationshipFind));
                return;
            }

            if (userTo != null &&
                    checkStatusForChange(relationshipFind, RelationshipStatusType.REQUESTED, RelationshipStatusType.DECLINED, status, userTo.getId(), userIdTo)) {
                relationshipFind.setStatusType(RelationshipStatusType.DECLINED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (userTo != null &&
                    checkStatusForChange(relationshipFind, RelationshipStatusType.ACCEPTED, RelationshipStatusType.DELETED, status, userTo.getId(), userIdTo)){
                relationshipDAO.update(delFromFriends(userTo, relationshipFind));
                return;
            }

            if (userFrom != null &&
                    checkStatusForChange(relationshipFind, RelationshipStatusType.REQUESTED, RelationshipStatusType.CANCELED, status, userFrom.getId(), userIdFrom)){
                relationshipFind.setStatusType(RelationshipStatusType.CANCELED);
                relationshipDAO.update(relationshipFind);
                return;
            }

            if (userFrom != null &&
                    checkStatusForChange(relationshipFind, RelationshipStatusType.ACCEPTED, RelationshipStatusType.DELETED, status, userFrom.getId(), userIdFrom)){
                relationshipDAO.update(delFromFriends(userFrom, relationshipFind));
            }*/
            else {
                throw new BadRequestException("Something is wrong with the input. Method setRelationshipByStatus");
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
    }

    /*private void changeStatusRelationship(Relationship relationship, User user, String status, String userId)throws BadRequestException, InternalServerError{
        relationshipAcceptedHandler.setRelationship(relationship, user, status, userId);
    }*/

    /*private boolean checkStatusForChange(Relationship relationship, RelationshipStatusType currentStatus, RelationshipStatusType newStatus, String status, Long id, String userId){
        return relationship != null && relationship.getStatusType().equals(currentStatus) && status.equals(newStatus.toString()) && id.equals(Long.parseLong(userId));
    }*/

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

    /*private Relationship addFriendsByRequest(User user, Relationship relationship) throws BadRequestException, InternalServerError {
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
    }*/

    private Relationship sendRequest(User user, Relationship relationship) throws BadRequestException, InternalServerError {
        if (user == null || relationship == null){
            throw new BadRequestException("User or relationship is not found.");
        }

        if (checkingNumberRequested(user)){
            relationship.setStatusType(RelationshipStatusType.REQUESTED);
        }
        else {
            throw new BadRequestException("Limit on sent requests exceeded.");
        }
        return relationship;
    }

    /*private Relationship delFromFriends(User user, Relationship relationship) throws BadRequestException, InternalServerError {
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
    }*/

    /*private boolean checkingNumberFriends(User user) throws BadRequestException, InternalServerError {
        if (user == null){
            throw new BadRequestException("User is not found.");
        }
        return relationshipDAO.getQuantityFriends(user.getId(), RelationshipStatusType.ACCEPTED) < 100;
    }*/

    private boolean checkingNumberRequested(User user) throws BadRequestException, InternalServerError{
        if (user == null){
            throw new BadRequestException("User is not found.");
        }
        return relationshipDAO.getQuantityRequests(user.getId(), RelationshipStatusType.REQUESTED) < 10;
    }

    /*private boolean checkingAcceptedDate(User user) throws BadRequestException, InternalServerError{
        if (user == null){
            throw new BadRequestException("User is not found.");
        }
        return relationshipDAO.getQuantityHoursAfterAccepted(user.getId(), RelationshipStatusType.ACCEPTED) >= 3;
    }*/
}
