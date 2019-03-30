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

    public ResponseEntity<String> updateRelationshipByStatus(Relationship relationshipFind, String status, String userIdTo, HttpSession session)throws BadRequestException{
        validate(relationshipFind, status);
        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.FRIENDS.toString()) &&
                    session.getAttribute(userIdTo) != null){
                relationshipFind.setStatusType(RelationshipStatusType.FRIENDS);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS) && status.equals(RelationshipStatusType.REMOVED_FROM_FRIENDS.toString())){
                relationshipFind.setStatusType(RelationshipStatusType.REMOVED_FROM_FRIENDS);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }

            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(RelationshipStatusType.REQUEST_REJECTED.toString())){
                relationshipFind.setStatusType(RelationshipStatusType.REQUEST_REJECTED);
                relationshipDAO.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
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

    public void validationInputData(String idUserFrom, String idUserTo)throws BadRequestException{
        if (idUserFrom == null || idUserTo == null){
            throw  new BadRequestException("Something is wrong with the input.");
        }

        if (idUserFrom.equals(idUserTo)){
            throw  new BadRequestException("Actions between the same user are not possible.");
        }
    }

    private void validate(Relationship relationship, String status)throws BadRequestException{
        if (relationship == null || status == null){
            throw new BadRequestException("Relationship or status is not exist.");
        }

        if (relationship.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(String.valueOf(RelationshipStatusType.FRIENDS)) ||
                relationship.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST) && status.equals(String.valueOf(RelationshipStatusType.REQUEST_REJECTED)) ||
                relationship.getStatusType().equals(RelationshipStatusType.FRIENDS) && status.equals(String.valueOf(RelationshipStatusType.REMOVED_FROM_FRIENDS))){
            return;
        }
        else {
            throw new BadRequestException("Something is wrong with the input");
        }
    }
}
