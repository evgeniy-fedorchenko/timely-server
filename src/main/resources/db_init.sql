CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    name     varchar(128) NOT NULL,
    position varchar(32)  NOT NULL,
    rate     smallint     NOT NULL
);

CREATE TABLE user_details
(
    id          UUID PRIMARY KEY,
    username    varchar(64) UNIQUE NOT NULL,
    password    varchar(64)        NOT NULL,
    authorities varchar(32),
    FOREIGN KEY (id) REFERENCES users (id)
);
