package com.example.inventory.repository.entities

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "INVENTORY")
data class Inventory(
    @Id @Column(name = "art_id") val artId: Int,
    @Column(name = "name") val name: String,
    @Column(name = "stock") var stock: Int = 0,
    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, mappedBy = "inventory")
    val productStructures: MutableList<ProductStructure> = mutableListOf()
) {
    fun addProductStructure(productStructure: ProductStructure) {
        productStructures += productStructure
        productStructure.inventory = this
    }
    fun removeProductStructures() {
        productStructures.forEach {
            it.inventory = null
        }
        productStructures.clear()
    }
    override fun toString(): String {
        return "Inventory(artId=$artId, name='$name', stock=$stock)"
    }
}

@Entity
@Table(name = "PRODUCTS")
data class Products(
    @Id
    @Column(name = "product_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val productId: Long,
    @Column(name = "name") val name: String,
    @OneToMany(cascade = [(CascadeType.ALL)], fetch = FetchType.LAZY, mappedBy = "products")
    val productStructures: MutableList<ProductStructure> = mutableListOf()
) {
    fun addProductStructure(productStructure: ProductStructure) {
        productStructures += productStructure
        productStructure.products = this
    }
    fun removeProductStructures() {
        productStructures.forEach {
            it.products = null
        }
        productStructures.clear()
    }
    override fun toString(): String {
        return "Products(name='$name')"
    }
}

@Entity
@Table(name = "PRODUCT_STRUCTURE")
data class ProductStructure(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") val id: Long,
    @ManyToOne
    @JoinColumn(name = "art_id", referencedColumnName = "art_id")
    var inventory: Inventory? = null,
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    var products: Products? = null,
    @Column(name = "amount_of") val amountOf: Int
) {
    override fun toString(): String {
        return "ProductStructure(id=$id, amounOf=$amountOf)"
    }
}
