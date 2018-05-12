package com.mybank.controller;

import java.util.Calendar;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mybank.datatransferobject.Transaction;
import com.mybank.transaction.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	private final TransactionService transactionService;

	@Autowired
	public TransactionController(final TransactionService transactionService) {
		super();
		this.transactionService = transactionService;
	}

	@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity postTransaction(@Valid @RequestBody Transaction transaction) {
		
		final int timeStampEvaluation = transactionService.postTranscation(transaction);

		//return 204  if transaction is older than 60 seconds.
		if(timeStampEvaluation == 1)
			return ResponseEntity.noContent().build();

		//return 400 if transaction is set in future
		if(timeStampEvaluation == -1)
			return ResponseEntity.badRequest().build();

		//retun null means empty body with default 201 response
		return null;
	}

}
