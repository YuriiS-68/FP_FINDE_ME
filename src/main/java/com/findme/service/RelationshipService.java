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

    private void update(Relationship relationship)throws BadRequestException{
        if (relationship == null){
            throw new BadRequestException("Relationship is not exist");
        }
        relationshipDAO.update(relationship);
    }

    public Relationship getRelationshipBetweenUsers(Long userFrom, Long userTo) throws InternalServerError, BadRequestException {
        if (userFrom == null || userTo == null){
            throw new BadRequestException("Input data is wrong.");
        }
        return relationshipDAO.getRelationship(userFrom, userTo);
    }

    public ResponseEntity<String> updateRelationshipByStatus(Relationship relationshipFind, String status, String userIdTo, HttpSession session){
        if (relationshipFind == null || status == null){
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.FRIENDS.toString()) &&
                    getUserFromSession(session, userIdTo) != null){
                relationshipFind.setStatusType(RelationshipStatusType.FRIENDS);
                update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.FRIENDS.toString()) &&
                    getUserFromSession(session, userIdTo) == null){
                return new ResponseEntity<>("Unable to be friends with yourself.", HttpStatus.BAD_REQUEST);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS) && status.equals(RelationshipStatusType.REMOVED_FROM_FRIENDS.toString())){
                relationshipFind.setStatusType(RelationshipStatusType.REMOVED_FROM_FRIENDS);
                update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.REQUEST_REJECTED.toString())){
                relationshipFind.setStatusType(RelationshipStatusType.REQUEST_REJECTED);
                update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            return new ResponseEntity<>("Could not add user as friend.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> sendFriendRequest(Relationship relationshipFind){
        if (relationshipFind == null){
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.REQUEST_REJECTED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.REMOVED_FROM_FRIENDS)){
                relationshipFind.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Failed to send friend request.", HttpStatus.BAD_REQUEST);
        }
    }

    public void validationInputData(User userTo, Long idUserFrom, Long idUserTo)throws BadRequestException{
        if (idUserFrom == null || idUserTo == null){
            throw  new BadRequestException("Something is wrong with the input.");
        }

        if (idUserFrom.equals(idUserTo)){
            throw  new BadRequestException("Actions between the same user are not possible.");
        }

        if (userTo == null){
            throw  new BadRequestException("User with ID " + idUserTo + " not found in the database.");
        }
    }

    public User getUserFromSession(HttpSession session, String userIdFrom)throws BadRequestException{
        if (userIdFrom == null || session == null){
            throw  new BadRequestException("Something is wrong with the input.");
        }
        return (User) session.getAttribute(userIdFrom);
    }

    public void setRelationshipDAO(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }
}
