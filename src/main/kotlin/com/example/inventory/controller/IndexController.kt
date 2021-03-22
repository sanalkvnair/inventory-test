package com.example.inventory.controller

import com.damnhandy.uri.template.UriTemplate
import de.otto.edison.hal.HalRepresentation
import de.otto.edison.hal.Link
import de.otto.edison.hal.Links
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

const val INDEX_CONTROLLER_PATH = "/"
const val GET_PRODUCTS_REL = "get-all-products"
const val SELL_PRODUCT_REL = "sell-product"
const val ADD_INVENTORY_REL = "add-inventory"
const val ADD_PRODUCTS_REL = "add-products"

@RestController
class IndexController {

    @GetMapping(value = [INDEX_CONTROLLER_PATH], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun index(): ResponseEntity<HalRepresentation> = ResponseEntity.ok().body(
        HalRepresentation(links())
    )

    companion object {
        private fun links(): Links =
            Links.linkingTo()
                .self(selfHref())
                .array(inventoryControllerLinks())
                .build()

        private fun selfHref(): String = MvcUriComponentsBuilder.fromMethodCall(
            MvcUriComponentsBuilder.controller(IndexController::class.java).index()
        ).toUriString()

        private fun inventoryControllerLinks(): MutableList<Link> {
            return mutableListOf(
                getProductsLink(),
                sellProductLink(),
                addInventoryLink(),
                addProductsLink()
            )
        }

        private fun getProductsLink(): Link {
            val template = UriTemplate.buildFromTemplate(
                MvcUriComponentsBuilder.fromController(
                    InventoryController::class.java
                ).toUriString()
            ).template(INVENTORY_CONTROLLER_PRODUCTS_PATH)
                .build()
                .template

            return Link.link(GET_PRODUCTS_REL, template)
        }

        private fun sellProductLink(): Link {
            val template = UriTemplate.buildFromTemplate(
                MvcUriComponentsBuilder.fromController(
                    InventoryController::class.java
                ).toUriString()
            ).template(INVENTORY_CONTROLLER_DELETE_PRODUCT_PATH)
                .query(REQUEST_PARAM_QUANTITY)
                .build()
                .template

            return Link.link(SELL_PRODUCT_REL, template)
        }

        private fun addInventoryLink(): Link {
            val template = UriTemplate.buildFromTemplate(
                MvcUriComponentsBuilder.fromController(
                    InventoryController::class.java
                ).toUriString()
            ).template(INVENTORY_CONTROLLER_INVENTORY_PATH)
                .build()
                .template

            return Link.link(ADD_INVENTORY_REL, template)
        }
        private fun addProductsLink(): Link {
            val template = UriTemplate.buildFromTemplate(
                MvcUriComponentsBuilder.fromController(
                    InventoryController::class.java
                ).toUriString()
            ).template(INVENTORY_CONTROLLER_PRODUCTS_PATH)
                .build()
                .template

            return Link.link(ADD_PRODUCTS_REL, template)
        }
    }
}
