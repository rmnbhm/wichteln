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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ExtendWith({ScreenshotOnFailureExtension.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.mail.host=localhost",
                "spring.mail.port=3025",
                "spring.mail.username=testuser",
                "spring.mail.password=testpassword",
                "spring.mail.protocol=smtp"
        }
)
public class WichtelnUiIntegrationTest {

    private final static String HOST_IP_ADDRESS = SystemUtils.IS_OS_LINUX ? "172.17.0.1" : "host.docker.internal";

    @LocalServerPort
    private int port;
    private String wichtelnUrl;
    private RemoteWebDriver webDriver;
    @Container
    private BrowserWebDriverContainer<?> container = new BrowserWebDriverContainer<>()
            .withSharedMemorySize(536_870_912L) // 512 MiB
            // This is a workaround to make the container start under WSL 2,
            // s. https://github.com/testcontainers/testcontainers-java/issues/2552
            .withCapabilities(new FirefoxOptions());

    @BeforeEach
    public void establishWebDriver() {
        wichtelnUrl = "http://" + HOST_IP_ADDRESS + ":" + port + "/"; // port is dynamic
        webDriver = container.getWebDriver();
    }

    @Test
    public void shouldDisplayEventCreationForm() {
        webDriver.get(wichtelnUrl);

        WebElement eventCreationForm = webDriver.findElement(By.id("event-creation-form"));
        assertThat(eventCreationForm).isNotNull();
        WebElement title = eventCreationForm.findElement(By.id("title"));
        assertThat(title).isNotNull();
        assertThat(title.getAttribute("placeholder")).isEqualTo("Title");
        WebElement description = eventCreationForm.findElement(By.id("description"));
        assertThat(description).isNotNull();
        assertThat(description.getAttribute("placeholder")).isEqualTo("Description");
        WebElement monetaryAmount = eventCreationForm.findElement(By.id("monetary-amount"));
        assertThat(monetaryAmount).isNotNull();
        assertThat(monetaryAmount.getAttribute("placeholder")).isEqualTo("Monetary Amount");
        WebElement heldAt = eventCreationForm.findElement(By.id("held-at"));
        assertThat(heldAt).isNotNull();
        assertThat(heldAt.getAttribute("placeholder")).isEqualTo("Held At");
    }

    @Test
    public void shouldDisplayTableHeaders() {
        webDriver.get(wichtelnUrl);

        WebElement participantsTableHeader = webDriver.findElement(By.id("participants-table-label"));
        assertThat(participantsTableHeader.getText()).isEqualTo("Participants");
    }

    @Test
    public void shouldDisplaySubmitAndResetButtons() {
        webDriver.get(wichtelnUrl);

        WebElement submitButton = webDriver.findElement(By.id("submit-button"));
        assertThat(submitButton.getText()).isEqualTo("Submit");
        WebElement resetButton = webDriver.findElement(By.id("reset-button"));
        assertThat(resetButton.getText()).isEqualTo("Reset");
    }

    @Test
    public void shouldAddAndRemoveParticipants() throws InterruptedException {
        webDriver.get(wichtelnUrl);

        // Fill with data
        WebElement title = webDriver.findElement(By.id("title"));
        title.sendKeys("AC/DC Secret Santa");
        WebElement description = webDriver.findElement(By.id("description"));
        description.sendKeys("There's gonna be some santa'ing");
        WebElement monetaryAmount = webDriver.findElement(By.id("monetary-amount"));
        monetaryAmount.sendKeys("78");
        WebElement heldAt = webDriver.findElement(By.id("held-at"));
        String dateTime = Instant.now().plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        heldAt.sendKeys(dateTime);

        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);

        assertThat(webDriver.findElement(By.id("add-participant-button"))).isNotNull();
        webDriver.findElement(By.id("add-participant-button")).click();
        webDriver.findElement(By.id("add-participant-button")).click();

        fillRow(0, "Angus", "Young", "angusyoung@acdc.net");
        fillRow(1, "Malcolm", "Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil", "Rudd", "philrudd@acdc.net");
        fillRow(3, "Bon", "Scott", "bonscott@acdc.net");
        fillRow(4, "Cliff", "Williams", "cliffwilliams@acdc.net");

        participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(5);
        assertThat(tableData()).containsExactly(
                List.of("Angus", "Young", "angusyoung@acdc.net"),
                List.of("Malcolm", "Young", "malcolmyoung@acdc.net"),
                List.of("Phil", "Rudd", "philrudd@acdc.net"),
                List.of("Bon", "Scott", "bonscott@acdc.net"),
                List.of("Cliff", "Williams", "cliffwilliams@acdc.net")
        );

        // Button id suffix (i.e. index) get recalculated after every removal, so in order to remove participants with
        // actual indices 1 and 2, we need to click removeParticipantButton1 twice
        webDriver.findElement(By.id("remove-participants1-button")).click();
        webDriver.findElement(By.id("remove-participants1-button")).click();

        participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);
        assertThat(tableData()).containsExactly(
                List.of("Angus", "Young", "angusyoung@acdc.net"),
                List.of("Bon", "Scott", "bonscott@acdc.net"),
                List.of("Cliff", "Williams", "cliffwilliams@acdc.net")
        );
        assertThat(webDriver.findElement(By.id("remove-participants0-button")).isEnabled()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants1-button")).isEnabled()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants2-button")).isEnabled()).isFalse();
    }

    @Test
    public void shouldValidateFormInput() {
        webDriver.get(wichtelnUrl);

        // Fill with invalid data
        WebElement title = webDriver.findElement(By.id("title"));
        title.sendKeys("AC/DC Secret Santa".repeat(20)); // too long
        WebElement description = webDriver.findElement(By.id("description")); // too long
        description.sendKeys("There's gonna be some santa'ing".repeat(100));
        WebElement monetaryAmount = webDriver.findElement(By.id("monetary-amount"));
        monetaryAmount.sendKeys("-1");
        WebElement heldAt = webDriver.findElement(By.id("held-at"));
        String dateTime = Instant.now().minus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        heldAt.sendKeys(dateTime);

        fillRow(0, "Angus".repeat(20), "Young", "angusyoung@acdc.net");
        fillRow(1, "Malcolm", "Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil", "Rudd".repeat(20), "philrudd@acdc.net");

        WebElement submitButton = webDriver.findElement(By.id("submit-button"));
        submitButton.click();

        WebElement titleError = webDriver.findElement(By.id("title-error"));
        assertThat(titleError.isDisplayed()).isTrue();
        WebElement descriptionError = webDriver.findElement(By.id("description-error"));
        assertThat(descriptionError.isDisplayed()).isTrue();
        WebElement monetaryAmountError = webDriver.findElement(By.id("monetary-amount-error"));
        assertThat(monetaryAmountError.isDisplayed()).isTrue();
        WebElement heldAtError = webDriver.findElement(By.id("held-at-error"));
        assertThat(heldAtError.isDisplayed()).isTrue();

        WebElement angusFirstNameError = webDriver.findElement(By.id("participants0-first-name-error"));
        assertThat(angusFirstNameError.isDisplayed()).isTrue();
        WebElement malcolmLastNameError = webDriver.findElement(By.id("participants2-last-name-error"));
        assertThat(malcolmLastNameError.isDisplayed()).isTrue();

    }

    private void fillRow(int rowIndex, String firstName, String lastName, String email) {
        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        WebElement row = participantsTable.findElements(By.cssSelector("tbody tr")).get(rowIndex);
        List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
        WebElement firstNameInput = inputFields.get(0);
        firstNameInput.sendKeys(firstName);
        WebElement lastNameInput = inputFields.get(1);
        lastNameInput.sendKeys(lastName);
        WebElement emailInput = inputFields.get(2);
        emailInput.sendKeys(email);
    }

    private List<List<String>> tableData() {
        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        List<List<String>> rows = new ArrayList<>();
        participantsTable.findElements(By.cssSelector("tbody tr")).forEach(row -> {
            List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
            rows.add(
                    inputFields.stream()
                            .map(webElement -> webElement.getAttribute("value"))
                            .filter(value -> !(value.equalsIgnoreCase("x")))
                            .collect(Collectors.toList())
            );
        });
        return rows;

    }

    private List<List<String>> tableErrors() {
        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        List<List<String>> rows = new ArrayList<>();
        participantsTable.findElements(By.cssSelector("tbody tr")).forEach(row -> {
            List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
            rows.add(
                    inputFields.stream()
                            .map(webElement -> webElement.getAttribute("value"))
                            .filter(value -> !(value.equalsIgnoreCase("x")))
                            .collect(Collectors.toList())
            );
        });
        return rows;
    }
}