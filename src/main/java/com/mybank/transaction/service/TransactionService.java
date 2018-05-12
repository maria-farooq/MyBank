package com.mybank.transaction.service;

import com.mybank.datatransferobject.Transaction;

public interface TransactionService {
	
	/**
	 * @param transaction
	 * 
	 * @return
	 * 1: if transaction is older than 60 seconds
	 *</p>-1: if transaction is in future
	 *</p>0: if transaction is within 60 seconds
	 */
	int postTranscation(final Transaction transaction);
}
