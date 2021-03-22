package com.example.inventory.controller

import com.example.inventory.dto.request.ArticleInventories
import com.example.inventory.dto.request.ProductStructure
import com.example.inventory.dto.response.ProductStock
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.net.URI
import java.net.URL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.zalando.problem.Problem

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InventoryControllerTests {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var testObjectMapper: ObjectMapper

    @Test
    fun `get all products`() {
        val response = restTemplate.getForEntity(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_PRODUCTS_PATH").toString(),
            ProductStock::class.java
        )
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertTrue(response.body.products.isNotEmpty())
        assertEquals(2, response.body.products.size)
        println("Response: $response")
    }

    @Test
    fun `verify successful selling product`() {
        val param = mapOf("productId" to 1)
        val response = restTemplate.exchange(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH").toString(),
            HttpMethod.DELETE,
            null,
            ProductStock::class.java,
            param
        )
        println("Response: $response")
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertTrue(response.body.products.isNotEmpty())
        assertEquals(1, response.body.products.size)
    }

    @Test
    fun `verify successful selling of multiple products`() {
        val param = mapOf("productId" to 1, "quantity" to 2)
        val response = restTemplate.exchange(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH?quantity={quantity}").toString(),
            HttpMethod.DELETE,
            null,
            ProductStock::class.java,
            param
        )
        println("Response: $response")
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertTrue(response.body.products.isNotEmpty())
        assertEquals(1, response.body.products.size)
    }

    @Test
    fun `verify no stock error when selling product with stock quantity not available`() {
        val param = mapOf("productId" to 2, "quantity" to 2)
        val response = restTemplate.exchange(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH?quantity={quantity}").toString(),
            HttpMethod.DELETE,
            null,
            Problem::class.java,
            param
        )
        println("Response: $response")
        assertEquals(400, response.statusCodeValue)
        assertNotNull(response.body)
        assertEquals("Dining Table", response.body.parameters["Product"])
        assertEquals(4, response.body.parameters["Article"])
        assertEquals(URI("https://example.com/no-stock"), response.body.type)
        assertEquals("Insufficient stock", response.body.title)
    }

    @Test
    fun `verify successful adding of inventory`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestJson = testObjectMapper.readValue(
            getInputStream("/requests/inventory-request.json"),
            ArticleInventories::class.java
        )
        val httpEntity = HttpEntity(requestJson, headers)
        val response = restTemplate.postForEntity(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_INVENTORY_PATH").toString(),
            httpEntity,
            ArticleInventories::class.java
        )

        println("Inventory Response: $response")
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertTrue(response.body.inventory.isNotEmpty())
        assertEquals(4, response.body.inventory.size)
    }

    @Test
    fun `verify successful adding of product`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestJson = testObjectMapper.readValue(
            getInputStream("/requests/products-request.json"),
            ProductStructure::class.java
        )
        val httpEntity = HttpEntity(requestJson, headers)
        val response = restTemplate.postForEntity(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_PRODUCTS_PATH").toString(),
            httpEntity,
            ProductStock::class.java
        )

        println("Product Response: $response")
        assertEquals(200, response.statusCodeValue)
        assertNotNull(response.body)
        assertTrue(response.body.products.isNotEmpty())
        assertEquals(requestJson.products.size, response.body.products.size)
        response.body.products.map {
            assertNotNull(it.productId)
            assertEquals(4, it.inventory.size)
            assertNotNull(it.price)
        }
    }

    @Test
    fun `verify adding product failure when article not exist`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val requestJson = testObjectMapper.readValue(
            getInputStream("/requests/non-exist-article-products-request.json"),
            ProductStructure::class.java
        )
        val httpEntity = HttpEntity(requestJson, headers)
        val response = restTemplate.postForEntity(
            URL("http://localhost:$port/$INVENTORY_CONTROLLER_PRODUCTS_PATH").toString(),
            httpEntity,
            Problem::class.java
        )

        println("Failure Product Response: $response")
        assertEquals(400, response.statusCodeValue)
        assertNotNull(response.body)
        assertEquals(5, response.body.parameters["Article-ID"])
        assertEquals(URI("https://example.com/article-not-found"), response.body.type)
        assertEquals("Article not found", response.body.title)
    }

    protected fun getInputStream(path: String): InputStream {
        return this.javaClass.getResourceAsStream(path)
    }
}
