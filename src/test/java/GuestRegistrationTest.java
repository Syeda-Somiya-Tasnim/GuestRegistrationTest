import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GuestRegistrationTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // Class-level variable to store the registration URL
    private String registrationUrl;

    @BeforeAll
    public void setup() {
        // Uncomment and set the path to your ChromeDriver executable if necessary
        // System.setProperty("webdriver.chrome.driver", "path/to/chromedriver"); // Update this path
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;

        // Initialize the registration URL
        registrationUrl = "https://demo.wpeverest.com/user-registration/guest-registration-form/";
    }

    private void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void navigateToRegistrationForm() {
        driver.get(registrationUrl);
        wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));
    }

    @Test
    @Order(1)
    @DisplayName("Visit Registration Form URL and check if title is displayed correctly")
    public void visitUrl() {
        navigateToRegistrationForm();

        String currentUrl = driver.getCurrentUrl();
        String actualTitle = driver.getTitle();
        String expectedTitle = "Guest Registration Form – User Registration";

        Assertions.assertEquals(expectedTitle, actualTitle);
        Assertions.assertTrue(currentUrl.contains("guest-registration-form"), "Current URL does not contain 'guest-registration-form'");
    }

    @Test
    @Order(2)
    @DisplayName("Fill out the registration form")
    public void fillOutForm() {
        navigateToRegistrationForm(); // Ensure the form is loaded

        fillFirstName("John");
        fillLastName("Doe");
        fillEmail("johndoe@example.com");
        selectGender("Male");
        fillDateOfBirth("1990-01-01");
        fillNationality("American");
        fillPhoneNumber("(123) 456-7890");
        selectCountry("Bangladesh");
        checkTermsAndConditions();
        submitForm();
    }

    private void fillFirstName(String firstName) {
        WebElement firstNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("first_name")));
        scrollToElement(firstNameField);
        firstNameField.sendKeys(firstName);
    }

    private void fillLastName(String lastName) {
        WebElement lastNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("last_name")));
        scrollToElement(lastNameField);
        lastNameField.sendKeys(lastName);
    }

    private void fillEmail(String email) {
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("user_email")));
        scrollToElement(emailField);
        emailField.sendKeys(email);
    }

    private void selectGender(String gender) {
        WebElement genderRadio = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='" + gender + "']")));
        scrollToElement(genderRadio);
        js.executeScript("arguments[0].click();", genderRadio);
    }

    private void fillDateOfBirth(String dob) {
        WebElement dateField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input.ur-flatpickr-field")));
        scrollToElement(dateField);
        js.executeScript("arguments[0].value='" + dob + "';", dateField);
    }

    private void fillNationality(String nationality) {
        WebElement nationalityField = wait.until(ExpectedConditions.elementToBeClickable(By.id("input_box_1665629217")));
        scrollToElement(nationalityField);
        nationalityField.sendKeys(nationality);
    }

    private void fillPhoneNumber(String phoneNumber) {
        WebElement phoneField = wait.until(ExpectedConditions.elementToBeClickable(By.id("phone_1665627880")));
        scrollToElement(phoneField);
        js.executeScript("arguments[0].value = '';", phoneField); // Clear the field before entering the number
        phoneField.sendKeys(phoneNumber);
    }

    private void selectCountry(String country) {
        WebElement countrySelect = wait.until(ExpectedConditions.elementToBeClickable(By.id("country_1665629257")));
        scrollToElement(countrySelect);
        new Select(countrySelect).selectByVisibleText(country);
    }

    private void checkTermsAndConditions() {
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("privacy_policy_1665633140")));
        scrollToElement(termsCheckbox);
        if (!termsCheckbox.isSelected()) {
            js.executeScript("arguments[0].click();", termsCheckbox);
        }
    }

    private void submitForm() {
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.ur-submit-button")));
        scrollToElement(submitButton);
        js.executeScript("arguments[0].click();", submitButton);
    }

    @Test
    @Order(3)
    @DisplayName("Assert successful registration")
    public void assertRegistrationSuccess() {
        WebElement successMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ur-message.user-registration-message")));
        String successMessage = successMessageElement.getText();
        Assertions.assertTrue(successMessage.contains("User successfully registered."), "Registration was not successful.");
    }

    @Test
    @Order(4)
    @DisplayName("Take a screenshot after submission")
    public void takeScreenShotAfterSubmission() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String filePath = "./src/test/resources/screenshots/registration_" + timestamp + ".png";

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(filePath));
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
