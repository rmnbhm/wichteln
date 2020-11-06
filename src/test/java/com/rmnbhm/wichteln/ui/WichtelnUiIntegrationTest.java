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
        wichtelnUrl = "http://" + HOST_IP_ADDRESS + ":" + port + "/wichteln"; // port is dynamic
        webDriver = container.getWebDriver();
    }

    @Test
    public void shouldDisplayEventCreationForm() {
        webDriver.get(wichtelnUrl);

        WebElement eventCreationForm = webDriver.findElement(By.id("event-creation-form"));
        assertThat(eventCreationForm).isNotNull();
        WebElement title = webDriver.findElement(By.id("title"));
        assertThat(title).isNotNull();
        WebElement description = webDriver.findElement(By.id("description"));
        assertThat(description).isNotNull();
        WebElement monetaryAmountNumber = webDriver.findElement(By.id("monetary-amount-number"));
        assertThat(monetaryAmountNumber).isNotNull();
        WebElement monetaryAmountCurrency = webDriver.findElement(By.id("monetary-amount-currency"));
        assertThat(monetaryAmountCurrency).isNotNull();
        WebElement localDateTime = webDriver.findElement(By.id("local-date-time"));
        assertThat(localDateTime).isNotNull();
        WebElement place = webDriver.findElement(By.id("place"));
        assertThat(place).isNotNull();
        WebElement hostName = webDriver.findElement(By.id("host-name"));
        assertThat(hostName).isNotNull();
        WebElement hostEmail = webDriver.findElement(By.id("host-email"));
        assertThat(hostEmail).isNotNull();
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
    public void shouldAddAndRemoveParticipants() {
        webDriver.get(wichtelnUrl);

        // Fill with data
        WebElement title = webDriver.findElement(By.id("title"));
        title.sendKeys("AC/DC Secret Santa");
        WebElement description = webDriver.findElement(By.id("description"));
        description.sendKeys("There's gonna be some santa'ing");
        WebElement monetaryAmountNumber = webDriver.findElement(By.id("monetary-amount-number"));
        monetaryAmountNumber.sendKeys("78.50");
        WebElement monetaryAmountUnit = webDriver.findElement(By.id("monetary-amount-currency"));
        monetaryAmountUnit.sendKeys("AUD");
        WebElement localDateTime = webDriver.findElement(By.id("local-date-time"));
        localDateTime.sendKeys(Instant.now().plus(1, ChronoUnit.DAYS)
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );
        WebElement place = webDriver.findElement(By.id("place"));
        place.sendKeys("Sydney");
        WebElement hostName = webDriver.findElement(By.id("host-name"));
        hostName.sendKeys("George Young");
        WebElement hostEmail = webDriver.findElement(By.id("host-email"));
        hostEmail.sendKeys("georgeyoung@acdc.net");

        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);

        // Remove buttons should be hidden initially since we do _not_ have more than three participants
        assertThat(webDriver.findElement(By.id("remove-participants0-button")).isDisplayed()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants1-button")).isDisplayed()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants2-button")).isDisplayed()).isFalse();

        assertThat(webDriver.findElement(By.id("add-participant-button"))).isNotNull();
        webDriver.findElement(By.id("add-participant-button")).click();
        webDriver.findElement(By.id("add-participant-button")).click();

        fillRow(0, "Angus Young", "angusyoung@acdc.net");
        fillRow(1, "Malcolm Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil Rudd", "philrudd@acdc.net");
        fillRow(3, "Bon Scott", "bonscott@acdc.net");
        fillRow(4, "Cliff Williams", "cliffwilliams@acdc.net");

        participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(5);
        assertThat(tableData()).containsExactly(
                List.of("Angus Young", "angusyoung@acdc.net"),
                List.of("Malcolm Young", "malcolmyoung@acdc.net"),
                List.of("Phil Rudd", "philrudd@acdc.net"),
                List.of("Bon Scott", "bonscott@acdc.net"),
                List.of("Cliff Williams", "cliffwilliams@acdc.net")
        );

        // Remove buttons should now be displayed since we have more than three participants
        assertThat(webDriver.findElement(By.id("remove-participants0-button")).isDisplayed()).isTrue();
        assertThat(webDriver.findElement(By.id("remove-participants1-button")).isDisplayed()).isTrue();
        assertThat(webDriver.findElement(By.id("remove-participants2-button")).isDisplayed()).isTrue();

        // Button id suffix (i.e. index) get recalculated after every removal, so in order to remove participants with
        // actual indices 1 and 2, we need to click removeParticipantButton1 twice
        webDriver.findElement(By.id("remove-participants1-button")).click();
        webDriver.findElement(By.id("remove-participants1-button")).click();

        participantsTable = webDriver.findElement(By.id("participants-table"));
        assertThat(participantsTable.findElements(By.cssSelector("tbody tr"))).hasSize(3);
        assertThat(tableData()).containsExactly(
                List.of("Angus Young", "angusyoung@acdc.net"),
                List.of("Bon Scott", "bonscott@acdc.net"),
                List.of("Cliff Williams", "cliffwilliams@acdc.net")
        );

        // Remove buttons should be hidden again
        assertThat(webDriver.findElement(By.id("remove-participants0-button")).isDisplayed()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants1-button")).isDisplayed()).isFalse();
        assertThat(webDriver.findElement(By.id("remove-participants2-button")).isDisplayed()).isFalse();
    }

    @Test
    public void shouldValidateFormInput() {
        webDriver.get(wichtelnUrl);

        // Fill with invalid data
        WebElement title = webDriver.findElement(By.id("title"));
        title.sendKeys("AC/DC Secret Santa".repeat(20)); // too long
        WebElement description = webDriver.findElement(By.id("description")); // too long
        description.sendKeys("There's gonna be some santa'ing".repeat(100));
        WebElement monetaryAmountNumber = webDriver.findElement(By.id("monetary-amount-number"));
        monetaryAmountNumber.sendKeys("-1"); // negative
        WebElement monetaryAmountCurrency = webDriver.findElement(By.id("monetary-amount-currency"));
        monetaryAmountCurrency.sendKeys("XXXX"); // not a valid currency
        WebElement localDateTime = webDriver.findElement(By.id("local-date-time"));
        localDateTime.sendKeys(Instant.now().minus(1, ChronoUnit.DAYS) // before present
                .atZone(ZoneId.of("Europe/Berlin"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
        );
        WebElement place = webDriver.findElement(By.id("place"));
        place.sendKeys("Sydney".repeat(20)); // too long
        WebElement hostName = webDriver.findElement(By.id("host-name"));
        hostName.sendKeys("George Young".repeat(20)); // too long
        WebElement hostEmail = webDriver.findElement(By.id("host-email"));
        hostEmail.sendKeys("georgeyoungacdc.net"); // no '@'

        fillRow(0, "Angus Young".repeat(20), "angusyoung@acdc.net"); // too long
        fillRow(1, "Malcolm Young", "malcolmyoung@acdc.net");
        fillRow(2, "Phil Rudd".repeat(20), "philrudd@acdc.net"); // too long

        WebElement submitButton = webDriver.findElement(By.id("submit-button"));
        submitButton.click();

        WebElement titleError = webDriver.findElement(By.id("title-error"));
        assertThat(titleError.isDisplayed()).isTrue();
        WebElement descriptionError = webDriver.findElement(By.id("description-error"));
        assertThat(descriptionError.isDisplayed()).isTrue();
        WebElement monetaryAmountNumberError = webDriver.findElement(By.id("monetary-amount-number-error"));
        assertThat(monetaryAmountNumberError.isDisplayed()).isTrue();
        WebElement localDateTimeError = webDriver.findElement(By.id("local-date-time-error"));
        assertThat(localDateTimeError.isDisplayed()).isTrue();
        WebElement placeError = webDriver.findElement(By.id("place-error"));
        assertThat(placeError.isDisplayed()).isTrue();
        WebElement hostNameError = webDriver.findElement(By.id("host-name-error"));
        assertThat(hostNameError.isDisplayed()).isTrue();
        WebElement hostEmailError = webDriver.findElement(By.id("host-email-error"));
        assertThat(hostEmailError.isDisplayed()).isTrue();

        WebElement angusFirstNameError = webDriver.findElement(By.id("participants0-name-error"));
        assertThat(angusFirstNameError.isDisplayed()).isTrue();
        WebElement malcolmLastNameError = webDriver.findElement(By.id("participants2-name-error"));
        assertThat(malcolmLastNameError.isDisplayed()).isTrue();

    }

    private void fillRow(int rowIndex, String name, String email) {
        WebElement participantsTable = webDriver.findElement(By.id("participants-table"));
        WebElement row = participantsTable.findElements(By.cssSelector("tbody tr")).get(rowIndex);
        List<WebElement> inputFields = row.findElements(By.cssSelector("input"));
        WebElement firstNameInput = inputFields.get(0);
        firstNameInput.sendKeys(name);
        WebElement emailInput = inputFields.get(1);
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
}