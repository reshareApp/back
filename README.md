# ReShare Backend Application

## Setup 
### 1. How to run locally : 
`localhost:8080`

### 2. Connecting to db local :

On your **PSQL** shell :
    
    create databse reshare_db ;
    
    create user reshare_pg with encrypted password 'Reshare_123';
    
    grant all privileges on database reshare_db to reshare_pg;

**OR** you can use pgAdmin interface to create user and set all privileges to it for the db you created .

### 3. Dependencies :

1. JWT for authentication tokens.
2. Spring Security for secure our endpoints
3. JPA for accessing Db
4. Postgres Driver
5. OpenApi for documenting APIs
6. Using yaml properties file (more structured and well organized + new experience)