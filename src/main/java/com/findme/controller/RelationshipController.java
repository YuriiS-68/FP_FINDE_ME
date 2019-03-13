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
import java.util.ArrayList;
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
        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            if (relationshipFind == null){
                User userFrom = userDAO.findById(idUserFrom);
                User userTo = userDAO.findById(idUserTo);

                validationInputData(session,userFrom, userTo, idUserFrom, idUserTo);

                Relationship relationship = new Relationship();
                relationship.setUserFrom(userFrom);
                relationship.setUserTo(userTo);
                relationship.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                relationshipService.save(relationship);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Relationship between users already exist.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/update-relationship", method = RequestMethod.POST)
    public ResponseEntity<String> updateRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo,
                                   @RequestParam String status){
        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = userDAO.findById(idUserFrom);
            User userTo = userDAO.findById(idUserTo);
            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            validationInputData(session, userFrom, userTo, idUserFrom, idUserTo);
            validationInputDataForUpdate(session, relationshipFind, status, userTo, idUserTo);

            return updateRelationshipByStatus(relationshipFind, status);

        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> updateRelationshipByStatus(Relationship relationshipFind, String status){
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
        else {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> addInFriends(Relationship relationshipFind){
        try {
            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
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
        try {
            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.REQUEST_REJECTED) ||
                    relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.REMOVED_FROM_FRIENDS)){
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
        try {
            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.FRIENDS)){
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
        try {
            if (relationshipFind != null && relationshipFind.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
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

    private ResponseEntity<String> validationInputData(HttpSession session, User userFrom, User userTo, long idUserFrom, long idUserTo){
        if (idUserFrom == idUserTo){
            return new ResponseEntity<>("Actions between the same user are not possible.", HttpStatus.BAD_REQUEST);
        }

        if (userTo == null){
            return new ResponseEntity<>("User with ID " + idUserTo + " not found in the database.", HttpStatus.BAD_REQUEST);
        }

        if (userFrom == null){
            return new ResponseEntity<>("User with ID " + idUserFrom + " not found in the database.", HttpStatus.BAD_REQUEST);
        }

        if (session.getAttribute(userFrom.getEmail()) == null) {
            return new ResponseEntity<>("User with ID " + idUserFrom + " is not in the system.", HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> validationInputDataForUpdate(HttpSession session, Relationship relationshipFind, String status, User userTo, long idUserTo){
        if (relationshipFind == null || status == null){
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }

        if (session.getAttribute(userTo.getEmail()) == null) {
            return new ResponseEntity<>("User with ID " + idUserTo + " is not in the system.", HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/income-requests", method = RequestMethod.GET)
    public List<Relationship> getIncomeRequests(@RequestParam String userId){


        return null;
    }

    @RequestMapping(path = "/outcome-requests", method = RequestMethod.GET)
    public List<String> getOutcomeRequests(@RequestParam String userId){
        List<Relationship> relationshipList;
        List<String> outcomeRequests = new ArrayList<>();
        String relationshipOutcome;

        try {
            relationshipList = relationshipService.getOutcomeRequests(userId);
            for (Relationship relationship : relationshipList){
                if (relationship != null && relationship.getStatusType().equals(RelationshipStatusType.FRIEND_REQUEST)){
                    relationshipOutcome = relationship.getStatusType().toString();
                    outcomeRequests.add(relationshipOutcome);
                }
            }
        } catch (BadRequestException e) {
            e.printStackTrace();
        }

        return outcomeRequests;
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
