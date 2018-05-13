package com.mybank.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
 * This class will test various use cases of <b>/transactions</b> endpoint.
 * 
 * @author mariafarooq
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TransactionControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
    ObjectMapper objectMapper;
	
	/**
	 * Endpoint: <b>/transactions</b>
	 * </p> Http method: <b>POST</b>
	 * </p> <b>Given: </b>
	 * </p> 1. valid amount
	 * </p> 2. valid timestamp within last 60 seconds
	 * </p> <b>Expected: </b>
	 * </p> Empty body with Http status 201
	 * </p>
	 * @throws Exception
	 */
	@Test
    public void postTransaction() throws Exception {
		
		final long currentTime = System.currentTimeMillis(); 

		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, currentTime)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated());	
		
		final long twentySecondsAgo = System.currentTimeMillis() - 20000; 

		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, twentySecondsAgo)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated());		
    }
	
	/**
	 * Endpoint: <b>/transactions</b>
	 * </p> Http method: <b>POST</b>
	 * </p> <b>Given: </b>
	 * </p> 1. valid amount
	 * </p> 2. timestamp older than 60 seconds
	 * </p> <b>Expected: </b>
	 * </p> Empty body with Http status 204
	 * </p>
	 * @throws Exception
	 */
	@Test
    public void postTransactionWithTimeOlderThan60Seconds() throws Exception {

		final long sixtyOneSecondsAgo = System.currentTimeMillis() - (61000); 
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, sixtyOneSecondsAgo)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());			
    }
	
	/**
	 * Endpoint: <b>/transactions</b>
	 * </p> Http method: <b>POST</b>
	 * </p> <b>Given: </b>
	 * </p> 1. valid amount
	 * </p> 2. timestamp in future
	 * </p> <b>Expected: </b>
	 * </p> Empty body with Http status 400 (Bad Request)
	 * </p>
	 * @throws Exception
	 */
	@Test
    public void postTransactionWithTimeInFurture() throws Exception {

		final long timeInFuture = System.currentTimeMillis() + (61000); 
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, timeInFuture)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());			
    }
	
}
