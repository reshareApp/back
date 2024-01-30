# ReShare Backend Application

## Setup 
### 1. How to run locally : 
`localhost:8080`

### 2. Connecting to local Database -PostgreSQL- :

- **Docker Compose :** is a tool for defining and running multi-container Docker applications. We use it here to define instance of PostgreSQL Database for applying the same approach for every user when trying to connect to the same database _locally_ .
- In the project root directory you should _**up**_ the docker compose which _**runs**_ all services,networks,volumes ..etc attached on it.
- To _**up**_ the docker compose you should use this command in your terminal : `docker-compose up -d`
- And if you want to stop your database container/instance you can stop the docker compose itself by using : `docker-compose down -v`

### 3. Dependencies :

1. JWT for authentication tokens.
2. Spring Security for secure our endpoints
3. JPA for accessing Db
4. Postgres Driver
5. OpenApi for documenting APIs
6. Using yaml properties file (more structured and well organized + new experience)