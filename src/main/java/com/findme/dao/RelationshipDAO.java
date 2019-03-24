package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository("relationshipDAO")
@Transactional
public class RelationshipDAO extends GeneralDAO<Relationship> {

    private static final String GET_RELATIONSHIP = "SELECT * FROM RELATIONSHIP WHERE ID_USER_FROM = ? AND ID_USER_TO = ?";

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
}
