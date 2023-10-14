# Dynamic Full-Stack Web Application

Welcome to a compelling full-stack web application, carefully crafted to demonstrate the synergy of technology and innovation. 🚀

## Overview
This repository is a showcase of a dynamic full-stack web application, powered by the Java Spring framework for the robust backend and React.js for the interactive frontend. It offers:

- **JWT User Authentication and Authorization** for a secure user experience.
- Seamless execution of **CRUD Operations** supported by essential Spring framework libraries:
   - **Spring Security**
   - **Spring Web**
   - **Spring Data JPA**
   - **Spring JDBC** on the server side.
- A modern and user-friendly frontend leveraging **React**, **ReactRouter**, and the elegant **Chakra** framework.
- **Postgres** as the dependable database of choice.
- Comprehensive **Unit** and **Integration Tests** for reliability and stability.
- Full compatibility with **CI/CD** pipelines for automated builds and deployments.

## Deployment
- The **Backend** is effortlessly deployable on **AWS Elastic Beanstalk**, providing scalability and ease of management.
- The **Frontend** is ready for automated deployment through **AWS Amplify**, ensuring a seamless user experience.
- Profile pictures are stored efficiently using **AWS S3**, enhancing the visual appeal and user interactions.

Feel free to clone and deploy this application yourself, or contribute to this open-source gem. 🌟

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Backend (Spring Framework)](#backend-spring-framework)
    - [Frontend (React)](#frontend-react)
- [Configuration](#configuration)
- [Usage](#usage)


## Prerequisites

Before you begin, ensure you have met the following requirements:

- JDK 17 (both Java 8 and 11 also supported), Node.js v18 or above
- Docker installed in your machine and postgres v15.3 or above running in a container.

## Getting Started

To get this project up and running, follow the steps below for both the backend (Spring Framework) and frontend (React) components.

### Backend (Spring Framework)

#### To get running backend:

1. Clone the repository:

   ```bash
   git clone https://github.com/msbeigiai/spring-full-stack.git
    ```
2. Navigate to the backend directory:
   ```bash
    cd spring-full-stack
    ```
   to run the application there are 2 alternative ways:
    - 1. running each one individually
    - 2. or by deploying to docker hub and running _docker compose_. \
*I suggest the second option because both backend and frontend will run in a single command*
3. Navigate to the backend folder and run:
    ```bash
   mvn clean deploy jib:build
   ```
   this command will deploy the latest backend in Docker hub and later can be run in `docker compose`.

### Frontend (React)
#### To get running frontend:
1. Navigate back to frontend, then navigate to react folder and run:
    ```bach
   cd ../frontend/react
   docker build . -t msbeigiai/msbeigi-react
   docker push msbeigiai/msbeigi-react
    ```
   this will also push latest version of frontend into Docker hub.
2. To boot up the entire project navigate back to the project root and then run docker compose command:
    ```bash
   cd spring-boot-example/
   docker compose up -d
    ```

### Configuration
1. In case you see error booting up backend just navigate inside `postgres` database by running:
```bash
docker exec -it postgres bash
```
and then run:
```bash
psql -U msbeigi
```
while you are prompted for password, supply `password`.\
inside postgres database create `customer` database by writing:
```sql
CREATE DATABASE customer;
```
by now your application must be up and running.

### Usage
By configuring the project as above guidelines, project can run perfectly.

### If you have any question feel free to ask me.
