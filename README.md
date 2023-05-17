# Documentation

## Server APIs

### Products APIs

#### **`GET /api/public/products`**

List all registered products in the database

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

---

#### **`GET /api/public/products/:ean`**

Details of product associated with the provided ean or fail if it does not exist

**Request header:**

- `Params: req.params.ean to obtain the required ean `
- `Content-Type: application/json`

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

- `HTTP status code 404 Not Found` (Profile Not Found Exception)

---

### Profiles APIs

#### **`GET /api/public/profiles/:email`**

Details of user profiles associated with the provided email or fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email `
- `Content-Type: application/json`

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

- `HTTP status code 404 Not Found` (Profile Not Found Exception)

---

#### **`POST /api/public/profiles`**

Store a new profile into the database, provided that the email address does not already exist

**Request header:**
- `Content-Type: application/json`

**Request body:**

JSON object containing name, surname and email.

```json
{
    "name": "user",
    "surname": "surname",
    "email": "user2@studenti.polito.it"
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
    "name": "user",
    "surname": "surname2" ,
    "email": "user2@studenti.polito.it"
}
```

**Error responses**

- `HTTP status code 404 Not Found` (Profile Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 409 Conflict` (Email already existing Exception)

---

#### **`PUT api/customer/profiles/:email`**

Edit a field of a specific profile associated with the email and fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email`
- `Content-Type: application/json`

**Request body:**

A JSON object containing the data of the changes to be made.

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
- `HTTP status code 404 Not Found` (email not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---

### Ticket APIs

#### **`POST /api/customer/tickets`**

Stores a new ticket into the database for a given product


**Request header:**

- `Content-Type: application/json`

**Request body:**

JSON object containing title, description, customerId, productEAN, specializationId

```json
{
    "title": "Broken phone", 
    "description": "The phone is not turning on since yesterday", 
    "customerId": "79c12344-4092-4661-b0b3-287685340d7b", 
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
- `HTTP status code 404 Not Found` (customer not found error)
- `HTTP status code 404 Not Found` (product not found error)
- `HTTP status code 404 Not Found` (specialization not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---


#### **`PATCH /api/customer/tickets/:id/cancel`**

Sets to CANCELLED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`


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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---


#### **`PATCH /api/{expert,manager}/tickets/:id/close`** 

Sets to CLOSED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`


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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)- `HTTP status c
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---


#### **`PATCH /api/customer/tickets/:id/reopen`**

Sets to REOPENED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`


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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---


#### **`PATCH /api/manager/tickets/:id/start`**

Sets to IN_PROGRESS the status field of a specific ticket associated with the id provided and fails if the id does not exist.
Automatically assigning the expert with the lowest "is working on" and the related specialization after having specified its level of priority(between 0 and 3).

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`

**Request body:**

JSON object containing priorityLevel

```json
{
    "priorityLevel":2
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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---


#### **`PATCH /api/expert/tickets/:id/stop`**

Sets to OPEN the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`


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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)

---
#### **`PATCH /api/{manager,expert}/tickets/:id/resolve`**

Sets to RESOLVED the status field of a specific ticket associated with the id provided and fails if the id does not exist

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`


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
- `HTTP status code 404 Not Found` (id not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)

---

#### **`GET /api/authenticated/tickets/:id`**

Retrieves the ticket with the given id

**Request header:**

- `Params: req.params.id to obtain the required id `
- `Content-Type: application/json`

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

- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Token)

---

#### **`GET /api/manager/tickets?product={productId}`**

Retrieves all the tickets related to the product id provided

**Request header:**

- `Params: req.params.productId to obtain the required productId `  
- `Content-Type: application/json`

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

- `HTTP status code 404 Not Found` (Product Not Found Exception)
- `HTTP status code 403 Forbidden` (Forbidden Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Token)

---

### Authentication APIs

#### **`POST /api/public/auth/login`**

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
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJuakQyeW82amZ5ZXhpSnhibWJCRDdlbl9sYVMzTnlUdHMxMzhSX2p6YmdzIn0.eyJleHAiOjE2ODQyNTY3NTUsImlhdCI6MTY4NDI1NjQ1NSwianRpIjoiYTAxN2EzZWMtMjY3My00NGNmLTlkNWUtOTZkYWNjZDc1ZWViIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy93YTJnMDVrZXljbG9hY2siLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNjlhMmM0NzMtZDE0MS00NGMwLWJhZTEtMzQ1YTE5ZGU1NGRlIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoid2EyZzA1a2V5Y2xvYWNrLWNsaWVudCIsInNlc3Npb25fc3RhdGUiOiI1N2QxYzJiOS04MTVlLTQ1YjYtYjA4NC0yNzQzNzJhODJhMWIiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXdhMmcwNWtleWNsb2FjayIsImFwcF9jdXN0b21lciIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJ3YTJnMDVrZXljbG9hY2stY2xpZW50Ijp7InJvbGVzIjpbIkN1c3RvbWVyIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNTdkMWMyYjktODE1ZS00NWI2LWIwODQtMjc0MzcyYTgyYTFiIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6ImN1c3RvbWVyMSIsImdpdmVuX25hbWUiOiIiLCJmYW1pbHlfbmFtZSI6IiIsImVtYWlsIjoiY3VzdG9tZXIxQG1haWwuY29tIn0.L_etrqDXmhPXIyFFiFuGn4VudnuFyOf5MSyY6ZNgvfB65MKGBhq-6fNGAeTdf8fcpeVH9-9hLmorRSIUDhL0ppYaTNaoK98AFm8C8rGqX0iJdXy46CnHlRrHh-T9cloVqbxa_0qe8jhBo-ctVeNBVFYtJWfVo0oZmrBJrtWFj43feQ1_I3dGCQPblgEuX2Un9t-dcYwIDjK1DmHizD7J1eAgWRlRH4mnVunj2oE6d0hBBKHrjbGDZNmuO-cSmX-FyIz6BD6YKO1de3aSiKZLV4IS7Jhs4Xv08cayR126Teigqk6PYgTJ6FgB4FsPGPHRxMEbx8sPrQjEfrp9U0fk3Q",
  "email": "customer1@mail.com",
  "username": "customer1",
  "name": "Gianmarco",
  "surname": "Verdi",
  "workingOn": null
}
```

**Error responses**

- `HTTP status code 404 Not Found` (Employee Not Found Exception or Profile Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 401 Unauthorized ` (Invalid User Credentials Exception)         

---

#### **`DELETE /api/authenticated/auth/logout`**

Close the current session and invalidating the access token.

**Request header:**
- `Content-Type: application/json`
- `Authorization: Bearer {token}`

**Response body**

`HTTP status code 200 OK`


**Error responses**

- `HTTP status code 401 Unauthorized` (Invalid Token)

