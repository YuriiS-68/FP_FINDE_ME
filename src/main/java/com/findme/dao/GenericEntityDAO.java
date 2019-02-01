package com.findme.dao;

import java.io.Serializable;

interface GenericEntityDAO<T> {
    T findById(Serializable id);

    void update(T t);
}
