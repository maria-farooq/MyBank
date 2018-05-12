package com.mybank.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.datatransferobject.Transaction;

/**
 * This class will test various use cases of <b>/transactions</b> endpoint.
 * 
 * @author mariafarooq
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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
	 * </p> 2. current timestamp in milliseconds
	 * </p> <b>Expected: </b>
	 * </p> Empty body with Http status 201
	 * </p>
	 * @throws Exception
	 */
	@Test
    public void postTransaction() throws Exception {

		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, System.currentTimeMillis())))
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
    public void postTransactionWithTimeOlderThan60Seconds() throws Exception{

		long timeOlderThan60Seconds = System.currentTimeMillis() - (61000); 
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, timeOlderThan60Seconds)))
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
    public void postTransactionWithTimeInFurture() throws Exception{

		long timeInFuture = System.currentTimeMillis() + (61000); 
		
		mvc.perform(MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsBytes(new Transaction(12.3, timeInFuture)))
	            .contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());			
    }
}
