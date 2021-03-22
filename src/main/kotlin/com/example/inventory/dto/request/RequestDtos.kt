package com.example.inventory.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class ArticleInventories(
    val inventory: MutableList<Inventory> = mutableListOf()
)

data class Inventory(
    @JsonProperty(value = "art_id")
    val artId: String,
    val name: String,
    val stock: String
)

data class ProductStructure(
    val products: MutableList<Product> = mutableListOf()
)

data class Product(
    val name: String,
    @JsonProperty(value = "contain_articles")
    val containArticles: MutableList<Article> = mutableListOf()
)

data class Article(
    @JsonProperty(value = "art_id")
    val artId: String,
    @JsonProperty(value = "amount_of")
    val amountOf: String
)
