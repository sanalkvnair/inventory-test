package com.example.inventory.controller

import com.example.inventory.dto.request.ArticleInventories
import com.example.inventory.dto.request.ProductStructure
import com.example.inventory.dto.response.ProductStock
import com.example.inventory.exception.emptyInventoryListException
import com.example.inventory.exception.emptyProductsListException
import com.example.inventory.service.InventoryService
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

const val INVENTORY_CONTROLLER_PRODUCTS_PATH = "api/products"
const val INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH = "api/product/{productId}"
const val INVENTORY_CONTROLLER_INVENTORY_PATH = "api/inventory"

const val REQUEST_PARAM_QUANTITY = "quantity"

@RestController
class InventoryController(
    val inventoryService: InventoryService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(InventoryController::class.java)
    }

    @GetMapping(value = [INVENTORY_CONTROLLER_PRODUCTS_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProducts(): ResponseEntity<ProductStock> {
        logger.info("Get all products with stock info.")
        return ResponseEntity.ok(inventoryService.getProducts())
    }

    @DeleteMapping(value = [INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun sellProduct(@PathVariable("productId") productId: Long, @RequestParam(REQUEST_PARAM_QUANTITY, required = false, defaultValue = "1")quantity: Int): ResponseEntity<ProductStock> {
        logger.info("Selling product: $productId with quantity: $quantity")
        return ResponseEntity.ok(inventoryService.sellProduct(productId, quantity))
    }

    @PostMapping(value = [INVENTORY_CONTROLLER_INVENTORY_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addInventories(@RequestBody @Valid articleInventories: ArticleInventories): ResponseEntity<ArticleInventories> {
        logger.info("Saving inventory")
        if (articleInventories.inventory.isEmpty()) throw emptyInventoryListException()
        return ResponseEntity.ok(inventoryService.addArticleInventory(articleInventories))
    }

    @PostMapping(value = [INVENTORY_CONTROLLER_PRODUCTS_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun addProducts(@RequestBody @Valid productStructure: ProductStructure): ResponseEntity<ProductStock> {
        logger.info("Saving products")
        if (productStructure.products.isEmpty()) throw emptyProductsListException()
        return ResponseEntity.ok(inventoryService.addProducts(productStructure))
    }

    @PutMapping(value = [INVENTORY_CONTROLLER_INVENTORY_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateInventories(@RequestBody @Valid articleInventories: ArticleInventories): ResponseEntity<ArticleInventories> {
        logger.info("Updating inventory")
        if (articleInventories.inventory.isEmpty()) throw emptyInventoryListException()
        return ResponseEntity.ok(inventoryService.updateArticleInventory(articleInventories))
    }
}
