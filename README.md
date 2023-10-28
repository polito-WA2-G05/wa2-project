# Documentation

## Docker

In order to make easy testing the application, a `docker-compose.yml` file has been created in `/server/src/main/docker`
directory
and it includes our server application, our postgres database and keycloak images, already configured and ready-to-use.

You can move to `/server/src/main/docker` directory and execute the following command on terminal to pull our services
images:

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
In order to run also observability services, i.e. Tempo, Loki, Prometheus and Grafana, you have to execute also the
following command:

```bash
docker-compose -f docker-compose.observability.yml up
```

Note that you could see error logs on the Spring Boot server output console if you don't run observability services.
In order to disable observability on the server application you can edit the `docker-compose.yml` file with the
following `environment` for `server` service:

```yml
services:
  server:
    image: "gcr.io/g5project-385811/server:latest"

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
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)

---

#### **`GET /api/public/products/:ean`**

Details of product associated with the provided ean

**Request header:**

- `Params: req.params.ean to obtain the required ean`
- `Content-Type: application/json`
- `[Authorization: Bearer <access_token>]`

**Response body**

`HTTP status code 200 OK`

```json
{
  "ean": "4935531465706",
  "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
  "brand": "JMT"
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)

---

### Profiles APIs

#### **`GET /api/customer/profiles/me`**

Details of the authenticated user

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

```json
{
  "id": "79c12344-4092-4661-b0b3-287685340d7b",
  "name": "Mario",
  "surname": "Rossi",
  "email": "customer1@mail.com"
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Profile Not Found Exception)

---

#### **`PUT api/customer/profiles/me`**

Edit the profile of authenticated user

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

```json
{
  "name": "Mario",
  "surname": "Rossi"
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": "79c12344-4092-4661-b0b3-287685340d7b",
  "name": "Giovanni",
  "surname": "Rossi",
  "email": "customer1@mail.com"
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

```json
{
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "productEan": "4935531465706",
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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
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
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 400 Bad Request` (Failed to read Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Product not purchased Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)
- `HTTP status code 404 Not Found` (Specialization Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Validation Exception)

---

#### **`PATCH /api/customer/tickets/:id/cancel`**

Sets to CANCELLED the status field of a specific ticket associated with the id provided

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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
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
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/expert/tickets/:id/close`**

Expert sets to CLOSED the status field of a specific ticket associated with the id provided

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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-03T14:24:45.173+00:00",
  "closedDate": "2023-05-09T17:24:45.173+00:00",
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/manager/tickets/:id/close`**

Manager sets to CLOSED the status field of a specific ticket associated with the id provided

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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-03T14:24:45.173+00:00",
  "closedDate": "2023-05-09T17:24:45.173+00:00",
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  },
  "resolvedDescription": null,
  "survey": {
    "serviceValuation": 3,
    "professionality": 2,
    "comment": "good job"
  }
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)
- `HTTP status code 422 Unprocessable Entity` (Survey not sent yet Exception)

---

#### **`PATCH /api/customer/tickets/:id/reopen`**

Sets to REOPENED the status field of a specific ticket associated with the id provided

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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
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
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/manager/tickets/:id/start`**

Sets to IN_PROGRESS the status field of a specific ticket associated with the id provided.
Automatically assigning the expert with the lowest "is working on" and the related specialization after having specified
its level of priority(between 0 and 3).

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

```json
{
  "priorityLevel": 2
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": 2,
  "status": "REOPENED",
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "customer": {
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": {
    "id": "64c760b6-6e5c-42fb-924d-428d418e32eb",
    "username": "expert1",
    "workingOn": 0,
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
  },
  "resolvedDescription": null,
  "survey": null
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

Sets to OPEN the status field of a specific ticket associated with the id provided

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
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
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
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`PATCH /api/{manager,expert}/tickets/:id/resolve`**

Expert or Manager sets to RESOLVED the status field of a specific ticket associated with the id provided

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body (just for manager):**

```json
{
  "description": "resolved :) "
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": 2,
  "status": "RESOLVED",
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "customer": {
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-02T09:32:18.050+00:00",
  "closedDate": "2023-05-08T10:32:18.050+00:00",
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 400 Unauthorized` (Failed to read Exception) (just for manager)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Status Transition Exception)

---

#### **`GET /api/{customer,expert,manager}/tickets/:id`**

Retrieves the ticket with the given id.
If you are a "Manager" you can take any ticket, otherwise in the case of "Customer" and "Expert" only the tickets
assigned to them.

**Request header:**

- `Params: req.params.id to obtain the required id `
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

`HTTP status code 200 OK`

```json
{
  "id": 2,
  "status": "RESOLVED",
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "customer": {
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-02T09:32:18.050+00:00",
  "closedDate": "2023-05-08T10:32:18.050+00:00",
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  },
  "resolvedDescription": null,
  "survey": null
}
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)

---

#### **`GET /api/{customer,expert,manager}/tickets[?product={productEan}]`**

Retrieves all the tickets related to the product id provided.
If you are a "Manager" you can take any ticket, otherwise in the case of "Customer" and "Expert" only the tickets
assigned to them.
If the "product Ean" is passed, the tickets are filtered for that product.

**Request header:**

- `[Params: req.params.productEan to obtain the required productEan] `
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

`HTTP status code 200 OK`

```json
[
  {
    "id": 2,
    "status": "RESOLVED",
    "title": "Broken phone",
    "description": "The phone is not turning on since yesterday",
    "customer": {
      "id": "79c12344-4092-4661-b0b3-287685340d7b",
      "name": "Giovanni",
      "surname": "Rossi",
      "email": "customer1@mail.com"
    },
    "expert": null,
    "priorityLevel": null,
    "product": {
      "ean": "4935531465706",
      "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
      "brand": "JMT"
    },
    "createdDate": "2023-05-02T09:32:18.050+00:00",
    "closedDate": "2023-05-08T10:32:18.050+00:00",
    "specialization": {
      "id": 1,
      "name": "COMPUTER"
    },
    "resolvedDescription": null,
    "survey": null
  },
  ...
]

```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 404 Not Found` (Product Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)

---

#### **`GET /api/manager/tickets/changes`**

Returns all changes

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

`HTTP status code 200 OK`

```json
[
  {
    "id": 150,
    "fromStatus": null,
    "toStatus": "OPEN",
    "timestamp": "2023-10-20T19:47:35.530+00:00",
    "ticket": {
      "id": 2,
      "status": "OPEN",
      "title": "Broken phone",
      "description": "The phone is not turning on since yesterday",
      "customer": {
        "id": "79c12344-4092-4661-b0b3-287685340d7b",
        "name": "Giovanni",
        "surname": "Rossi",
        "email": "customer1@mail.com"
      },
      "expert": null,
      "priorityLevel": null,
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
      },
      "resolvedDescription": null,
      "survey": null
    },
    "expert": null
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)

---

#### **`GET /api/manager/tickets/experts`**

Retrieves all the experts

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body:**

`HTTP status code 200 OK`

```json
[
  {
    "id": "64c760b6-6e5c-42fb-924d-428d418e32eb",
    "username": "expert1",
    "workingOn": 0,
    "specializations": [
      {
        "id": 1,
        "name": "COMPUTER"
      },
      {
        "id": 2,
        "name": "MOBILE"
      }
      },
      ...
    ]

```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)

---

#### **`POST /api/customer/tickets/:id/survey`**

Sends the survey for the specified ticket

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

```json
{
  "serviceValutation": 2,
  "professionality": 3,
  "comment": null
}
```

**Response body**

`HTTP status code 200 OK`

```json
{
  "id": 2,
  "status": "RESOLVED",
  "title": "Broken phone",
  "description": "The phone is not turning on since yesterday",
  "customer": {
    "id": "79c12344-4092-4661-b0b3-287685340d7b",
    "name": "Giovanni",
    "surname": "Rossi",
    "email": "customer1@mail.com"
  },
  "expert": null,
  "priorityLevel": null,
  "product": {
    "ean": "4935531465706",
    "name": "JMT X-ring 530x2 Gold 104 Open Chain With Rivet Link for Kawasaki KH 400 a 1976",
    "brand": "JMT"
  },
  "createdDate": "2023-05-02T09:32:18.050+00:00",
  "closedDate": "2023-05-08T10:32:18.050+00:00",
  "specialization": {
    "id": 1,
    "name": "COMPUTER"
  },
  "resolvedDescription": null,
  "survey": {
    "serviceValutation": 2,
    "professionality": 3,
    "comment": null
  }
}
```

**Error responses**

- `HTTP status code 400 Unauthorized` (Failed to read Exception)
- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Ticket not resolved yet Exception)

### Messages APIs

#### **`GET /api/{customer,expert}/tickets/:id/messages/history`**

Returns all chat messages sent for a specific ticket

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "text": "ciao",
    "timestamp": "2023-10-24T18:00:37.923+00:00",
    "isFromCustomer": false
  },
  {
    "text": "ciao",
    "timestamp": "2023-10-24T18:01:41.414+00:00",
    "isFromCustomer": true
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 404 Not Found` (Expert Not Found Exception)

---

### Attachments APIs

#### **`GET /api/{customer,expert}/tickets/:id/attachments`**

Returns all attachments sent for a specific ticket

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "name": "file1.pdf",
    "url": "http://localhost:8080/api/customer/tickets/2/attachments/file1.pdf"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Ticket not in progress Exception)

---

#### **`POST /api/{customer,expert}/tickets/:id/attachments`**

Upload an attachment for a specific ticket

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Request body:**

The attachment as multipart file

**Response body**

`HTTP status code 200 OK`

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Ticket not in progress Exception)
- `HTTP status code 500 Internal Server Error` (Unexpected Exception)

---

#### **`GET /api/{customer,expert}/tickets/:id/attachments/:filename`**

Downloads the specified attachment sent for a specific ticket

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Params: req.params.filename to obtain the required filename`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

The requested file as bytes

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 403 Forbidden` (Customer Not Allowed Exception)
- `HTTP status code 403 Forbidden` (Expert Not Allowed Exception)
- `HTTP status code 404 Not Found` (Ticket Not Found Exception)
- `HTTP status code 422 Unprocessable Entity` (Ticket not in progress Exception)
- `HTTP status code 500 Internal Server Error` (Unexpected Exception)

---

### Purchases APIs

#### **`PUT /api/customer/purchases/:id/register`**

Register specified purchase for a customer

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Purchase Not Found Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)
- `HTTP status code 409 Conflict` (Purchase already registered Exception)
- `HTTP status code 500 Internal Server Error` (Unexpected Exception)

---

#### **`GET /api/customer/purchases/products`**

Returns all purchased products for the customer

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "products": [
      {
        "ean": "3528701753911",
        "name": "1x Summer Tyre Michelin Pilot Sport 4 255/40zr17 98y El",
        "brand": "Michelin"
      },
      {
        "ean": "4894836830193",
        "name": "2 in 1 out Dual Color Metal Hotend Extruder Kit With Cable 0.4mm Brass Nozz Y1e1",
        "brand": "SODIAL"
      },
      {
        "ean": "5018253112451",
        "name": "Hollings Smoked Filled Shank Bone 100g",
        "brand": "Hollings"
      },
      {
        "ean": "9781784881795",
        "name": "Darcey Bussell: Evolved by Darcey Bussell (Hardcover, 2018)",
        "brand": "Hardie Grant Books (United Kingdom)"
      }
    ],
    "purchasedAt": "2023-10-22T13:02:04.512+00:00"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (Access Denied Exception)
- `HTTP status code 404 Not Found` (Customer Not Found Exception)

---

### Specializations APIs

#### **`GET /api/authenticated/specializations`**

Returns all specializations

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "id": 1,
    "name": "COMPUTER"
  },
  {
    "id": 2,
    "name": "MOBILE"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)

---

### Notifications APIs

#### **`GET /api/authenticated/notifications**

Returns all notifications for the logged in user

**Request header:**

- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "id": "87173a95-01a6-4377-9e29-8e1e7dba6676",
    "text": "Your ticket (48) has been closed",
    "timestamp": "2023-10-27T16:11:03.218+00:00"
  },
  {
    "id": "46bf27c1-780c-4dc1-85b7-17f88fa941db",
    "text": "Your ticket (54) has been closed",
    "timestamp": "2023-10-27T15:41:12.143+00:00"
  },
  {
    "id": "976cfbab-dd62-4f6c-a6c8-8ddd65c225a5",
    "text": "You have received a new message for the ticket 48",
    "timestamp": "2023-10-27T15:39:32.689+00:00"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)

---

#### **`DELETE /api/authenticated/notifications/:id**

Delete specified notification for the logged in user

**Request header:**

- `Params: req.params.id to obtain the required id`
- `Content-Type: application/json`
- `Authorization: Bearer <access_token>`

**Response body**

`HTTP status code 200 OK`

```json
[
  {
    "id": "87173a95-01a6-4377-9e29-8e1e7dba6676",
    "text": "Your ticket (48) has been closed",
    "timestamp": "2023-10-27T16:11:03.218+00:00"
  },
  {
    "id": "46bf27c1-780c-4dc1-85b7-17f88fa941db",
    "text": "Your ticket (54) has been closed",
    "timestamp": "2023-10-27T15:41:12.143+00:00"
  },
  ...
]
```

**Error responses**

- `HTTP status code 401 Unauthorized` (Unauthorized Exception)
- `HTTP status code 401 Unauthorized` (Expired or Revoked Token Exception)
- `HTTP status code 403 Forbidden` (User not allowed Exception)
- `HTTP status code 404 Not Found` (Notification Not Found Exception)

---

### Authentication APIs

#### **`POST /api/anonymous/auth/login`**

Perform login

**Request header:**

- `Content-Type: application/json`

**Request body:**

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

```json
{
  "username": "mariorossi",
  "email": "mariorossi@mail.com",
  "password": "Password1?",
  "details": {
    "name": "Mario",
    "surname": "Rossi"
  }
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
  "username": "mariorossi",
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

```json
{
  "username": "giuliobianchi",
  "email": "giuliobianchi@mail.com",
  "password": "Password1?",
  "details": {
    "specializations": [
      1,
      2
    ]
  }
}
```

**Response body**

`HTTP status code 201 CREATED`

```json
{
  "username": "giuliobianchi",
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
