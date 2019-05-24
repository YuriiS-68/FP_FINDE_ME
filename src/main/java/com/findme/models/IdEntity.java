package com.findme.models;

import javax.persistence.Transient;

public class IdEntity {
    private Long id;

    @Transient
    public Long getId() {
        return id;
    }
}
