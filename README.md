# Documentation

## Server APIs

### Products APIs

#### **`GET /api/products`**

List all registered products in the database

**Response body**

`HTTP status code 200 OK`

```
[
    {
        "ean": 4935531465706,
        "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
        "brand": "JMT"
    },
    ...
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

```
{
    "ean": 4935531465706,
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
}
```

---

### Profiles APIs

#### **`GET /api/profiles/:email`**

Details of user profiles associated with the provided email or fail if it does not exist

**Request header:**

- `Params: req.params.email to obtain the required email `
- `Content-Type: application/json`

**Response body:**

JSON object contenente username e password.

```
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

```
{
    "name": "user2"
    "surname": "surname2" ,
    "email": "user2@studenti.polito.it"
}
```

**Response body**

`HTTP status code 201 CREATED`

```
{
    "id": 2
    "name": "user2"
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

```
{
    "name": "user12",
    "surname": "surname12"
}
```

**Response body**

`HTTP status code 200 OK`

```
{
    "id": 2
    "name": "user12"
    "surname": "surname12" ,
    "email": "user2@studenti.polito.it"
}
```

**Error responses**
- `HTTP status code 404 Not Found` (email not found error)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)
