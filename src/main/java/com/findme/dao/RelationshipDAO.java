package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.Relationship;
import com.findme.models.RelationshipStatusType;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

@Repository("relationshipDAO")
@Transactional
public class RelationshipDAO extends GeneralDAO<Relationship> {

    private static final String GET_RELATIONSHIP = "SELECT * FROM RELATIONSHIP WHERE ID_USER_FROM = ? AND ID_USER_TO = ?";
    private static final String GET_COUNT_ROWS_FRIENDS = "SELECT COUNT(s) FROM Relationship s WHERE s.userTo.id = :idParam AND s.statusType = :statusParam";
    private static final String GET_COUNT_ROWS_REQUESTS = "SELECT COUNT(s) FROM Relationship s WHERE s.userFrom.id = :idParam AND s.statusType = :statusParam";
    private static final String GET_DATE_ACCEPTED = "SELECT acceptedFriends FROM Relationship s WHERE s.userFrom.id = :idParam AND s.statusType = :statusParam";

    public int getQuantityHoursAfterAccepted(Long idUser, RelationshipStatusType status)throws InternalServerError{
        Date dateAccepted;
        Date currentDate = new Date();

        try {
            dateAccepted = getEntityManager().createQuery(GET_DATE_ACCEPTED, Date.class).setParameter("idParam", idUser)
                    .setParameter("statusParam", status)
                    .getSingleResult();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }

        long difference = currentDate.getTime() - dateAccepted.getTime();
        return  (int)(difference / (24 * 60 * 60 * 1000));
    }

    public Long getQuantityFriends(Long idUser, RelationshipStatusType status) throws InternalServerError{
        return getEntityManager().createQuery(GET_COUNT_ROWS_FRIENDS, Long.class).setParameter("idParam", idUser)
                .setParameter("statusParam", status)
                .getSingleResult();
    }

    public Long getQuantityRequests(Long idUser, RelationshipStatusType status) throws InternalServerError{
        return getEntityManager().createQuery(GET_COUNT_ROWS_REQUESTS, Long.class).setParameter("idParam", idUser)
                .setParameter("statusParam", status)
                .getSingleResult();
    }

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
        return relationship;
    }
}
