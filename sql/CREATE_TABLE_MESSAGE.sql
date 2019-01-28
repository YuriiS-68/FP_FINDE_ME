CREATE TABLE MESSAGE(
 MESSAGE_ID NUMBER NOT NULL PRIMARY KEY,
 ID_USER_FROM NUMBER,
 CONSTRAINT USER_FROM_FK FOREIGN KEY(ID_USER_FROM)REFERENCES USER_FM(USER_ID),
 ID_USER_TO NUMBER,
 CONSTRAINT USER_TO_FK FOREIGN KEY(ID_USER_TO)REFERENCES USER_FM(USER_ID),
 TEXT_MESSAGE NVARCHAR2(800),
 DATE_SENT TIMESTAMP NOT NULL,
 DATE_READ TIMESTAMP NOT NULL
);

CREATE SEQUENCE MES_SEQ MINVALUE 1 MAXVALUE 10000 START WITH 1 INCREMENT BY 1;