# Wichteln
## What is it?
A demo/testbed for various technologies disguised as a Secret Santa (German: "Wichteln") web application:
- Spring Boot
- Spring MVC + Thymeleaf
- JUnit 5 + Testcontainers
- HTML

## How to try it out?
### a) Web
Navigate to https://wichteln-app.herokuapp.com (N.B.: Initial load might take a while in case of cold starts under the Heroku free plan)
### b) On your machine
Make sure you have `docker` and `docker-compose` installed. Then
1) Clone this repo
2) Change to the repository's root directory
3) Run `docker-compose -f docker-compose-local.yml up`
4) Navigate to http://localhost:8080
