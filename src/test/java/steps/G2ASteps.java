package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;

public class G2ASteps extends WebSteps {

    private static final Logger logger = LogManager.getLogger(G2ASteps.class);
    String gamePrice;
    String gameTitle = System.getenv("GAME_TITLE");

    @Given("User enters G2A.com")
    public void user_enters_g2a_com() {
        getPage().navigate(getProps().getProperty("g2a.url"));
    }

    @When("User searches for game")
    public void user_searches_for_game() {
        var page = getPage();
        logger.info("---------------------------");
        logger.info("Searching for game: {}", gameTitle);
        logger.info("---------------------------");
        var searchInput = page.locator("input[type=search]");
        searchInput.fill(gameTitle);
        var searchButton = page.locator(".search_icon");
        searchButton.click();
        page.waitForCondition(() -> page.url().contains("search?query="));
    }

    @When("User adds the game to the cart")
    public void user_adds_the_game_to_the_cart() {
        var page = getPage();
        var xpath = String.format("//li[contains(., '%s')][1]", gameTitle);
        var gameCard = page.locator(xpath);
        gameCard.click();
        gamePrice = page.locator("//label[@data-locator='ppa-payment']").innerText();
        logger.info("Price: {}", gamePrice);
        var addToCartButton = page.locator("//form//button[@data-locator='ppa-payment__btn']");
        addToCartButton.evaluate("button => button.click()");
        // when popup confirm
        try {
            var confirmButton = page.locator("//button[@data-test-id='primary-button']");
            confirmButton.click();
        } catch (Exception e) {
            logger.warn("No confirmation popup... proceeding without");
        }
        logger.info("Game added to cart");
        page.waitForCondition(() -> page.locator("//header//h1").innerText().contains("Your cart"));
    }

    @Then("Cart should contain the game")
    public void cart_should_contain_the_game() {
        var gameContainerXpath = String.format(
                "//div[contains(@class, 'indexes__SellerWrapper') and contains(., '%s')]",
                gameTitle
        );
        // assert game container is present
        Assertions.assertThat(getPage().locator(gameContainerXpath).count()).isEqualTo(1);
    }

    @Then("Cart item price should be same as on the product page")
    public void cart_item_price_should_be_same_as_on_the_product_page() {
        // get price from cart
        // always first item matches the game price
        var cartPrice = getPage().locator("//span[@data-locator='zth-price']").nth(0).innerText();
        logger.info("Cart price: {}", cartPrice);
        // compare prices
        Assertions.assertThat(cartPrice).isEqualTo(gamePrice);
    }
}
