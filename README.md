# Documentation

## Server APIs

### Products APIs

#### **`GET /api/products`**

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

#### **`GET /api/products/:ean`**

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

#### **`GET /api/profiles/:email`**

Details of user profiles associated with the provided email or fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email `
- `Content-Type: application/json`

**Response body:**

JSON object containing username e password.

```json
{
    "id": "1",
    "name": "user1",
    "surname": "surname1" ,
    "email": "user1@studenti.polito.it"
}
```

**Error responses**

- `HTTP status code 404 Not Found` (Profile Not Found Exception)

---

#### **`POST /api/profiles/`**

Store a new profile into the database, provided that the email address does not already exist

**Request header:**
- `Content-Type: application/json`

**Request body:**

JSON object containing name, surname and email.

```json
{
    "name": "user2",
    "surname": "surname2",
    "email": "user2@studenti.polito.it"
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
    "id": 2,
    "name": "user2",
    "surname": "surname2" ,
    "email": "user2@studenti.polito.it"
}
```

**Error responses**

- `HTTP status code 404 Not Found` (Profile Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
- `HTTP status code 409 Conflict` (Email already existing Exception)

---

#### **`PUT api/profiles/:email`**

Edit a field of a specific profile associated with the email and fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email`
- `Content-Type: application/json`

**Request body:**

A JSON object containing the data of the changes to be made.

```json
{
    "name": "user12",
    "surname": "surname12"
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
    "id": 2,
    "name": "user12",
    "surname": "surname12" ,
    "email": "user2@studenti.polito.it"
}
```

**Error responses**
- `HTTP status code 404 Not Found` (email not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---


### Ticket APIs 



#### **`POST /api/tickets`**

Stores a new ticket into the database for a given product


**Request header:**


- `Content-Type: application/json`

**Request body:**

JSON object containing title, description, customerId, productEAN, specializationId

```json
{
    "title": "Broken phone", 
    "description": "The phone is not turning on since yesterday", 
    "customerId":1, 
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
        "id": 1,
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

---


#### **`PATCH /api/tickets/:id/cancel`**

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
        "id": 1,
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

---


#### **`PATCH /api/tickets/:id/close`**

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
        "id": 1,
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

---


#### **`PATCH /api/tickets/:id/reopen`**

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
        "id": 1,
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

---


#### **`PATCH /api/tickets/:id/start`**

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
        "id": 1,
        "name": "Test",
        "surname": "Test",
        "email": "test@test.com"
    },
    "expert": {
        "id": 1,
        "email": "test@expert.it",
        "role": "EXPERT",
        "specializations": [
            {
                "id": 1,
                "name": "COMPUTER"
            },
            {
                "id": 2,
                "name": "MOBILE"
            }
        ]
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

---


#### **`PATCH /api/tickets/:id/stop`**

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
    "id": 1,
    "name": "Test",
    "surname": "Test",
    "email": "test@test.com"
  },
  "expert": {
    "id": 1,
    "email": "test@expert.it",
    "role": "EXPERT",
    "specializations": [
      {
        "id": 1,
        "name": "COMPUTER"
      },
      {
        "id": 2,
        "name": "MOBILE"
      }
    ]
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

---
#### **`PATCH /api/tickets/:id/resolve`**

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
        "id": 1,
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

---

#### **`GET /api/tickets/:id`**

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
        "id": 1,
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

---

#### **`GET /api/tickets?product={productId}`**

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
            "id": 1,
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
            "id": 1,
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

