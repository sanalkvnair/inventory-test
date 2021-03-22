package com.example.inventory.service

import com.example.inventory.dto.response.ProductArticleInventory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom
import org.springframework.stereotype.Service

@Service
class ProductPricingService {

    fun getProductPrice(productArticleInventories: MutableList<ProductArticleInventory>) = BigDecimal(ThreadLocalRandom.current().nextInt(1, 500))
}
