
# Microservice for Posts management with external RESTful API support

The goal of this microservice (MS) is to mediate services of external RESTful API  to manage user posts.

External RESTful API web: [https://jsonplaceholder.typicode.com/](https://jsonplaceholder.typicode.com/)

MS behavior is very similar to external RESTful API.

Whole backend MS is built on:
- **Java (17.0.3)**
- **Spring Boot (3.0.4)**
- **Gradle(7.5.1)**
- **PostgreSQL**.

The MS solves basic CRUD operations.

## Table of Content
* [Settings](#settings)
  * [Environment variables](#environment-variables)
* [Features](#features)
  * [Add post](#add-post)
  * [List post by Id](#list-post-by-id)
  * [List posts by userId](#list-posts-by-userid)
  * [Update post by Id](#update-post-by-id)
  * [Delete post by Id](#delete-post-by-id)
* [Docker](#docker) 

## Settings

### Environment variables

- #### DB URL: ``SPRING_DATASOURCE_URL``

Whole url including DB name, for example: ``jdbc:postgresql://localhost:5432/be-ms-tasks``

- #### DB user name: ``SPRING_DATASOURCE_USERNAME``

- #### DB password: ``SPRING_DATASOURCE_PASSWORD``

- #### External RESTful API URL: ``APIURL``

### Configurations

- #### MS listen on port: `8080`

## Features

### Add post

The MS:
- checks, if userId exists on external API.
- adds post into external API
- adds post into internal DB

#### Good case:

```
REQUEST:
POST /posts
Accept: aplication/json
Content-type: aplication/json

Body:
{
    "title": "foo",
    "body": "bar",
    "userId": 1
}

RESPONSE: HTTP 200
Body:
{
    "title": "foo",
    "body": "bar",
    "userId": 1
}
```

#### Bad case: User doesn't exist

```
RESPONSE: HTTP 400
Body:
{
    "error": "Wrong User ID"
}
```

#### Bad case: Other External API error

```
RESPONSE: HTTP 503
Body:
{
    "error": "External API Error"
}
```
[Back to Table of Content](#table-of-content)

### List post by Id

The MS:
- checks internal DB for asked Id and response with content
- if post doesn't exist in internal DB:
  - asks external API
  - if post exists on external API, MS saves post into internal DB and returns content

#### Good case:

```
REQUEST:
GET /posts/{id}
Accept: aplication/json
Content-type: aplication/json

RESPONSE: HTTP 200
Body:
{
    "Id": 1,
    "title": "foo",
    "body": "bar",
    "userId": 1
}
```

#### Bad case: Post with required Id doesn't exist

```
RESPONSE: HTTP 404
Body:
{
    "error": "Post Not Found"
}
```

#### Bad case: Other External API error

```
RESPONSE: HTTP 503
Body:
{
    "error": "External API Error"
}
```
[Back to Table of Content](#table-of-content)

### List posts by userId

The MS:
- checks internal DB and external API for posts with asked userId
- if no posts are found, the MS just returns empty list

#### Good case:

```
REQUEST:
GET /posts?userId={userId}
Accept: aplication/json
Content-type: aplication/json

RESPONSE: HTTP 200
Body:
[
    {
        "id": 2,
        "userId": 1,
        "title": "qui est esse",
        "body": "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
    },
    {
        "id": 1,
        "userId": 1,
        "title": "foo1",
        "body": "shdkhjsdkj dhsk"
    },
    ... 
]
```

#### Bad case: Other External API error

```
RESPONSE: HTTP 503
Body:
{
    "error": "External API Error"
}
```

[Back to Table of Content](#table-of-content)

### Update post by Id

The MS:
- updates title or body in external API
- if success, updates post in internal DB

#### Good case:

```
REQUEST:
PUT /posts/{id}
Accept: aplication/json
Content-type: aplication/json

Body:
{
    "title": "foo X",
    "body": "bar X"
}
or
{
    "title": "foo X"
}
or
{
    "body": "bar X"
}

RESPONSE: HTTP 200
Body:
{
    "id": 1,
    "title": "foo X",
    "body": "bar X",
    "userId": 1
}
```

#### Bad case: Post doesn't exist

```
RESPONSE: HTTP 404
Body:
{
    "error": "Post Not Found"
}
```

#### Bad case: Other External API error

```
RESPONSE: HTTP 503
Body:
{
    "error": "External API Error"
}
```

[Back to Table of Content](#table-of-content)


### Delete post by Id

The MS:
- deletes post in external API
- if success, deletes post in internal DB

#### Good case:

```
REQUEST:
DELETE /posts/{id}
Accept: aplication/json
Content-type: aplication/json

RESPONSE: HTTP 200
```

#### Bad case: Other External API error

```
RESPONSE: HTTP 503
Body:
{
    "error": "External API Error"
}
```


[Back to Table of Content](#table-of-content)

## Docker

Step 1 - to build docker image run ``$ docker build ./ -t springbootapp``

Step 2 - to run app and db: ``$ docker-compose up``.

MS listen on port 8080

[Back to Table of Content](#table-of-content)
