package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.User;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

@Repository("relationshipDAO")
@Transactional
public class RelationshipDAO extends GeneralDAO<Relationship> {

    private static final String GET_RELATIONSHIP = "SELECT * FROM RELATIONSHIP WHERE ID_USER_FROM = ? AND ID_USER_TO = ?";
    private static final String GET_LIST_OUTCOME = "SELECT * FROM RELATIONSHIP WHERE ID_USER_FROM = ?";
    private static final String GET_LIST_INCOME = "SELECT * FROM RELATIONSHIP WHERE ID_USER_TO = ?";

    @SuppressWarnings("unchecked")
    public Relationship getRelationship(Long idUserFrom, Long idUserTo)throws InternalServerError {
        Relationship relationship;
        NativeQuery<Relationship> query = (NativeQuery<Relationship>) getEntityManager().createNativeQuery(GET_RELATIONSHIP, Relationship.class);
        try {
            relationship = query.setParameter(1, idUserFrom).setParameter(2, idUserTo).uniqueResult();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }

        if (relationship != null){
            return relationship;
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Relationship> getOutcomeRelationships(String userId)throws InternalServerError{
        List<Relationship> relationshipList;
        NativeQuery<Relationship> query = (NativeQuery<Relationship>) getEntityManager().createNativeQuery(GET_LIST_OUTCOME, Relationship.class);
        try {
            relationshipList = query.setParameter(1, userId).getResultList();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return relationshipList;
    }

    /*@SuppressWarnings("unchecked")
    public List<Relationship> getIncomeRelationships(String userId)throws InternalServerError{
        List<Relationship> relationshipList;
        NativeQuery<Relationship> query = (NativeQuery<Relationship>) getEntityManager().createNativeQuery(GET_LIST_INCOME, Relationship.class);
        try {
            relationshipList = query.setParameter(1, userId).getResultList();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return relationshipList;
    }*/
}
