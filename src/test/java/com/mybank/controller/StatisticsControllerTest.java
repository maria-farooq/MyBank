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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.datatransferobject.Transaction;

/**
 * This class will test various use cases of <b>/statistics</b> endpoint.
 * 
 * @author mariafarooq
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class StatisticsControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
    ObjectMapper objectMapper;
	
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
	@DirtiesContext
    public void getStatistics() throws Exception {

		// create 5 transactions within 60 seconds and 5 older than 60 seconds; each of amount=10

		final double amount = 10;
		
		for(int i=1; i<=5; i++) {
			createTransaction(amount, System.currentTimeMillis());
			createOlderTransaction(1);
		}

		mvc.perform(MockMvcRequestBuilders.get("/statistics")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sum", is(50.0)))
                .andExpect(jsonPath("$.avg", is(10.0)))
				.andExpect(jsonPath("$.max", is(10.0)))
				.andExpect(jsonPath("$.min", is(10.0)))
				.andExpect(jsonPath("$.count", is(5)));
		
		//sleep for 60 seconds and recalculate the statistics
		Thread.sleep(65000);

		mvc.perform(MockMvcRequestBuilders.get("/statistics")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sum", is(0.0)))
                .andExpect(jsonPath("$.avg", is(0.0)))
				.andExpect(jsonPath("$.max", is(0.0)))
				.andExpect(jsonPath("$.min", is(0.0)))
				.andExpect(jsonPath("$.count", is(0)));
    }
	
	/**
	 * Create a transaction older than 60 seconds.
	 * 
	 * @param amount
	 * @throws Exception
	 */
	private void createOlderTransaction(final double amount) throws Exception {
		final Long timestamp = System.currentTimeMillis()-66000;
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(amount, timestamp)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
	
	}

	/**
	 * Create a transaction with given parameters.
	 * @param amount
	 * @param timestamp
	 * @throws Exception
	 */
	private void createTransaction(final double amount, final long timestamp) throws Exception {
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(amount, timestamp)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated());
	}
}
