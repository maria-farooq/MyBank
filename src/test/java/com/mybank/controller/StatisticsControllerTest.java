package com.mybank.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * This class will test various use cases of <b>/statistics</b> endpoint.
 * 
 * @author mariafarooq
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

	@Autowired
	private MockMvc mvc;
	
	/**
	 * Endpoint: <b>/statistics</b>
	 * </p> Http method: <b>GET</b>
	 * </p> <b>Expected: </b>
	 * </p> Http status 200
	 * Returns:{"sum": ?,"avg": ?,"max": ?,"min": ?,"count": ?}
	 * </p>
	 * @throws Exception
	 */
	@Test
    public void getStatistics() throws Exception {

		final String sum=null;		// TODO: populate properly
		final String avg=null;		// TODO: populate properly
		final String max=null;		// TODO: populate properly
		final String min=null;		// TODO: populate properly
		final String count=null;	// TODO: populate properly

		mvc.perform(MockMvcRequestBuilders.get("/statistics")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sum", is(sum)))
                .andExpect(jsonPath("$.avg", is(avg)))
				.andExpect(jsonPath("$.max", is(max)))
				.andExpect(jsonPath("$.min", is(min)))
				.andExpect(jsonPath("$.count", is(count)));			
    }
}
