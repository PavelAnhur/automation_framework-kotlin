package org.example.core.pageobjects

import org.example.core.infra.property.PropertyManager
import org.example.core.ui.UIElement
import org.example.core.infra.webdriver.WebDriverSingleton
import org.openqa.selenium.By

class HomePage : BasePage() {
    private val logo = UIElement(By.xpath("//*[@id='header_logo']/a"), "homepage logo")
    private val womenTitle = UIElement(By.xpath("//a[@title='Women']"), "'Women' title")
    private val driver = WebDriverSingleton.instance
    
    fun openHomePage(
        homePageUrl: String? = PropertyManager.config().homePageUrl(),
    ): HomePage {
        driver?.get(homePageUrl)
        return this
    }
    
    fun getLogoTitle(): String {
        logo.waitForDisplayed()
        val title = logo.getAttribute("title") ?: "Title element doesn't exist"
        logger.info { "title= '$title'" }
        return title
    }
    
    fun clickWomenTitle(): WomenPage {
        try {
            womenTitle.click()
        } catch (e: Exception) {
            logger.warn {
                """${e.message}
                    |need to refresh the page""".trimMargin()
            }
            driver?.navigate()?.refresh()
            womenTitle.click()
        }
        return WomenPage()
    }
}