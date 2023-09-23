# CBS APP
## A mini Core Banking System

## Table of Contents

- [Features](#features)
- [Building and Running the application](#building-and-running-the-application)
- [Endpoints Overview](#endpoints-overview)
- [Test Reports](#test-reports)
- [Feedbacks](#feedbacks)

## Features

- **A core banking system**: Make and keep track of monetary transactions.
- **Asynchronous architecture**: Never lose any updates, guaranteed alerts.

## Building and Running the application

1. Navigate to the project root:
   ```bash
   cd cbs
   ```

2. Build the jar file using gradle:
   ```bash
   ./gradlew clean build
   ```

3. Navigate away from project root:
   ```bash
   cd ..
   ```

4. Build a Docker image for the application:
   ```bash
   docker build -t cbs .
   ```
   
5. Start the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```

6. Shutdown the application using Docker compose
   ```bash
   docker-compose down -v
   ```
   Database tables will be created automatically

## Endpoints Overview

1. To see the API doc use the following [link](http://localhost:8080/swagger-ui/index.html) when the app is running.

2. To see the message queues use the following [link](http://localhost:15672/#/queues) when the app is running.
   Here are the rabbitmq login details (if you're asked for it):
   username: `guest`
   password: `guest`

## Test Reports
   Build test reports can be found on this [page](https://htmlpreview.github.io/?https://github.com/De-Bola/cbs/blob/master/cbs/build/reports/tests/test/index.html)
   
   ![Screenshot of test coverage from intellij](./test_coverage.png)

   Screenshot of test coverage from intellij

   Coverage test reports can be found on this [page](https://htmlpreview.github.io/?https://github.com/De-Bola/cbs/blob/master/coverage/reports/index.html)
   
## Feedbacks
   Here are the feedbacks I got for this task:
#### Likes
1. Nice project setup
2. Nice error handling and error response 
3. Validations are done 
4. Correct data types 
5. Nice use of enums 
6. I like that there are integration test

#### Dislikes
1. ~~Could have created two mappers/repositories separately for accounts and transactions~~ 
2. ~~SuccessResponse could have used generic for data to have better type definition~~ 
3. ~~What can be the dark side of using Lombok too extensively?~~ 
4. Endpoints are not REST-ful 
5. ~~System out is not really the best approach on logging...~~ 
6. ~~Too much logic on controller level~~ 
7. ~~No locking... What would happen in case of concurrent same account transactions?~~ 
8. ~~Much easier and cleaner would be to use db sequence for unique id instead of a central Java function.~~ 
9. ~~And even if you use Java function, create a Util class with static function for it instead of having it part of a certain service...~~ 
10. ~~Quite a few TODOs still around the project and a lot of comments, which is not considered clean code.~~ 
11. Test coverage is not met (while the services are covered quite nicely, you have quite a bit of code on controller level, which is not covered at all)
   

## Way forward
- Fix the dislikes majorly and handle them per commit.
- Implemented Dislike 9 instead of 8.