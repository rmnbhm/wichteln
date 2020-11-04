# Wichteln
## What is it?
A demo/testbed for various technologies disguised as a Secret Santa (German: "Wichteln") application:
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Security + OAuth
- JUnit 5 + Testcontainers
- HTML 5
- SQL

## How to try it out?
### a) Web
Navigate to http://wichteln.rmnbhm.com
### b) On your machine
Make sure you have `docker` and `docker-compose` installed. Then
1) Clone this repo
2) Change to the repository's root directory
3) Run `docker-compose up`
4) Navigate to http://localhost:8080

## Upcoming Features
- [x] An actual UI
- [x] HTTPS access
- [ ] A nice looking, mobile friendly UI
- [ ] User accounts and, with it, wichtel events administration (necessitates a database)
