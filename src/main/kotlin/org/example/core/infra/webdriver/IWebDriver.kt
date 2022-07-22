package org.example.core.infra.webdriver

import mu.KLogger
import org.openqa.selenium.WebDriver

interface IWebDriver {
    val logger: KLogger
    fun getDriver(browserName: String): WebDriver?
}