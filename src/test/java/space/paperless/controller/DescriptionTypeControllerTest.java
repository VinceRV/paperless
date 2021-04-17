package space.paperless.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import space.paperless.domain.DescriptionType;

import java.util.Arrays;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.hasItems;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class DescriptionTypeControllerTest {

	@Autowired
	private DescriptionTypeController controller;

	@Test
	public void get_descriptionFields_fieldTypesAreReturned() {
		given().standaloneSetup(controller).when().get("/descriptionTypes").then().statusCode(200).body("name",
				hasItems(
						Arrays.stream(DescriptionType.values()).map(DescriptionType::getName).toArray(String[]::new)));
	}
}
