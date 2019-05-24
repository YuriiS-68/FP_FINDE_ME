package com.findme.models;

public enum RelationshipStatusType {
    REQUESTED("REQUESTED"),
    CANCELED("CANCELED"),
    DECLINED("DECLINED"),
    DELETED("DELETED"),
    ACCEPTED("ACCEPTED");

    private String value;

    RelationshipStatusType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return this.getValue();
    }
}
