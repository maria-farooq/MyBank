package com.mybank.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mybank.datatransferobject.Transaction;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity createTransaction(@Valid @RequestBody Transaction transaction) {
		return null;
	}

}
