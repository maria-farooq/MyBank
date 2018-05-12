package com.mybank.datatransferobject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

// singelton??
public class StatisticsCalculator {

	private static Logger LOG = LoggerFactory.getLogger(StatisticsCalculator.class);
	/**
	 * Each index represent a second in last one minute.
	 * and it will contain statistics of transactions happened in that second.
	 * </p>Space cost is constant 60 units of elements.
	 */
	final private Vector<Statistics> sixtySecondStatistics = new Vector<>(59);
	
	private static StatisticsCalculator statisticsVector = null;
	
	public static synchronized StatisticsCalculator getStatisticsVector() {
	    if (statisticsVector == null) {
	    	statisticsVector = new StatisticsCalculator();
	    }
	    return statisticsVector;
	}
	
	private StatisticsCalculator(){
		
	}
	
	/**
	 * Submit new transaction and it will update the statistics accordingly.
	 * Transaction older than 60 seconds will be discarded and will have no impact on statistics.
	 * @param transaction
	 */
	public void submitTransaction (final Transaction transaction) {
		if (transaction.getTimestamp() > (System.currentTimeMillis()-60000)){
			//discard this as its already older than 60 seconds so its not helpful in statistics
		} else {
			// find out the index
			ZonedDateTime z = ZonedDateTime.ofInstant(Instant.ofEpochMilli(transaction.getTimestamp()), ZoneOffset.UTC);
			int transactionSecond = z.getSecond();
			updateStatisticsVectorIndex(transactionSecond, transaction);
		}
	}
	
	/**
	 * Traverse the vector and calculate statistics
	 * </p> time cost is: size of the vector = 60 constant units. O(1)
	 * @return
	 * Statistics
	 */
	public Statistics getStatistics() {
		Double currentMin = 0.0;
		Double currentMax = 0.0;
		Double sum = 0.0;
		Double avg;
		Long count = 0L;

		for(int i=0; i<sixtySecondStatistics.size(); i++) {

			Statistics currentStatisticsElement = sixtySecondStatistics.elementAt(i);

			if (currentStatisticsElement != null) {
				//re evaluate max
				final Double nextIndexMax = currentStatisticsElement.getMax();
				if (nextIndexMax > currentMax)
					currentMax = nextIndexMax;
				
				// re evaluate min
				final Double nextIndexMin = currentStatisticsElement.getMin();
				if (nextIndexMin < currentMin)
					currentMin = nextIndexMin;

				//update sum and count
				sum += currentStatisticsElement.getSum();
				count += currentStatisticsElement.getCount();
			}
		}
		
		//prevent divided by Zero
		if(count != 0)
			avg = sum/count;
		else
			avg = sum;
		
		return new Statistics(sum, avg, currentMax, currentMin, count);
	}
	
	/**
	 * @param index
	 * @param transaction
	 */
	private void updateStatisticsVectorIndex(final int index, final Transaction transaction){
		final Statistics currentStatistics = sixtySecondStatistics.get(index);
		
		final Double newTransactionAmount = transaction.getAmount();
		
		/** 
		 * if there is no stats on this index consider this transaction as first one for this second
		 * and initialize index with base statistics
		 */
		if (currentStatistics == null) {
			sixtySecondStatistics.add(index, new Statistics(newTransactionAmount, newTransactionAmount, newTransactionAmount, newTransactionAmount, 1L));
		} else {
			final Long newCount = currentStatistics.getCount()+1;
			final Double newSum = currentStatistics.getSum()+newTransactionAmount;
			final Double newMin = newTransactionAmount < currentStatistics.getMin() ? newTransactionAmount : currentStatistics.getMin();
			final Double newMax = newTransactionAmount > currentStatistics.getMax() ? newTransactionAmount : currentStatistics.getMax();
			final Double newAvg = newSum/newCount;
			sixtySecondStatistics.add(index, new Statistics(newSum, newAvg, newMax, newMin, newCount));
		}
	}
	
	/**
	 * shifts the vector to right. drops last item and empties first index.
	 */
	@Scheduled(fixedDelay = 1000)
	private void shiftVectorToRight() {
		LOG.info("shiftVectorToRight");
		int i=sixtySecondStatistics.size();
		do{
			sixtySecondStatistics.add(i, sixtySecondStatistics.elementAt(i-1));
		} while (i>0);
		
		//since all vector is shifted to right, lets empty the first index
		sixtySecondStatistics.add(0, null);
	}

	/*@Override
	public void run() {
		// run in a second
		final long timeInterval = 1000;

		while (true) {
			shiftVectorToRight();
			try {
				Thread.sleep(timeInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/
}
