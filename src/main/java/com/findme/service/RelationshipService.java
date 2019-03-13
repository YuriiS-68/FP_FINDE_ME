package com.findme.service;

import com.findme.dao.RelationshipDAO;
import com.findme.exception.BadRequestException;
import com.findme.models.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void update(Relationship relationship)throws BadRequestException{
        if (relationship == null){
            throw new BadRequestException("Relationship is not exist");
        }
        relationshipDAO.update(relationship);
    }

    public List<Relationship> getIncomeRequests(String userId)throws BadRequestException{
        if (userId == null){
            throw new BadRequestException("Input data is wrong.");
        }

        return null;
    }

    public List<Relationship> getOutcomeRequests(String userId)throws BadRequestException{
        if (userId == null){
            throw new BadRequestException("Input data is wrong.");
        }

        return null;
    }

    public void setRelationshipDAO(RelationshipDAO relationshipDAO) {
        this.relationshipDAO = relationshipDAO;
    }
}
