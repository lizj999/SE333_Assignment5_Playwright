package org.example.playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookstoreTest {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
    }

    @AfterAll
    static void teardown() {
        context.close();
        browser.close();
        playwright.close();
}

    // ─────────────────────────────────────────────────────────────────
    // TestCase 1: Bookstore
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(1)
    void testBookstore() {
        page.navigate("https://depaul.bncollege.com/");
        page.waitForLoadState();

        // Search
        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.waitForLoadState();

        // Filter: Brand → JBL
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.waitForTimeout(1000);
        page.locator("#facet-brand").getByRole(AriaRole.LIST)
                .getByLabel("(10 Products) in total").click();
        page.waitForLoadState();

        // Filter: Color → Black
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.waitForTimeout(1000);
        page.getByText("Color Black").click();
        page.waitForLoadState();

        // Filter: Price → Over $50
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.waitForTimeout(1000);
        page.getByText("Price Over $").click();
        page.waitForLoadState();

        // Click product
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        page.waitForLoadState();

        // Assert product details
        assertThat(page.locator("h1.name").first())
                .containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("668972707");
        assertThat(page.locator("body")).containsText("$164.98");
        assertThat(page.locator("body")).containsText("Adaptive noise cancelling");

        // Add to cart
        page.getByLabel("Add to cart").click();
        page.waitForTimeout(3000);

        // Assert 1 item in cart
        assertThat(page.locator("body")).containsText("1");

        // Click cart
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
        page.waitForLoadState();
    }

    // ─────────────────────────────────────────────────────────────────
    // TestCase 2: Shopping Cart Page
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(2)
    void testShoppingCartPage() {
        assertThat(page.locator("body")).containsText("Your Shopping Cart");
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");
        assertThat(page.locator("body")).containsText("164.98");

        // Select FAST In-Store Pickup
        page.getByText("FAST In-Store Pickup").first().click();
        page.waitForTimeout(2000);

        // Assert sidebar totals
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        // Enter promo code TEST
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        page.waitForTimeout(2000);

        // Assert promo code rejected
        assertThat(page.locator("body")).containsText("coupon code entered is not valid");

        // Proceed to checkout
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("PROCEED TO CHECKOUT")).first().click();
        page.waitForLoadState();
    }

    // ─────────────────────────────────────────────────────────────────
    // TestCase 3: Create Account Page
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(3)
    void testCreateAccountPage() {
        assertThat(page.locator("body")).containsText("Create Account");
    
        // Pause so we can inspect the page
        page.waitForTimeout(10000);
    
        page.frames().forEach(f -> System.out.println("Frame URL: " + f.url()));
    
        page.getByText("Proceed As Guest").click();
        page.waitForLoadState();
}

    // ─────────────────────────────────────────────────────────────────
    // TestCase 4: Contact Information Page
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(4)
    void testContactInformationPage() {
        assertThat(page.locator("body")).containsText("Contact Information");

        page.getByPlaceholder("Please enter your first name").fill("Liz");
        page.getByPlaceholder("Please enter your last name").fill("Jaramillo");
        page.getByPlaceholder("Please enter a valid email").fill("liz@test.com");
        page.getByPlaceholder("Please enter a valid phone").fill("7085272285");

        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForLoadState();
    }

    // ─────────────────────────────────────────────────────────────────
    // TestCase 5: Pickup Information
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(5)
    void testPickupInformationPage() {
        assertThat(page.locator("body")).containsText("Liz");
        assertThat(page.locator("body")).containsText("Jaramillo");
        assertThat(page.locator("body")).containsText("liz@test.com");
        assertThat(page.locator("body")).containsText("DePaul University Loop Campus & SAIC");
        assertThat(page.locator("body")).containsText("I'll pick them up");
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("TBD");
        assertThat(page.locator("body")).containsText("167.98");
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        page.waitForLoadState();
    }

    // ─────────────────────────────────────────────────────────────────
    // TestCase 6: Payment Information
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(6)
    void testPaymentInformationPage() {
        assertThat(page.locator("body")).containsText("164.98");
        assertThat(page.locator("body")).containsText("3.00");
        assertThat(page.locator("body")).containsText("JBL Quantum True Wireless Noise Cancelling Gaming Earbuds- Black");

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
        page.waitForLoadState();
    }

    // ─────────────────────────────────────────────────────────────────
    // TestCase 7: Delete from cart
    // ─────────────────────────────────────────────────────────────────
    @Test
    @Order(7)
    void testDeleteFromCart() {
        assertThat(page.locator("body")).containsText("Your Shopping Cart");

        page.getByLabel("Remove product JBL Quantum").click();
        page.waitForTimeout(2000);

        assertThat(page.locator("body")).containsText("empty");

    }
}