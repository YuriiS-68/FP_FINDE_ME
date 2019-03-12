package com.findme.controller;

import com.findme.dao.RelationshipDAO;
import com.findme.dao.UserDAO;
import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import com.findme.models.User;
import com.findme.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RelationshipController extends Utils<Relationship> {

    private RelationshipService relationshipService;
    private RelationshipDAO relationshipDAO;
    private UserDAO userDAO;

    @Autowired
    public RelationshipController(RelationshipService relationshipService, RelationshipDAO relationshipDAO, UserDAO userDAO) {
        this.relationshipService = relationshipService;
        this.relationshipDAO = relationshipDAO;
        this.userDAO = userDAO;
    }

    //юзер отправивший запрос на добавление в друзья может:
    //1. отправить запрос на добавление в друзья, статус Запрос отправлен
    //2. отменить свой запрос, если он не был ещё принят, тогда статус меняется на Запрос отклонён
    //юзер получивший запрос может:
    //1. отменить запрос, тогда статус меняется на Запрос отклонён
    //2. принять запрос, тогда статус меняется на Друзья
    //3. удалить из друзей, тогда статус меняется на Удалён из друзей

    @RequestMapping(path = "/add-friends", method = RequestMethod.POST)
    public ResponseEntity<String> addRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo){
        System.out.println("Run method addRelationship");
        if (userIdFrom.equals(userIdTo)){
            return new ResponseEntity<>("It`s impossible to send a friend request to yourself.", HttpStatus.BAD_REQUEST);
        }

        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = userDAO.findById(idUserFrom);
            User userTo = userDAO.findById(idUserTo);
            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);
            System.out.println("RelationshipFind - " + relationshipFind);

            if (userTo != null && userFrom != null){
                if (session.getAttribute(userFrom.getEmail()) != null) {
                    if (relationshipFind == null){
                        Relationship relationship = new Relationship();
                        relationship.setUserFrom(userFrom);
                        relationship.setUserTo(userTo);
                        relationship.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                        relationshipService.save(relationship);
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
                else if (session.getAttribute(userFrom.getEmail()) == null) {
                    return new ResponseEntity<>("User with ID " + idUserFrom + " is not in the system.", HttpStatus.BAD_REQUEST);
                }
            }

            if (userTo == null || userFrom == null){
                return checkUsersInDB(userFrom, userTo, idUserFrom, idUserTo);
            }
            else
                return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/update-relationship", method = RequestMethod.POST)
    public ResponseEntity<String> updateRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo,
                                   @RequestParam String status){
        System.out.println("Run method updateRelationship");
        if (userIdFrom.equals(userIdTo)){
            return new ResponseEntity<>("Actions between the same user are not possible.", HttpStatus.BAD_REQUEST);
        }

        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = userDAO.findById(idUserFrom);
            User userTo = userDAO.findById(idUserTo);
            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            if (relationshipFind == null || status == null){
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }

            if (userTo == null || userFrom == null){
                return checkUsersInDB(userFrom, userTo, idUserFrom, idUserTo);
            }

            if (session.getAttribute(userFrom.getEmail()) != null || session.getAttribute(userTo.getEmail()) != null){
                if (status.equals(RelationshipStatusType.FRIEND_REQUEST.toString())){
                    return sendFriendRequest(relationshipFind);
                }

                if (status.equals(RelationshipStatusType.REQUEST_REJECTED.toString())){
                    return requestRejected(relationshipFind);
                }

                if (status.equals(RelationshipStatusType.FRIENDS.toString())){
                    return addInFriends(relationshipFind);
                }

                if (status.equals(RelationshipStatusType.REMOVED_FROM_FRIENDS.toString())){
                    return deleteFromFriends(relationshipFind);
                }
            }

            if (session.getAttribute(userFrom.getEmail()) == null) {
                return new ResponseEntity<>("User with ID " + idUserFrom + " is not in the system.", HttpStatus.BAD_REQUEST);
            }

            if (session.getAttribute(userTo.getEmail()) == null) {
                return new ResponseEntity<>("User with ID " + idUserTo + " is not in the system.", HttpStatus.BAD_REQUEST);
            }
            else {
                return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> addInFriends(Relationship relationshipFind){
        if (relationshipFind == null){
            return new ResponseEntity<>("Input data is wrong.", HttpStatus.BAD_REQUEST);
        }

        if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS)){
            return new ResponseEntity<>("You are already friends with this user.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
                relationshipFind.setStatusType(RelationshipStatusType.FRIENDS);
                relationshipService.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            return new ResponseEntity<>("Could not add user as friend.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> sendFriendRequest(Relationship relationshipFind){
        if (relationshipFind == null){
            return new ResponseEntity<>("Input data is wrong.", HttpStatus.BAD_REQUEST);
        }

        if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
            return new ResponseEntity<>("Request already sent to user.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.REQUEST_REJECTED) ||
                    relationshipFind.getStatusType().equals(RelationshipStatusType.REMOVED_FROM_FRIENDS)){
                relationshipFind.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                relationshipService.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            return new ResponseEntity<>("Failed to send friend request.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> deleteFromFriends(Relationship relationshipFind){
        if (relationshipFind == null){
            return new ResponseEntity<>("Input data is wrong.", HttpStatus.BAD_REQUEST);
        }

        if (relationshipFind.getStatusType().equals(RelationshipStatusType.REMOVED_FROM_FRIENDS)){
            return new ResponseEntity<>("You have already been removed from friends.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS)){
                relationshipFind.setStatusType(RelationshipStatusType.REMOVED_FROM_FRIENDS);
                relationshipService.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            return new ResponseEntity<>("Could not remove user from friends.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> requestRejected(Relationship relationshipFind){
        if (relationshipFind == null){
            return new ResponseEntity<>("Input data is wrong.", HttpStatus.BAD_REQUEST);
        }

        if (relationshipFind.getStatusType().equals(RelationshipStatusType.REQUEST_REJECTED)){
            return new ResponseEntity<>("Request has already been rejected.", HttpStatus.BAD_REQUEST);
        }

        try {
            if (relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
                relationshipFind.setStatusType(RelationshipStatusType.REQUEST_REJECTED);
                relationshipService.update(relationshipFind);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
            }
        }catch (BadRequestException e) {
            return new ResponseEntity<>("The request was not canceled.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> checkUsersInDB(User userFrom, User userTo, long idUserFrom, long idUserTo){
        if (userTo == null){
            return new ResponseEntity<>("User with ID " + idUserTo + " not found in the database.", HttpStatus.BAD_REQUEST);
        }

        if (userFrom == null){
            return new ResponseEntity<>("User with ID " + idUserFrom + " not found in the database.", HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/income-requests", method = RequestMethod.GET)
    public List getIncomeRequests(@RequestParam String userId){


        return null;
    }

    @RequestMapping(path = "/outcome-requests", method = RequestMethod.GET)
    public List getOutcomeRequests(@RequestParam String userId){


        return null;
    }

    public void setRelationshipService(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    public void setRelationshipDAO(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
