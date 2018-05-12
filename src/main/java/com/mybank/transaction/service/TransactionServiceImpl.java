package com.mybank.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mybank.datatransferobject.StatisticsManager;
import com.mybank.datatransferobject.Transaction;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.mybank.transaction.service.TransactionService#postTranscation(com.mybank.datatransferobject.Transaction)
	 */
	@Override
	public int postTranscation(final Transaction transaction) {
		final long currentTimeMillis = System.currentTimeMillis();
    	final long sixtySecondsAgo = currentTimeMillis - 60000;

    	if (transaction.getTimestamp() < sixtySecondsAgo)
    		return 1;
    	if (transaction.getTimestamp() > currentTimeMillis)
    		return -1;
    	
    	StatisticsManager statisticsManager = StatisticsManager.getStatisticsCalculator();
    	statisticsManager.submitTransaction(transaction);
		return 0;

	}

}
