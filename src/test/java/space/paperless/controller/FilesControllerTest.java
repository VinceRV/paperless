package space.paperless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import space.paperless.repository.IdUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
@AutoConfigureMockMvc
public class FilesControllerTest {
	@Autowired
	private MockMvc mvc;

	@Test
	public void get_file_fileIsReturned() throws Exception {
		mvc.perform(get("/repositories/archive/files/" + IdUtils.id("archive", "type2", "201501_Captain_Future.pdf")))
				.andExpect(status().isOk()).andExpect(content().contentType("application/pdf"));
	}
}
