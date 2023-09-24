# Spring Full Stack

## Overview

This is a full-stack web application that combines a 
Spring Framework backend with a React frontend. 
It provides customer registration, see all customer, customer
authentication and authorization by using most common Spring 
framework libraries **Spring Security, Spring Web, Spring Data JPA,
Spring JDBC** for backend and **React**, **ReactRouter** 
and **Chakra** technologies for frontend, and also **Postgres** as a database. \
All the `Unit tests` and `integration tests` are implemented and the application \
is fully **CI/CD** compatible. \
The backend is fully automated deployable 

in **AWS Elastic BeansTalk** and frontend is fully automated deployable in **AWS Amplify**.
This is a full-stack web application that combines a Spring Framework backend with a React frontend. It provides customer registration, see all customer, customer authentication and authorization by using most common Spring framework libraries **Spring Security, Spring Web, Spring Data JPA, Spring JDBC**.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Backend (Spring Framework)](#backend-spring-framework)
    - [Frontend (React)](#frontend-react)
- [Configuration](#configuration)
- [Usage](#usage)
- [Development](#development)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

Before you begin, ensure you have met the following requirements:

- JDK 17 (both Java 8 and 11 also supported), Node.js v18 or above
- Docker installed in your machine and postgres v15.3 or above running in a container.

## Getting Started

To get this project up and running, follow the steps below for both the backend (Spring Framework) and frontend (React) components.

### Backend (Spring Framework)

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
    - 2. or by deploying to docker hub and running _docker compose_
    _ I would suggest the second option because both backend and frontend will run in a single command.
3. Navigate to the backend folder and run:
    ```bash
   mvn clean deploy jib:build
   ```
   this will deploy the newest backend in Docker hub and later can be run in `docker compose`.
4. Navigate back to frontend, then react and run:
    ```bach
   cd ../frontend/react
   docker build . -t msbeigiai/msbeigi-react
   docker push msbeigiai/msbeigi-react
    ```
   this will also pushes newest version of frontend into Docker hub.
5. To boot up the entire project navigate back to the project root and then run docker compose command:
    ```bash
   cd spring-boot-example/
   docker compose up -d
    ```
   
6- In case you see error booting up backend just navigate inside `postgres` database by running:
```bash
docker exec -it postgres bash
```
and then run:
```bash
psql -U msbeigi
```
while your are prompted for password, supply `password` as password.\
inside postgres database create `customer` database by querying:
```sql
CREATE DATABASE customer;
```
by now your application must be up and running.

If you have any question feel free to ask me.
