package it.polito.wa2.g05.server.products

class ProductNotFoundException(ean: String) :
    RuntimeException("Product with ean equals to $ean not found")