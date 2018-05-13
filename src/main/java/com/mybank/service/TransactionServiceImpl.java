package com.mybank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    	if (transaction.getTimestamp() < sixtySecondsAgo){
    		LOG.debug("Transaction is older than a minute");
    		return 1;
    	}
    	if (transaction.getTimestamp() > currentTimeMillis){
    		LOG.debug("Transaction is in future");
    		return -1;
    	}
    	
    	StatisticsManager statisticsManager = StatisticsManager.getStatisticsManager();
    	statisticsManager.submitTransaction(transaction);
		return 0;

	}

}
