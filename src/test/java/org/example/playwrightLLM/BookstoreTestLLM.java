package org.example.playwrightLLM;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AI-Generated Playwright Tests using Playwright MCP Agent.
 * Generated from natural language prompt:
 * "Test the DePaul bookstore purchase pathway: search for earbuds,
 *  filter by JBL brand, Black color, and Over $50 price, add the
 *  JBL Quantum True Wireless Gaming Earbuds to cart, proceed through
 *  checkout as guest, fill contact info, verify pickup info, go to
 *  payment, go back to cart, and delete the item."
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreTestLLM {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
        page.setDefaultTimeout(30000);
    }

    @AfterAll
    static void closeBrowser() {
        context.close();
        browser.close();
        playwright.close();
    }

    @Test
    @Order(1)
    @DisplayName("Search for earbuds, apply filters, add JBL product to cart")
    void testSearchAndAddToCart() {
        page.navigate("https://depaul.bncollege.com/");
        page.waitForLoadState();

        // Search for earbuds
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForLoadState();

        // Apply Brand filter: JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.waitForTimeout(1000);
        page.locator("#facet-brand").getByRole(AriaRole.LIST)
                .getByLabel("(10 Products) in total").click();
        page.waitForLoadState();

        // Apply Color filter: Black
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.waitForTimeout(1000);
        page.getByText("Color Black").click();
        page.waitForLoadState();

        // Apply Price filter: Over $50
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.waitForTimeout(1000);
        page.getByText("Price Over $").click();
        page.waitForLoadState();

        // Navigate to product page
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName("JBL Quantum True Wireless")).click();
        page.waitForLoadState();

        // Verify product details
        assertThat(page.locator("h1.name").first())
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("668972707");
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("Adaptive noise cancelling");

        // Add to cart and verify
        page.getByLabel("Add to cart").click();
        page.waitForTimeout(3000);
        assertThat(page.locator("body")).containsText("1");

        // Navigate to cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName("Cart 1 items")).click();
        page.waitForLoadState();
    }

    @Test
    @Order(2)
    @DisplayName("Verify cart contents and apply promo code")
    void testCartPageAndPromoCode() {
        // Verify cart page
        assertThat(page.locator("body")).containsText("Your Shopping Cart");
        assertThat(page.locator("body"))
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("164.98");

        // Select in-store pickup
        page.getByText("FAST In-Store Pickup").first().click();
        page.waitForTimeout(2000);

        // Verify order summary
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Test invalid promo code
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(2000);
        assertThat(page.locator("body")).containsText("coupon code entered is not valid");

        // Proceed to checkout
        page.getByLabel("Proceed To Checkout").first().click();
        page.waitForLoadState();
    }

    @Test
    @Order(3)
    @DisplayName("Verify Create Account page and proceed as guest")
    void testProceedAsGuest() {
        assertThat(page.locator("body")).containsText("Create Account");
        page.waitForTimeout(2000);
        page.frames().stream()
            .filter(f -> f.url().contains("sso.bncollege.com"))
            .findFirst()
            .ifPresent(frame -> frame.getByRole(AriaRole.LINK,
                new Frame.GetByRoleOptions().setName("Proceed As Guest")).click());
        page.waitForLoadState();
    }

    @Test
    @Order(4)
    @DisplayName("Fill in contact information and continue")
    void testContactInformation() {
        assertThat(page.locator("body")).containsText("Contact Information");

        page.getByPlaceholder("Please enter your first name").fill("Liz");
        page.getByPlaceholder("Please enter your last name").fill("Jaramillo");
        page.getByPlaceholder("Please enter a valid email").fill("liz@test.com");
        page.getByPlaceholder("Please enter a valid phone").fill("7085272285");

        // Verify sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName("Continue")).click();
        page.waitForLoadState();
    }

    @Test
    @Order(5)
    @DisplayName("Verify pickup information page")
    void testPickupInformation() {
        assertThat(page.locator("body")).containsText("Liz");
        assertThat(page.locator("body")).containsText("Jaramillo");
        assertThat(page.locator("body")).containsText("liz@test.com");
        assertThat(page.locator("body")).containsText("DePaul University Loop Campus & SAIC");
        assertThat(page.locator("body")).containsText("I'll pick them up");
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");
        assertThat(page.locator("body"))
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName("Continue")).click();
        page.waitForLoadState();
    }

    @Test
    @Order(6)
    @DisplayName("Verify payment page and go back to cart")
    void testPaymentPage() {
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body"))
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions()
                .setName("Back to cart")).click();
        page.waitForLoadState();
    }

    @Test
    @Order(7)
    @DisplayName("Remove item from cart and verify cart is empty")
    void testRemoveFromCart() {
        assertThat(page.locator("body")).containsText("Your Shopping Cart");

        page.getByLabel("Remove product JBL Quantum").click();
        page.waitForTimeout(2000);

        assertThat(page.locator("body")).containsText("empty");
    }
}
