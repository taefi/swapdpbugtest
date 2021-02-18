package com.example.application.views;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;

public class BugViewTest extends TestBenchTestCase {

    @Before
    public void setUp() {
        setDriver(TestBench.createDriver(new ChromeDriver()));
    }

    @After
    public void tearDown() {
        if(driver != null) {
            driver.quit();
        }
    }

    @Test
    public void reproduceDetachAttachComboBoxBug() throws InterruptedException {
        getDriver().get("http://localhost:8080/bug");

        waitUntil(driver -> {
            assert driver != null;
            return driver.getTitle().equals("Bug");
        });

        $(ComboBoxElement.class).id("combo-box").openPopup();
        $(ButtonElement.class).id("combo-related-btn").click();
        $(ComboBoxElement.class).id("combo-box").openPopup();

        Thread.sleep(1000);

        Assert.assertThrows(org.openqa.selenium.NoSuchElementException.class, () -> {
            Assert.assertFalse($(DivElement.class).attribute("class", "v-system-error").first().isDisplayed());
        });

        checkLogsForErrors(msg -> false);
    }

    protected void checkLogsForErrors(
            Predicate<String> acceptableMessagePredicate) {
        getLogEntries(Level.WARNING).forEach(logEntry -> {
            if ((Objects.equals(logEntry.getLevel(), Level.SEVERE)
                    || logEntry.getMessage().contains(" 404 "))
                    && !acceptableMessagePredicate
                    .test(logEntry.getMessage())) {
                throw new AssertionError(String.format(
                        "Received error message in browser log console right after opening the page, message: %s",
                        logEntry));
            } else {
                LoggerFactory.getLogger(this.getClass().getName()).warn(
                        "This message in browser log console may be a potential error: '{}'",
                        logEntry);
            }
        });
    }

    protected List<LogEntry> getLogEntries(Level level) {
        // https://github.com/vaadin/testbench/issues/1233
        getCommandExecutor().waitForVaadin();

        return driver.manage().logs().get(LogType.BROWSER).getAll().stream()
                .filter(logEntry -> logEntry.getLevel().intValue() >= level
                        .intValue())
                // we always have this error
                .filter(logEntry -> !logEntry.getMessage()
                        .contains("favicon.ico"))
                .collect(Collectors.toList());
    }
}
