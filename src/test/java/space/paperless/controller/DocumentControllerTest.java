package space.paperless.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
public class DocumentControllerTest {
	private static final String ID = IdUtils.id("archive", "type2", "201501_Captain_Future.pdf");
	private static final String REPOSITORIES_ARCHIVE_DOCUMENTS = "/repositories/archive/documents/";

	@Autowired
	private MockMvc mvc;

	@Test
	public void get_document_documentIsReturned() throws Exception {
		mvc.perform(get(REPOSITORIES_ARCHIVE_DOCUMENTS + ID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.documentId", is(ID)));
	}
}
