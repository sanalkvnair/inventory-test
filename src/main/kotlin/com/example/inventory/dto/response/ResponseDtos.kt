package com.example.inventory.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ProductStock(
    val products: MutableList<Products> = mutableListOf()
)

data class Products(
    @JsonProperty(value = "product_id")
    val productId: String,
    val name: String,
    val price: BigDecimal,
    val inventory: MutableList<ProductArticleInventory> = mutableListOf()
)

data class ProductArticleInventory(
    @JsonProperty(value = "art_id")
    val artId: String,
    @JsonProperty(value = "art_name")
    val artName: String,
    @JsonProperty(value = "amount_of")
    val amountOf: String,
    val stock: String
)
