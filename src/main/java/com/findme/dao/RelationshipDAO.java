package com.findme.dao;

import com.findme.models.Relationship;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("relationshipDAO")
@Transactional
public class RelationshipDAO extends GeneralDAO<Relationship> {

}
