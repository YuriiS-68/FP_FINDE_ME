package com.findme.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;
import java.util.StringJoiner;

public class PostInfo {
    private Long idUserPosted;
    private Long idUserPagePosted;

    public PostInfo(Long idUserPosted, Long idUserPagePosted) {
        this.idUserPosted = idUserPosted;
        this.idUserPagePosted = idUserPagePosted;
    }

    @JsonCreator
    public static PostInfo createFromJson(String jsonString){
       ObjectMapper objectMapper = new ObjectMapper();
       PostInfo postInfo = null;

       try {
           postInfo = objectMapper.readValue(jsonString, PostInfo.class);
       }catch (IOException e){
           e.printStackTrace();
       }
       return postInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostInfo postInfo = (PostInfo) o;
        return idUserPosted.equals(postInfo.idUserPosted) &&
                idUserPagePosted.equals(postInfo.idUserPagePosted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUserPosted, idUserPagePosted);
    }

    public Long getIdUserPosted() {
        return idUserPosted;
    }

    public void setIdUserPosted(Long idUserPosted) {
        this.idUserPosted = idUserPosted;
    }

    public Long getIdUserPagePosted() {
        return idUserPagePosted;
    }

    public void setIdUserPagePosted(Long idUserPagePosted) {
        this.idUserPagePosted = idUserPagePosted;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PostInfo.class.getSimpleName() + "[", "]")
                .add("idUserPosted=" + idUserPosted)
                .add("idUserPagePosted=" + idUserPagePosted)
                .toString();
    }
}
