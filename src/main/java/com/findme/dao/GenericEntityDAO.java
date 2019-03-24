package com.findme.dao;

import com.findme.exception.InternalServerError;

import java.io.Serializable;

interface GenericEntityDAO<T> {
    T findById(Serializable id)throws InternalServerError;

    void update(T t)throws InternalServerError;
}
