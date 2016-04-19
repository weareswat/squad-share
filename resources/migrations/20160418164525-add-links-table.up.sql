CREATE SCHEMA IF NOT EXISTS squadshare;

CREATE TABLE IF NOT EXISTS squadshare.links
(id SERIAL PRIMARY KEY,
 title VARCHAR(50),
 url VARCHAR(100),
 description VARCHAR(255),
 created_at TIMESTAMP);
