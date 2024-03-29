package org.example.steps.store

import io.qameta.allure.Step
import mu.KotlinLogging
import org.example.core.pageobject.store.SearchPage
import org.example.steps.BaseStep

class SearchPageSteps(
    private val searchPage: SearchPage
) : BaseStep() {
    @Step("Get diff list of search result")
    fun getDiffList(searchInput: String): List<String> {
        return productNamesFromSearchResult(searchInput)
            .filter { (!it.lowercase().contains(searchInput)) }
            .toCollection(mutableListOf())
            .toList()
    }

    private fun productNamesFromSearchResult(searchInput: String): List<String> {
        searchPage.waitForSearchResult(searchInput)
        val productNameList = ArrayList<String>()
        (1..searchPage.productSearchResult.size()).forEach { index ->
            val productName = searchPage.productName(index)
            KotlinLogging.logger {}.info { "#$index product name: $productName" }
            productNameList.add(productName)
        }
        return productNameList
    }
}
