# Documentation

## Docker

In order to make easy testing the application, a `docker-compose.yml` file has been created in `/server/src/main/docker` directory 
and it includes our server application, our postgres database and keycloak images, already configured and ready-to-use.

You can move to `/server/src/main/docker` directory and execute the following command on terminal to pull our services images:

```bash
docker-compose pull
```

Then you will be able to run the following command to start services:

```bash
docker-compose up
```

Note that:
- server application runs on `http://localhost:8080`
- keycloak runs on `http://localhost:8081`
- postgres database runs on `http://localhost:5432`

### Observability

Also within `/server/src/main/docker`, you can find the `docker-compose.observability.yml` file.
In order to run also observability services, i.e. Tempo, Loki, Prometheus and Grafana, you have to execute also the following command:

```bash
docker-compose -f docker-compose.observability.yml up
```

Note that you could see error logs on the Spring Boot server output console if you don't run observability services.
In order to disable observability on the server application you can edit the `docker-compose.yml` file with the following `environment` for `server` service:

```yml
services:
  server:
    image: 'gcr.io/g5project-385811/server:latest'
    
    # Don't change here
    
    environment:
      
      # Don't change here
      
      - LOKI_ENABLED=false # <--- Change this one from true to false
      - MANAGEMENT_TRACING_ENABLED=false # <--- Change this one from true to false
    env_file:
      - ./config/.env.docker
```

Then you can start services with no error logs using the command above.


### User Credentials

If you have pulled our images, you will be able to use the following user credentials to perform login:

| username  | password | role     |
|-----------|----------|----------|
| customer1 | customer | Customer |
| customer2 | customer | Customer |
| expert1   | expert   | Expert   |
| expert2   | expert   | Expert   |
| manager1  | manager  | Manager  |

## Server APIs

### Products APIs

#### **`GET /api/public/products`**

List all registered products in the database

**Request header:**

- `Content-Type: application/json`
- `[Authorization: Bearer <access_token>]`

**Response body**

`HTTP status code 200 OK`

```json
[
    {
        "ean": 4935531465706,
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    }
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)

---

#### **`GET /api/public/products/:ean`**

Details of product associated with the provided ean or fail if it does not exist

**Request header:**

- `Params: req.params.ean to obtain the required ean`
- `Content-Type: application/json`
- `[Authorization: Bearer <access_token>]`

**Response body**

`HTTP status code 200 OK`

```json
{
    "ean": 4935531465706,
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)

---

### Profiles APIs

#### **`GET /api/customer/profiles/:email`**

Details of user profiles associated with the provided email or fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email `
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

JSON object containing username e password.

```json
{
    "name": "user",
    "surname": "surname1" ,
    "email": "user1@studenti.polito.it"
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Profile Not Found Exception)

---

#### **`PUT api/customer/profiles/:email`**

Edit a field of a specific profile associated with the email and fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

A JSON object containing the data of the changes to be made

```json
{
    "name": "user",
    "surname": "surname"
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
    "name": "test",
    "surname": "surname" ,
    "email": "user2@studenti.polito.it"
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Profile Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---

### Ticket APIs

#### **`POST /api/customer/tickets`**

Stores a new ticket into the database for a given product


**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

JSON object containing title, description, productEAN, specializationId

```json
{
    "title": "Broken phone", 
    "description": "The phone is not turning on since yesterday",
    "productEAN": 4935531465706, 
    "specializationId": 1
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "status": "OPEN",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-03T14:24:45.173+00:00",
    "closedDate": null,
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}
```

**Error responses**
- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)
- `HTTP status code 404 Not Found` (Specialization Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---


#### **`PATCH /api/customer/tickets/:id/cancel`**

Sets to CANCELLED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "status": "CANCELLED",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-03T14:24:45.173+00:00",
    "closedDate": "2023-05-03T14:33:35.494+00:00",
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}
```

**Error responses**
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/{expert,manager}/tickets/:id/close`** 

Sets to CLOSED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "status": "CLOSED",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-03T14:24:45.173+00:00",
    "closedDate": "2023-05-03T14:33:35.494+00:00",
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}
```

**Error responses**
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/customer/tickets/:id/reopen`**

Sets to REOPENED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "status": "REOPENED",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-03T14:24:45.173+00:00",
    "closedDate": "2023-05-03T14:33:35.494+00:00",
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}
```

**Error responses**
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/manager/tickets/:id/start`**

Sets to IN_PROGRESS the status field of a specific ticket associated with the id provided and fails if the id does not exist.
Automatically assigning the expert with the lowest "is working on" and the related specialization after having specified its level of priority(between 0 and 3).

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

JSON object containing priorityLevel

```json
{
    "priorityLevel": 2
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": 1,
  "status": "IN_PROGRESS",
  "title": "Keyboard not working",
  "description": "The keyboard doesn't react to the key pressing",
  "customer": {
    "name": "Test",
    "surname": "Test",
    "email": "test@test.com"
  },
  "expert": {
    "specializations": [
      {
        "id": 1,
        "name": "COMPUTER"
      },
      {
        "id": 2,
        "name": "MOBILE"
      }
    ],
    "workingOn": 1
  },
  "priorityLevel": "HIGH",
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-02T09:32:18.050+00:00",
  "closedDate": null,
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  }
}

```

**Error responses**
- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---


#### **`PATCH /api/expert/tickets/:id/stop`**

Sets to OPEN the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": 2,
  "status": "OPEN",
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "customer": {
    "name": "Test",
    "surname": "Test",
    "email": "test@test.com"
  },
  "expert": {
    "specializations": [
      {
        "id": 1,
        "name": "COMPUTER"
      },
      {
        "id": 2,
        "name": "MOBILE"
      }
    ],
    "workingOn": 1
  },
  "priorityLevel": "MEDIUM",
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-02T09:32:18.050+00:00",
  "closedDate": null,
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  }
}

```

**Error responses**
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---
#### **`PATCH /api/{manager,expert}/tickets/:id/resolve`**

Sets to RESOLVED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "status": "RESOLVED",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-03T14:24:45.173+00:00",
    "closedDate": "2023-05-03T14:33:35.494+00:00",
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}


```

**Error responses**
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`GET /api/authenticated/tickets/:id`**

Retrieves the ticket with the given id

**Request header:**

- `Params: req.params.id to obtain the required id `
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

JSON object containing the info of the ticket.

`HTTP status code 200 OK`

```json
{
    "id": 1,
    "status": "RESOLVED",
    "title": "Keyboard not working",
    "description": "The keyboard doesn't react to the key pressing",
    "customer": {
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
        "ean": "4935531465706",
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    "createdDate": "2023-05-02T09:32:18.050+00:00",
    "closedDate": "2023-05-03T14:47:48.645+00:00",
    "specialization": {
        "id": 1,
        "name": "COMPUTER"
    }
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)

---

#### **`GET /api/manager/tickets?product={productEan}`**

Retrieves all the tickets related to the product id provided

**Request header:**

- `Params: req.params.productEan to obtain the required productEan `  
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

Array of JSON objects containing the information of the tickets associated with the product ID.

`HTTP status code 200 OK`

```json
[
    {
        "id": 2,
        "status": "CANCELLED",
        "title": "Broken phone",
        "description": "The phone is not turning on since yesterday",
        "customer": {
            "name": "Test",
            "surname": "Test",
            "email": "test@test.com"
        },
        "expert": null,
        "priorityLevel": null,
        "product": {
            "ean": "4935531465706",
            "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "brand": "JMT"
        },
        "createdDate": "2023-05-03T14:24:45.173+00:00",
        "closedDate": "2023-05-03T14:33:35.494+00:00",
        "specialization": {
            "id": 1,
            "name": "COMPUTER"
        }
    },
    {
        "id": 1,
        "status": "RESOLVED",
        "title": "Keyboard not working",
        "description": "The keyboard doesn't react to the key pressing",
        "customer": {
            "name": "Test",
            "surname": "TEST",
            "email": "test@test.com"
        },
        "expert": null,
        "priorityLevel": null,
        "product": {
            "ean": "4935531465706",
            "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
            "brand": "JMT"
        },
        "createdDate": "2023-05-02T09:32:18.050+00:00",
        "closedDate": "2023-05-03T14:47:48.645+00:00",
        "specialization": {
            "id": 1,
            "name": "COMPUTER"
        }
    }
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)

---

### Authentication APIs

#### **`POST /api/anonymous/auth/login`**

Perform login

**Request header:**
- `Content-Type: application/json`

**Request body:**

JSON object containing username, password

```json
{
    "username": "customer1",
    "password": "customer"
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "info": {
    "name": "Mario",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "details": {
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyNjViY2Y5ZS00ZGE4LTQ5NWYtYTcwYS1jNzAzZTg1M2YxNzcifQ.eyJleHAiOjE2ODU2OTg2MzQsImlhdCI6MTY4NTY5NjgzNCwianRpIjoiNTJmMDVjZmQtZWViMi00ZjIxLTgzMzMtMzJlYWMxODU3YjU2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy93YTJnMDVrZXljbG9hayIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9yZWFsbXMvd2EyZzA1a2V5Y2xvYWsiLCJzdWIiOiI3OWMxMjM0NC00MDkyLTQ2NjEtYjBiMy0yODc2ODUzNDBkN2IiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoid2EyZzA1a2V5Y2xvYWstY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjZkNDEyNGNhLTI3ZWUtNDhhYy1hYWRlLTVmYzlkZWU3M2E4OCIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjZkNDEyNGNhLTI3ZWUtNDhhYy1hYWRlLTVmYzlkZWU3M2E4OCJ9.d-LJhevcCljfcs9Zax7ItSkPeUQTiB7NWwDUa120il0",
    "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlS3ZKWFRjb25jbTNobnB5VVRJTzFlZm15QmIxRVJKYkFjbEIzZUxtSmV3In0.eyJleHAiOjE2ODU2OTcxMzQsImlhdCI6MTY4NTY5NjgzNCwianRpIjoiMTA1OGMwOWQtY2ViYS00NjQzLWFlNjItOGRhNDg1NThmMWM2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy93YTJnMDVrZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3OWMxMjM0NC00MDkyLTQ2NjEtYjBiMy0yODc2ODUzNDBkN2IiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ3YTJnMDVrZXljbG9hay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNmQ0MTI0Y2EtMjdlZS00OGFjLWFhZGUtNWZjOWRlZTczYTg4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtd2EyZzA1a2V5Y2xvYWsiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsid2EyZzA1a2V5Y2xvYWstY2xpZW50Ijp7InJvbGVzIjpbIkN1c3RvbWVyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI2ZDQxMjRjYS0yN2VlLTQ4YWMtYWFkZS01ZmM5ZGVlNzNhODgiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiY3VzdG9tZXIxIiwiZ2l2ZW5fbmFtZSI6IiIsImZhbWlseV9uYW1lIjoiIiwiZW1haWwiOiJjdXN0b21lcjFAbWFpbC5jb20ifQ.H015OUTrhtQjfZsNif7MrEeOQMYiSXQE0JYt1mZz5t-ObQ-Q-JI1e67easdzTMO1UQoVt-Z3DRPiy9eHtIVd9uSF2tDvJ7u9q4Svqn8Qf_VIKz52-duYqH5i330wACdmfwJ61JVCUlffaK77bDNag7tzBSdy8XctSIQKRkg9l74khrJc5igYyLLPuqR8S27b3ScI2INNcb-3ytqjFHmd5cJCxHuMzi2Rote6vJErG3y0aRUmTJMZTX97KcOpq-HpmqONfp7KJbeHwk7n9A0SCHv4gq7lC9sB5qBusDDArRb5g7c6v-o3UO9WLAFqtyDUII4Q66GSA4loRKQjf3fVTg",
    "uuid": "79c12344-4092-4661-b0b3-287685340d7b",
    "email": "customer1@mail.com",
    "username": "customer1",
    "authorities": [
      "Customer"
    ]
  }
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized ` (Invalid User Credentials Exception)     
- `HTTP status code 403 Forbidden ` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Employee or Profile Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---

#### **`POST /api/public/auth/refresh-token`**

Refresh authentication tokens.

**Request header:**
- `Content-Type: application/json`
- `[Authorization: Bearer <access_token>]`

**Request body:**

JSON object containing the refresh token

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyNjViY2Y5ZS00ZGE4LTQ5NWYtYTcwYS1jNzAzZTg1M2YxNzcifQ.eyJleHAiOjE2ODU2OTg2MzQsImlhdCI6MTY4NTY5NjgzNCwianRpIjoiNTJmMDVjZmQtZWViMi00ZjIxLTgzMzMtMzJlYWMxODU3YjU2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy93YTJnMDVrZXljbG9hayIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9yZWFsbXMvd2EyZzA1a2V5Y2xvYWsiLCJzdWIiOiI3OWMxMjM0NC00MDkyLTQ2NjEtYjBiMy0yODc2ODUzNDBkN2IiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoid2EyZzA1a2V5Y2xvYWstY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6IjZkNDEyNGNhLTI3ZWUtNDhhYy1hYWRlLTVmYzlkZWU3M2E4OCIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjZkNDEyNGNhLTI3ZWUtNDhhYy1hYWRlLTVmYzlkZWU3M2E4OCJ9.d-LJhevcCljfcs9Zax7ItSkPeUQTiB7NWwDUa120il0"
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJlS3ZKWFRjb25jbTNobnB5VVRJTzFlZm15QmIxRVJKYkFjbEIzZUxtSmV3In0.eyJleHAiOjE2ODU1Njc0MzYsImlhdCI6MTY4NTU2NzEzNiwianRpIjoiYzdkZjJlNTEtZjRkMy00OTU4LTk5ZDUtMmE0ZDRkOTg0YjIzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy93YTJnMDVrZXljbG9hayIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI2NGM3NjBiNi02ZTVjLTQyZmItOTI0ZC00MjhkNDE4ZTMyZWIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ3YTJnMDVrZXljbG9hay1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZDY3YTY1NzUtY2E2ZS00NmRmLWEzMTItZjA1NDIyZGY5YTE0IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbImRlZmF1bHQtcm9sZXMtd2EyZzA1a2V5Y2xvYWsiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsid2EyZzA1a2V5Y2xvYWstY2xpZW50Ijp7InJvbGVzIjpbIkV4cGVydCJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiZDY3YTY1NzUtY2E2ZS00NmRmLWEzMTItZjA1NDIyZGY5YTE0IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6ImV4cGVydDEiLCJnaXZlbl9uYW1lIjoiIiwiZmFtaWx5X25hbWUiOiIiLCJlbWFpbCI6ImV4cGVydDFAbWFpbC5jb20ifQ.MTwP5wBtQcwbY2kWQyFv4JW1dDwkppowEMLbC9LfVWIG7hNVvTgzJMpp3jJf_mKucWanuL5tr16yGb5Fq7zHQtjIUFUo7KNNSTSp6jHlDOPWr627KqIhY9d0HyzTXJ_27dxjSl73pBqbVrwJDIjB41W61NVUjQ8wBOjInbYhZU8Ns8MO8QdrBmBK11xA7yQCvl9BNA78CypNeFrv7SqB8llRkFnAkFFP-K6azcz09acIZlS5jKg0hDMgnfRZcJdXvArm-h-XFsw84_0Rw8j-uGYlXj7g88o2viKT_5NHSt9izxz5xKQzs7RcC90baX30fmYdtI6Hk3pQ95QqJB25Hg",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyNjViY2Y5ZS00ZGE4LTQ5NWYtYTcwYS1jNzAzZTg1M2YxNzcifQ.eyJleHAiOjE2ODU1Njg5MzYsImlhdCI6MTY4NTU2NzEzNiwianRpIjoiMDAxOWZmMDYtYWUzNS00ZjVmLTg3MzktOWFkYzNjOTE5ODFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy93YTJnMDVrZXljbG9hayIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9yZWFsbXMvd2EyZzA1a2V5Y2xvYWsiLCJzdWIiOiI2NGM3NjBiNi02ZTVjLTQyZmItOTI0ZC00MjhkNDE4ZTMyZWIiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoid2EyZzA1a2V5Y2xvYWstY2xpZW50Iiwic2Vzc2lvbl9zdGF0ZSI6ImQ2N2E2NTc1LWNhNmUtNDZkZi1hMzEyLWYwNTQyMmRmOWExNCIsInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImQ2N2E2NTc1LWNhNmUtNDZkZi1hMzEyLWYwNTQyMmRmOWExNCJ9.VGuJIMQ0fCbw8eSsE8WVlAOpEbE97P4j49ozbzBPmtU"
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 400 Bad Request` (Invalid or Not Active Refresh Token Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---

#### **`DELETE /api/authenticated/auth/logout`**

Close the current session and invalidating the access token.

**Request header:**
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)

#### **`POST /api/anonymous/auth/signup`**

Register a new customer profile 

**Request header:**
- `Content-Type: application/json`

**Request body:**

JSON object containing name, surname, an object "details" with password, email and username.

```json
{
  "username": "mariorossi",
  "email": "mariorossi@mail.com",
  "password": "Password1?",
    "details" : {
        "name": "Mario",
        "surname": "Rossi"
    }
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
  "username" : "mariorossi",
  "email": "mariorossi@mail.com"
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 403 Forbidden ` (Access Denied Exception)
- `HTTP status code 409 Conflict ` (Username or Email Already Exists Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---

#### **`POST /api/manager/auth/createExpert`**

Register a new expert profile

**Request header:**
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

JSON object containing an object "details" with password, email and username and a set of specialization ids

```json
{
  "username": "giuliobianchi",
  "email": "giuliobianchi@mail.com",
  "password": "Password1?",
  "details": {
    "specializations": [1, 2]
  }
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
  "username" : "giuliobianchi",
  "email": "giuliobianchi@mail.com"
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized ` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden ` (Access Denied Exception)
- `HTTP status code 404 Not Found ` (Specialization Not Found Exception)
- `HTTP status code 409 Conflict ` (Username or Email Already Exists Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
---