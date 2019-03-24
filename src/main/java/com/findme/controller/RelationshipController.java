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
    //2. отменить свой запрос, если он не был ещё принят, статус Запрос отправлен, тогда статус меняется на Запрос отклонён
    //3. удалить из друзей, статус Друзья, меняется на Удалён из друзей
    //юзер получивший запрос может:
    //1. отменить запрос, статус Запрос отправлен, тогда статус меняется на Запрос отклонён
    //2. принять запрос, статус Запрос отправлен, тогда статус меняется на Друзья
    //3. удалить из друзей, статус Друзья, тогда статус меняется на Удалён из друзей

    @RequestMapping(path = "/add-friends", method = RequestMethod.POST)
    public ResponseEntity<String> addRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo){
        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = relationshipService.getUserFromSession(session, userIdFrom);
            User userTo = userDAO.findById(idUserTo);

            if (userTo == null || userFrom == null) {
                return new ResponseEntity<>("Users is not found.", HttpStatus.BAD_REQUEST);
            }

            relationshipService.validationInputData(idUserFrom, idUserTo);

            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            if (relationshipFind == null){
                Relationship relationship = new Relationship();
                relationship.setUserFrom(userFrom);
                relationship.setUserTo(userTo);
                relationship.setStatusType(RelationshipStatusType.FRIEND_REQUEST);
                relationshipService.save(relationship);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return relationshipService.sendFriendRequest(relationshipFind);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/update-relationship", method = RequestMethod.POST)
    public ResponseEntity<String> updateRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo,
                                   @RequestParam String status){
        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = relationshipService.getUserFromSession(session, userIdFrom);
            User userTo = relationshipService.getUserFromSession(session, userIdTo);

            relationshipService.validationInputData(idUserFrom, idUserTo);

            if (userTo == null && userFrom == null) {
                return new ResponseEntity<>("Users is not authorized.", HttpStatus.BAD_REQUEST);
            }

            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            if (relationshipService.getUserFromSession(session, userIdTo) != null || userFrom != null){
                return relationshipService.updateRelationshipByStatus(relationshipFind, status, userIdTo, session);
            }
            else {
                return new ResponseEntity<>("Something went wrong...", HttpStatus.BAD_REQUEST);
            }
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }
}
