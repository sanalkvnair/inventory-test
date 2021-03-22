package com.example.inventory.repository

import com.example.inventory.repository.entities.Inventory
import com.example.inventory.repository.entities.ProductStructure
import com.example.inventory.repository.entities.Products
import org.springframework.data.repository.CrudRepository

interface InventoryRepository : CrudRepository<Inventory, Int>

interface ProductsRepository : CrudRepository<Products, Long>

interface ProductStructureRepository : CrudRepository<ProductStructure, Long>
