package it.polito.wa2.g05.server.purchases

import java.util.UUID

class PurchaseNotFoundException(id: UUID) :
    RuntimeException("Purchase with id equals to $id not found")

class PurchaseAlreadyRegistered(id: UUID) :
    RuntimeException("Purchase $id already registered")

class PurchaseRegistrationFailed(id: UUID) :
    RuntimeException("Purchase $id registration failed")