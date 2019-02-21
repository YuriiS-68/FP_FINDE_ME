package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.User;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

@Repository("userDAO")
@Transactional
public class UserDAO extends GeneralDAO<User> {

    private static final String FIND_USER_WITH_FIELDS_EMAIL_AND_PHONE = "SELECT * FROM USER_FM WHERE PHONE = ? OR EMAIL = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM USER_FM WHERE EMAIL = ?";

    @SuppressWarnings("unchecked")
    public boolean findUserByFields(User user) throws InternalServerError {
        NativeQuery<User> query = (NativeQuery<User>) getEntityManager().createNativeQuery(FIND_USER_WITH_FIELDS_EMAIL_AND_PHONE, User.class);
        try {
            user = query.setParameter(1, user.getPhone()).setParameter(2, user.getEmail()).uniqueResult();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }
        return user == null;
    }

    @SuppressWarnings("unchecked")
    public User findUserByEmail(User user) throws InternalServerError {
        NativeQuery<User> query = (NativeQuery<User>) getEntityManager().createNativeQuery(FIND_USER_BY_EMAIL, User.class);
        try {
            user = query.setParameter(1, user.getEmail()).uniqueResult();
        }catch (NoResultException e){
            System.err.println(e.getMessage());
            throw e;
        }
        if (user != null){
            return user;
        }
        else {
            return null;
        }
    }
}
