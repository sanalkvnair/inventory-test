package com.example.inventory.exception

import com.example.inventory.repository.entities.Inventory
import com.example.inventory.repository.entities.Products
import java.net.URI
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.ThrowableProblem

fun insufficientStockException(inventory: Inventory, products: Products): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/no-stock"))
        .withTitle("Insufficient stock")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Insufficient stock of article [${inventory.artId} - ${inventory.name}] for selling ${products.name}.")
        .with("Article", inventory.artId)
        .with("Product", products.name)
        .build()
}

fun productNotFoundException(productId: Long): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/product-not-found"))
        .withTitle("Product not found")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Product ID [$productId] not found.")
        .with("Product-ID", productId)
        .build()
}

fun emptyInventoryListException(): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/empty-inventory-list"))
        .withTitle("Empty inventory list for saving.")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Empty inventory list sent for saving.")
        .build()
}

fun emptyProductsListException(): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/empty-products-list"))
        .withTitle("Empty prodcuts list for saving.")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Empty products list sent for saving.")
        .build()
}

fun articleNotFoundException(artId: Int): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/article-not-found"))
        .withTitle("Article not found")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Article ID [$artId] not found.")
        .with("Article-ID", artId)
        .build()
}

fun invalidDataFormatException(): ThrowableProblem {
    return Problem.builder()
        .withType(URI.create("https://example.com/invalid-data-format"))
        .withTitle("Invalid data format.")
        .withStatus(Status.BAD_REQUEST)
        .withDetail("Invalid data format sent in request.")
        .build()
}
