package org.example.tests.ui

import io.qameta.allure.Description
import org.example.core.infra.property.PropertyService
import org.example.steps.HomePageSteps
import org.example.steps.WomenPageSteps
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class WomenCollectionsTest : BaseTest() {
    private val collectionView = PropertyService.getProperty().collectionView().toString()

    @Test(dataProvider = "womenCollectionTestData", description = "Women collection verification")
    @Description("Women collection price order verification test")
    fun compareWomenCollectionTest(sortOrder: String) {
        reporter.info("<<<Compare women collection by price order>>>")
        homePageSteps = HomePageSteps()
            .also {
                it.openHomePage()
                    .openWomenPage()
            }

        womenPageSteps = WomenPageSteps()
            .also {
                it.changeProductTableView(sortOrder, collectionView)
                    .storeProductInfoInDB()
            }

        Assert.assertTrue(
            womenPageSteps.isProductPricesInOrder(sortOrder),
            """The price list isn't in $sortOrder order 
                |Actual price list: ${womenPageSteps.actualProductPricesList}""".trimMargin()
        )
    }

    @DataProvider
    fun womenCollectionTestData(): Iterator<String> {
        val data = listOf("ascending", "descending")
        return data.listIterator()
    }
}
