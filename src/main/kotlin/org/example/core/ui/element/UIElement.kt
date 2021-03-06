package org.example.core.ui.element

import mu.KotlinLogging
import org.example.core.infra.retry.NUMBER_OF_ATTEMPTS
import org.example.core.infra.retry.WaitUtil
import org.example.core.infra.webdriver.WebDriverSingleton
import org.example.core.infra.webdriver.config.WebDriverConfig.Companion.EXPLICIT_TIMEOUT_SEC
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import java.util.function.Supplier

@Suppress("unused")
open class UIElement(
    val by: By,
    private val description: String,
) {
    private val logger = KotlinLogging.logger {}
    private val driver = WebDriverSingleton.instance
    private val jsExecutor = driver as JavascriptExecutor
    private val highlighter by lazy { IElementHighlighter.Impl(this) }
    internal val webElement: WebElement?
        get() = driver?.findElement(by)

    constructor(by: By) : this(by, by.toString())

    override fun toString(): String {
        return "locator=$by"
    }

    fun waitForDisplayed(timeout: Long = EXPLICIT_TIMEOUT_SEC) {
        logger.info { "waiting for element is displayed: $this" }
        waitForCondition(timeout, visibilityOfElementLocated(by))
    }

    fun getAttribute(attribute: String): String? {
        logger.info("getting attribute of the element: $this")
        highlighter.highlightAndUnhighlight()
        return webElement?.getAttribute(attribute)
    }

    fun click() {
        try {
            scrollToElement()
            waitForClickable()
            highlighter.highlightAndUnhighlight()
            logger.info { "clicking on the element: $this" }
            webElement?.click()
        } catch (e: Exception) {
            logger.error { "Failed clicking on element $this: ${e.message}" }
            error(e.message.toString())
        }
    }

    fun getText(): String {
        scrollToElement()
        logger.info { "getting text of the element: $this" }
        highlighter.highlightAndUnhighlight()
        return webElement?.text ?: "element $this doesn't contain test"
    }

    fun waitForDisappear() {
        logger.info { "waiting for element $this to disappear.." }
        val isElementDisappeared = Supplier<Boolean> {
            try {
                webElement?.isDisplayed
                logger.info { "element $this is visible" }
                false
            } catch (e: org.openqa.selenium.NoSuchElementException) {
                logger.info { "unable to locate element $this" }
                true
            }
        }
        WaitUtil.waitForTrue(
            supplier = isElementDisappeared,
            errorMessage = "element $this is visible after $NUMBER_OF_ATTEMPTS retries"
        )
    }

    private fun scrollToElement() {
        logger.info { "scrolling to the element: $this" }
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", webElement)
    }

    private fun waitForClickable(timeout: Long = EXPLICIT_TIMEOUT_SEC) {
        logger.info("waiting for the element is clickable: $this")
        waitForCondition(timeout, elementToBeClickable(by))
    }

    private fun waitForCondition(timeout: Long, expectedCondition: ExpectedCondition<WebElement>) {
        WebDriverWait(driver, Duration.ofSeconds(timeout)).until(expectedCondition)
    }
}
