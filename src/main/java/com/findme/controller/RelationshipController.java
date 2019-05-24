package com.findme.controller;

import com.findme.exception.BadRequestException;
import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
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
import java.sql.SQLException;

@Controller
public class RelationshipController extends Utils<Relationship> {

    private RelationshipService relationshipService;

    @Autowired
    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
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
        try {
            relationshipService.validationInputData(userIdFrom, userIdTo, session);
            relationshipService.setRelationship(userIdTo, userIdFrom, session);
            return new ResponseEntity<>(HttpStatus.OK);
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
        try {
            relationshipService.validationInputData(userIdFrom, userIdTo, session);
            User userFrom = (User) session.getAttribute(userIdFrom);
            User userTo = (User) session.getAttribute(userIdTo);

            if (userTo != null || userFrom != null) {
                relationshipService.setRelationshipByStatus(status, userIdTo, userIdFrom, session);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Users is not authorized.", HttpStatus.BAD_REQUEST);
            }
        }catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (BadRequestException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Something is wrong with the input.", HttpStatus.BAD_REQUEST);
        }
    }
}
