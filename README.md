﻿# inconvenio-webshop-authentication-service
This is the Authentication Microservice, responsible for managing user authentication and access to various features such as adding items to the cart, removing items from the cart, and updating the cart. It provides a RESTful API to handle user registration, login, token verification, and event publishing.
Features

    User registration
    User login
    Token verification
    Publish Add to Cart event
    Publish Remove from Cart event
    Publish Update Cart event

Requirements

    Java 11+
    Spring Boot
    Docker

Installation

Clone the repository:



    git clone https://github.com/your-username/authentication-microservice.git




## Usage
Navigate to the project directory



To start the Authentication Microservice using Docker, run the following command:



    docker-compose up

This will start the microservice and any required infrastructure, such as a database, in separate containers. The API will be accessible on http://localhost:8089.

### API Endpoints

    POST /user/create: Create a new user
    POST /user/login: Authenticate a user and generate an access token
    POST /user/add-to-cart: Publish an Add to Cart event
    POST /user/remove-from-cart: Publish a Remove from Cart event
    POST /user/update-cart: Publish an Update Cart event
