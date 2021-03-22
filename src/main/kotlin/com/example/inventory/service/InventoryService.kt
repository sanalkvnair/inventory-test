package com.example.inventory.service

import com.example.inventory.dto.request.ArticleInventories
import com.example.inventory.dto.request.ProductStructure
import com.example.inventory.dto.response.ProductArticleInventory
import com.example.inventory.dto.response.ProductStock
import com.example.inventory.dto.response.Products
import com.example.inventory.exception.articleNotFoundException
import com.example.inventory.exception.insufficientStockException
import com.example.inventory.exception.invalidDataFormatException
import com.example.inventory.exception.productNotFoundException
import com.example.inventory.repository.InventoryRepository
import com.example.inventory.repository.ProductStructureRepository
import com.example.inventory.repository.ProductsRepository
import com.example.inventory.repository.entities.Inventory
import javax.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val productsRepository: ProductsRepository,
    private val productStructureRepository: ProductStructureRepository,
    private val productPricingService: ProductPricingService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(InventoryService::class.java)
    }

    fun getProducts(): ProductStock {
        val productStocks = mutableListOf<Products>()
        productsRepository.findAll().map { products ->
            val articleInventories = mutableListOf<ProductArticleInventory>()
            products.productStructures.map { productStructure ->
                val articleInventory = ProductArticleInventory(
                    productStructure.inventory!!.artId.toString(),
                    productStructure.inventory!!.name,
                    productStructure.amountOf.toString(),
                    productStructure.inventory!!.stock.toString()
                )
                articleInventories.add(articleInventory)
            }
            productStocks.add(Products(products.productId.toString(), products.name, productPricingService.getProductPrice(articleInventories), articleInventories))
        }
        return ProductStock(productStocks)
    }

    fun getProduct(productId: Long): ProductStock {
        val productStock = mutableListOf<Products>()
        productsRepository
            .findById(productId)
            .ifPresentOrElse({ products ->
                val articleInventories = mutableListOf<ProductArticleInventory>()
                products.productStructures.map { productStructure ->
                    val articleInventory = ProductArticleInventory(
                        productStructure.inventory!!.artId.toString(),
                        productStructure.inventory!!.name,
                        productStructure.amountOf.toString(),
                        productStructure.inventory!!.stock.toString()
                    )
                    articleInventories.add(articleInventory)
                }
                productStock.add(Products(products.productId.toString(), products.name, productPricingService.getProductPrice(articleInventories), articleInventories))
            }, {
                throw productNotFoundException(productId)
            })

        return ProductStock(productStock)
    }

    @Transactional
    fun sellProduct(productId: Long, quantity: Int = 1): ProductStock {
        productsRepository.findByIdOrNull(productId)?.productStructures?.map { productStructure ->
            inventoryRepository.findById(productStructure.inventory!!.artId).map { inventory ->
                if (inventory.stock < productStructure.amountOf.times(quantity)) {
                    throw insufficientStockException(inventory, productStructure.products!!)
                }
                inventory.stock = inventory.stock.minus(productStructure.amountOf.times(quantity))
            }
        } ?: throw productNotFoundException(productId)

        val productStock = mutableListOf<Products>()
        productsRepository
            .findById(productId)
            .ifPresentOrElse({ products ->
                val articleInventories = mutableListOf<ProductArticleInventory>()
                products.productStructures.map { productStructure ->
                    val articleInventory = ProductArticleInventory(
                        productStructure.inventory!!.artId.toString(),
                        productStructure.inventory!!.name,
                        productStructure.amountOf.toString(),
                        productStructure.inventory!!.stock.toString()
                    )
                    articleInventories.add(articleInventory)
                }
                productStock.add(Products(products.productId.toString(), products.name, productPricingService.getProductPrice(articleInventories), articleInventories))
            }, {
                throw productNotFoundException(productId)
            })

        return ProductStock(productStock)
    }

    @Transactional
    fun addArticleInventory(articleInventories: ArticleInventories): ArticleInventories {
        val inventoryList: MutableList<Inventory> = mutableListOf()
        try {
            articleInventories.inventory.map { inventory ->
                inventoryList.add(Inventory(inventory.artId.toInt(), inventory.name, inventory.stock.toInt()))
            }
        } catch (e: Exception) {
            logger.error("Exception occurred: $e")
            throw invalidDataFormatException()
        }
        val savedInventoryList: MutableList<com.example.inventory.dto.request.Inventory> = mutableListOf()
        inventoryRepository.saveAll(inventoryList).map {
            savedInventoryList.add(com.example.inventory.dto.request.Inventory(it.artId.toString(), it.name, it.stock.toString()))
        }
        return ArticleInventories(savedInventoryList)
    }

    @Transactional
    fun addProducts(productStructure: ProductStructure): ProductStock {
        val productStocks = mutableListOf<Products>()
        productStructure.products.map { product ->
            val dbProduct = productsRepository.save(com.example.inventory.repository.entities.Products(0, product.name))
            product.containArticles.map { article ->
                inventoryRepository
                    .findById(article.artId.toInt())
                    .ifPresentOrElse({ inventory ->
                        val dbProductStructure = com.example.inventory.repository.entities.ProductStructure(0, amountOf = article.amountOf.toInt())
                        dbProduct.addProductStructure(dbProductStructure)
                        inventory.addProductStructure(dbProductStructure)
                    }, {
                        throw articleNotFoundException(article.artId.toInt())
                    })
            }
            val articleInventories = mutableListOf<ProductArticleInventory>()
            dbProduct.productStructures.map { productStructure ->
                val articleInventory = ProductArticleInventory(
                    productStructure.inventory!!.artId.toString(),
                    productStructure.inventory!!.name,
                    productStructure.amountOf.toString(),
                    productStructure.inventory!!.stock.toString()
                )
                articleInventories.add(articleInventory)
            }
            productStocks.add(Products(dbProduct.productId.toString(), dbProduct.name, productPricingService.getProductPrice(articleInventories), articleInventories))
        }
        return ProductStock(productStocks)
    }

    @Transactional
    fun updateArticleInventory(articleInventories: ArticleInventories): ArticleInventories {
        val savedInventoryList: MutableList<com.example.inventory.dto.request.Inventory> = mutableListOf()
        try {
            articleInventories.inventory.map { inventory ->
                inventoryRepository.findById(inventory.artId.toInt()).map {
                    it.stock = it.stock.plus(inventory.stock.toInt())
                    savedInventoryList.add(com.example.inventory.dto.request.Inventory(it.artId.toString(), it.name, it.stock.toString()))
                }
            }
        } catch (e: Exception) {
            logger.error("Exception occurred: $e")
            throw invalidDataFormatException()
        }

        return ArticleInventories(savedInventoryList)
    }
}
