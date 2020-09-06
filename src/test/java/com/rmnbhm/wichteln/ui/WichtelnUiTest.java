package com.rmnbhm.wichteln.ui;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith({ScreenshotOnFailureExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WichtelnUiTest {

    private final static String HOST_IP_ADDRESS = SystemUtils.IS_OS_LINUX ? "172.17.0.1" : "host.docker.internal";

    @LocalServerPort
    private int port;
    private String wichtelnUrl;
    private RemoteWebDriver webDriver;

    @BeforeEach
    public void establishUrl() {
        wichtelnUrl = "http://" + HOST_IP_ADDRESS + ":" + port + "/"; // port is dynamic
        webDriver = container.getWebDriver();
    }

    @Container
    private BrowserWebDriverContainer<?> container = new BrowserWebDriverContainer<>()
            .withSharedMemorySize(536_870_912L) // 512 MiB
            // This is a workaround to make the container start under WSL 2,
            // s. https://github.com/testcontainers/testcontainers-java/issues/2552
            .withCapabilities(new FirefoxOptions());


    @Test
    public void shouldDisplayEventCreationForm() {
        webDriver.get(wichtelnUrl);

        WebElement eventCreationForm = webDriver.findElement(By.id("eventCreationForm"));
        assertThat(eventCreationForm).isNotNull();
        WebElement title = eventCreationForm.findElement(By.id("title"));
        assertThat(title).isNotNull();
        assertThat(title.getAttribute("placeholder")).isEqualTo("Title");
        WebElement description = eventCreationForm.findElement(By.id("description"));
        assertThat(description).isNotNull();
        assertThat(description.getAttribute("placeholder")).isEqualTo("Description");
        WebElement monetaryAmount = eventCreationForm.findElement(By.id("monetaryAmount"));
        assertThat(monetaryAmount).isNotNull();
        assertThat(monetaryAmount.getAttribute("placeholder")).isEqualTo("Monetary Amount");
        WebElement heldAt = eventCreationForm.findElement(By.id("heldAt"));
        assertThat(heldAt).isNotNull();
        assertThat(heldAt.getAttribute("placeholder")).isEqualTo("Held At");
    }

    @Test
    public void shouldDisplayTableHeaders() {
        webDriver.get(wichtelnUrl);

        WebElement participantsTableHeader = webDriver.findElement(By.id("participantsTableLabel"));
        assertThat(participantsTableHeader.getText()).isEqualTo("Participants");
    }

    @Test
    public void shouldDisplaySubmitAndResetButtons() {
        webDriver.get(wichtelnUrl);

        WebElement submitButton = webDriver.findElement(By.id("submitButton"));
        assertThat(submitButton.getAttribute("value")).isEqualTo("Submit");
        WebElement resetButton = webDriver.findElement(By.id("resetButton"));
        assertThat(resetButton.getAttribute("value")).isEqualTo("Reset");
    }

    @Test
    public void shouldAddParticipants() {
        webDriver.get(wichtelnUrl);
        preFillMetaData();

        WebElement participantsTable = webDriver.findElement(By.id("participantsTable"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(2);

        assertThat(webDriver.findElement(By.id("addParticipantButton"))).isNotNull();

        fillRow(0, "Angus", "Young", "angusyoung@acdc.net");
        fillRow(1, "Malcolm", "Young", "malcolmyoung@acdc.net");

        webDriver.findElement(By.id("addParticipantButton")).click();
        fillRow(2, "Phil", "Rudd", "philrudd@acdc.net");

        webDriver.findElement(By.id("addParticipantButton")).click();
        fillRow(3, "Bon", "Scott", "bonscott@acdc.net");

        webDriver.findElement(By.id("addParticipantButton")).click();
        fillRow(4, "Cliff", "Williams", "cliffwilliams@acdc.net");

        participantsTable = webDriver.findElement(By.id("participantsTable"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(5);
    }

    private void preFillMetaData() {
        WebElement title = webDriver.findElement(By.id("title"));
        title.sendKeys("AC/DC Secret Santa");
        WebElement description = webDriver.findElement(By.id("description"));
        description.sendKeys("There's gonna be some santa'ing");
        WebElement monetaryAmount = webDriver.findElement(By.id("monetaryAmount"));
        monetaryAmount.sendKeys("78");
        WebElement heldAt = webDriver.findElement(By.id("heldAt"));
        String date = Instant.now().plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        heldAt.sendKeys(date);
    }

    private void fillRow(int rowIndex, String firstName, String lastName, String email) {
        WebElement participantsTable = webDriver.findElement(By.id("participantsTable"));
        WebElement row = participantsTable.findElements(By.cssSelector("tbody tr")).get(rowIndex);
        List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
        WebElement firstNameInput = inputFields.get(0);
        firstNameInput.sendKeys(firstName);
        WebElement lastNameInput = inputFields.get(1);
        lastNameInput.sendKeys(lastName);
        WebElement emailInput = inputFields.get(2);
        emailInput.sendKeys(email);
    }
}