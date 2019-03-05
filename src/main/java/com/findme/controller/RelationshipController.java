package com.findme.controller;

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

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RelationshipController extends Utils<Relationship> {

    private RelationshipService relationshipService;
    private UserDAO userDAO;

    @Autowired
    public RelationshipController(RelationshipService relationshipService, UserDAO userDAO) {
        this.relationshipService = relationshipService;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "/add-in-friends", method = RequestMethod.POST)
    public ResponseEntity<String> addRelationship(HttpSession session, String userIdFrom, String userIdTo){

        try {
            User userFrom = userDAO.findById(userIdFrom);
            User userTo = userDAO.findById(userIdTo);

            if (userTo != null && userFrom != null && session.getAttribute(userFrom.getEmail()) != null) {
                Relationship relationship = new Relationship();
                relationship.setUserFrom(userFrom);
                relationship.setUserTo(userTo);
                relationship.setStatusType(RelationshipStatusType.FRIENDS);
                relationshipService.save(relationship);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else if (userTo != null && userFrom != null && session.getAttribute(userFrom.getEmail()) == null){
                return new ResponseEntity<>("UserFrom ID " + userIdFrom + " is not in the system.", HttpStatus.BAD_REQUEST);
            }
            else if (userTo == null){
                return new ResponseEntity<>("UserTo ID " + userIdTo + " not found in the database.", HttpStatus.BAD_REQUEST);
            }
            else {
                return new ResponseEntity<>("UserFrom ID " + userIdFrom + " not found in the database.", HttpStatus.BAD_REQUEST);
            }
        } catch (InternalServerError e) {
            return new ResponseEntity<>("Something went wrong...", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException e) {
            return new ResponseEntity<>("This user can not registered.", HttpStatus.BAD_REQUEST);
        }
    }

    public void updateRelationship(String userIdFrom, String userIdTo, String status){

    }

    public List getIncomeRequests(String userId){


        return null;
    }

    public List getOutcomeRequests(String userId){


        return null;
    }

    public void setRelationshipService(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
