CREATE TABLE RELATIONSHIP(
RELATIONSHIP_ID NUMBER NOT NULL PRIMARY KEY,
ID_USER_FROM NUMBER,
CONSTRAINT RELATIONSHIP_USER_FROM_FK FOREIGN KEY (ID_USER_FROM)REFERENCES USERS1(USER_ID),
ID_USER_TO NUMBER,
CONSTRAINT RELATIONSHIP_USER_TO_FK FOREIGN KEY (ID_USER_TO)REFERENCES USERS1(USER_ID),
STATUS_TYPE NVARCHAR2(30) NOT NULL CHECK (STATUS_TYPE = 'FRIEND_REQUEST' OR
STATUS_TYPE = 'REQUEST_REJECTED' OR STATUS_TYPE = 'REMOVED_FROM_FRIENDS' OR
STATUS_TYPE = 'FRIENDS')
);

CREATE SEQUENCE RELATIONSHIP_SEQ MINVALUE 1 MAXVALUE 10000 START WITH 1 INCREMENT BY 1;
DROP SEQUENCE RELATIONSHIP_SEQ;