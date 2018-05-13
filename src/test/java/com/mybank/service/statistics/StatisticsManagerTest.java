package com.mybank.service.statistics;

import static org.junit.Assert.*;
import org.junit.Test;

import com.mybank.datatransferobject.Statistics;
import com.mybank.datatransferobject.Transaction;

public class StatisticsManagerTest {

	/**
	 * supply negative and positive amount - SHOULD handle both
	 * </p>supply time in past and present - SHOULD handle both but statistics should only consider last 60 seconds transactions
	 */
	@Test
	public void submitTransactionTest () {
		StatisticsManager manager = StatisticsManager.getStatisticsManager();
		// submit a brand new transaction
		manager.submitTransaction(new Transaction(15.0, System.currentTimeMillis()));
		// submit a 20 seconds old transaction
		manager.submitTransaction(new Transaction(5.0, System.currentTimeMillis()-30000));
		// submit a 20 seconds old transaction with NEGATIVE amount
		manager.submitTransaction(new Transaction(-5.0, System.currentTimeMillis()-30000));
		// submit a 2 minute old transaction
		manager.submitTransaction(new Transaction(5.0, System.currentTimeMillis()-120000));
		
		//make sure statistics are only calculating transactions within last one minute
		Statistics statistics = manager.getStatistics();
		assertEquals(new Double(15.0), statistics.getSum());
		assertEquals(new Double(5.0), statistics.getAvg());
		assertEquals(new Double(15.0), statistics.getMax());
		assertEquals(new Double(-5.0), statistics.getMin());
		assertEquals(new Long(3), statistics.getCount());
	}
}
