package com.findme.service;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class RelationshipService {

    private RelationshipDAO relationshipDAO;

    @Autowired
    public RelationshipService(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    public Relationship save(Relationship relationship)throws BadRequestException {
        if (relationship != null && relationship.getId() != null){
            throw new BadRequestException("This Relationship with ID - " + relationship.getId() + " can not save in DB.");
        }
        else {
            relationshipDAO.save(relationship);
        }
        return relationship;
    }

    public ResponseEntity<String> updateRelationshipByStatus(Relationship relationshipFind, String status, String userIdTo, HttpSession session){
        if (relationshipFind == null || status == null){
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.FRIENDS.toString()) &&
                    getUserFromSession(session, userIdTo) != null){
                validStatusAcceptRequest(status);
                relationshipFind.setStatusType(RelationshipStatusType.FRIENDS);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.FRIENDS.toString()) &&
                    getUserFromSession(session, userIdTo) == null){
                return new ResponseEntity<>("Unable to be friends with yourself.", HttpStatus.BAD_REQUEST);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS) && status.equals(RelationshipStatusType.REMOVED_FROM_FRIENDS.toString())){
                validStatusRemoved(status);
                relationshipFind.setStatusType(RelationshipStatusType.REMOVED_FROM_FRIENDS);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.REQUEST_REJECTED.toString())){
                validStatusCancelRequest(status);
                relationshipFind.setStatusType(RelationshipStatusType.REQUEST_REJECTED);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("Could not add user as friend.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> sendFriendRequest(Relationship relationshipFind) {
        if (relationshipFind == null){
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.REQUEST_REJECTED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.REMOVED_FROM_FRIENDS)){
                relationshipFind.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public User getUserFromSession(HttpSession session, String userIdFrom)throws BadRequestException{
        if (userIdFrom == null || session == null){
            throw  new BadRequestException("Something is wrong with the input.");
        }
        return (User) session.getAttribute(userIdFrom);
    }

    public void validationInputData(Long idUserFrom, Long idUserTo)throws BadRequestException{
        if (idUserFrom == null || idUserTo == null){
            throw  new BadRequestException("Something is wrong with the input.");
        }

        if (idUserFrom.equals(idUserTo)){
            throw  new BadRequestException("Actions between the same user are not possible.");
        }
    }

    private void validStatusAcceptRequest(String status)throws BadRequestException{
        if (!status.equals(RelationshipStatusType.FRIENDS.toString())){
            throw new BadRequestException("Input status is wrong.");
        }
    }

    private void validStatusRemoved(String status)throws BadRequestException{
        if (!status.equals(RelationshipStatusType.REMOVED_FROM_FRIENDS.toString())){
            throw new BadRequestException("Input status is wrong.");
        }
    }

    private void validStatusCancelRequest(String status)throws BadRequestException{
        if (!status.equals(RelationshipStatusType.REQUEST_REJECTED.toString())){
            throw new BadRequestException("Input status is wrong.");
        }
    }
}
