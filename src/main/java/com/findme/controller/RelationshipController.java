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
import java.util.*;

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

        //получить userFrom из сессии
        //получить userTo из базы
        //сделать валидацию
        //получить relationship между юзерами

        try {
            User userFrom = relationshipService.getUserFromSession(session, userIdFrom);
            User userTo = userDAO.findById(idUserTo);

            relationshipService.validationInputData(userTo, idUserFrom, idUserTo);

            Relationship relationshipFind = relationshipDAO.getRelationship(idUserFrom, idUserTo);

            if (userFrom == null){
                return new ResponseEntity<>("User with ID " + idUserFrom + " is not authorized.", HttpStatus.BAD_REQUEST);
            }

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

    //1. Получить userFrom из сессии, если юзера нет в сессии дальнейшие действия не возможны
    //2. Получить userTo из базы, если userTo == null ошибка
    //3. Получить из базы relationship
    //4. Проверить есть ли userTo в сессии
    //5. Если нету, то ошибка - юзер не залогинен
    //6. Если есть, выполняю какое-то действие
    @RequestMapping(path = "/update-relationship", method = RequestMethod.POST)
    public ResponseEntity<String> updateRelationship(HttpSession session, @RequestParam String userIdFrom, @RequestParam String userIdTo,
                                   @RequestParam String status){
        long idUserFrom = Long.parseLong(userIdFrom);
        long idUserTo = Long.parseLong(userIdTo);

        try {
            User userFrom = relationshipService.getUserFromSession(session, userIdFrom);
            User userTo = userDAO.findById(idUserTo);

            relationshipService.validationInputData(userTo, idUserFrom, idUserTo);

            Relationship relationshipFind = relationshipService.getRelationshipBetweenUsers(idUserFrom, idUserTo);

            if (userFrom == null && relationshipService.getUserFromSession(session, userIdTo) == null){
                return new ResponseEntity<>("User with ID " + idUserFrom + " is not authorized.", HttpStatus.BAD_REQUEST);
            }

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

    @RequestMapping(path = "/income-requests", method = RequestMethod.GET)
    public List<Relationship> getIncomeRequests(@RequestParam String userId){


        return null;
    }

    //Получить список отправленных заявок
    //у relationship есть колонка с id получателя заявки
    //надо по этому айдишнику получить имя и фамилию пользователя из таблицы User
    //соединить их в одну строку
    //создать мапу и ложить в неё ключ - id, value - строка из имени и фамилии
    @RequestMapping(path = "/outcome-requests", method = RequestMethod.GET)
    public Map<Long, String> getOutcomeRequests(@RequestParam String userId) throws InternalServerError {
        long inputUserId = Long.parseLong(userId);
        Map<Long, String> outcomeRequests = new HashMap<>();
        StringBuilder fullNameUser = new StringBuilder();

        try {
            if (!userDAO.getUsersTo(inputUserId).isEmpty()){
                for (User user : userDAO.getUsersTo(inputUserId)){
                    if (user != null){
                        outcomeRequests.put(user.getId(), fullNameUser.append(user.getFirstName()).append(user.getLastName()).toString());
                    }
                }
                System.out.println("Map with requests - " + outcomeRequests);
                return outcomeRequests;
            }
            else {
                System.out.println("Empty map - " + outcomeRequests);
                return outcomeRequests;
            }
        }catch (InternalServerError e) {
            throw new InternalServerError("Something went wrong...");
        }
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
