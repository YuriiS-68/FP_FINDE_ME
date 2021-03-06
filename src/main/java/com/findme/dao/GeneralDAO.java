package com.findme.dao;

import com.findme.exception.InternalServerError;
import com.findme.models.IdEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

@Repository
@Transactional
public abstract class GeneralDAO<T extends IdEntity> implements GenericEntityDAO<T> {

    @PersistenceContext
    private EntityManager entityManager;
    private Class<T> type;

    @SuppressWarnings("unchecked")
    public GeneralDAO(){
        this.type = ((Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public T save(T entity){
        entityManager.persist(entity);
        return entity;
    }

    public void update(T t)throws InternalServerError{
        entityManager.merge(t);
    }

    public void delete(Long id)throws InternalServerError{
        entityManager.remove(findById(id));
    }

    public T findById(Serializable id) throws InternalServerError {
        return entityManager.find(getType(), id);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
