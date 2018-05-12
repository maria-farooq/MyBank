package com.mybank.datatransferobject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class StatisticsManager {

	private static Logger LOG = LoggerFactory.getLogger(StatisticsManager.class);
	private final int seconds = 59; 
	/**
	 * Each index represent a second in last one minute.
	 * and it will contain statistics of transactions happened in that second.
	 * </p>Space cost is constant 60 units of elements.
	 */
	private final Vector<Statistics> sixtySecondStatistics = new Vector<>(seconds);
	
	private static StatisticsManager statisticsVector = null;
	
	public static synchronized StatisticsManager getStatisticsCalculator() {
	    if (statisticsVector == null) {
	    	statisticsVector = new StatisticsManager();
	    }
	    return statisticsVector;
	}
	
	private StatisticsManager(){
		for (int i=0; i<59; i++) sixtySecondStatistics.add(i, null);
	}
	
	/**
	 * Submit new transaction and it will update the statistics accordingly.
	 * Transaction older than 60 seconds will be discarded and will have no impact on statistics.
	 * @param transaction
	 */
	public void submitTransaction (final Transaction transaction) {
		LOG.info("submitTransaction: "+transaction);
		if (transaction.getTimestamp() < (System.currentTimeMillis()-60000)){
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
		LOG.info("getStatistics Called");
		Double currentMin = 0.0;
		Double currentMax = 0.0;
		Double sum = 0.0;
		Double avg;
		Long count = 0L;

		for(int i=0; i<seconds; i++) {

			Statistics currentStatisticsElement = sixtySecondStatistics.elementAt(i);
			LOG.info("getStatistics sixtySecondStatistics["+i+"] is: "+currentStatisticsElement);
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

				LOG.info(String.format("sum: %s, min: %s, max: %s, count: %s", sum, currentMin, currentMax, count));
			}
		}
		
		//prevent divided by Zero
		if(count != 0)
			avg = sum/count;
		else
			avg = sum;
		Statistics statistics = new Statistics(sum, avg, currentMax, currentMin, count);
		LOG.info("Returning: "+statistics);
		return statistics;
	}
	
	/**
	 * @param index
	 * @param transaction
	 */
	private void updateStatisticsVectorIndex(final int index, final Transaction transaction){
		LOG.info("updateStatisticsVectorIndex: "+transaction+ ": index: "+index);
		LOG.info("updateStatisticsVectorIndex sixtySecondStatistics size: "+sixtySecondStatistics.size());
		LOG.info("updateStatisticsVectorIndex sixtySecondStatistics: "+sixtySecondStatistics);
		final Statistics currentStatistics = sixtySecondStatistics.get(index);
		LOG.info("index: "+index+" currentStatistics: "+currentStatistics);
		
		final Double newTransactionAmount = transaction.getAmount();
		
		/** 
		 * if there is no stats on this index consider this transaction as first one for this second
		 * and initialize index with base statistics
		 */
		if (currentStatistics == null) {
			Statistics newStatistics = new Statistics(newTransactionAmount, newTransactionAmount, newTransactionAmount, newTransactionAmount, 1L);
			sixtySecondStatistics.add(index, newStatistics);
			LOG.info("created new stat: "+newStatistics);
		} else {
			final Long newCount = currentStatistics.getCount()+1;
			final Double newSum = currentStatistics.getSum()+newTransactionAmount;
			final Double newMin = newTransactionAmount < currentStatistics.getMin() ? newTransactionAmount : currentStatistics.getMin();
			final Double newMax = newTransactionAmount > currentStatistics.getMax() ? newTransactionAmount : currentStatistics.getMax();
			final Double newAvg = newSum/newCount;
			Statistics newStatistics = new Statistics(newSum, newAvg, newMax, newMin, newCount);
			sixtySecondStatistics.remove(index);
			sixtySecondStatistics.add(index, newStatistics);
			LOG.info("updated existing stats: "+newStatistics +" on index: "+index);
			LOG.info("sixtySecondStatistics after update: "+sixtySecondStatistics);
		}
	}
	
	/**
	 * shifts the vector to right. drops last item and empties first index.
	 */
	@Scheduled(fixedDelay = 1000, initialDelay=1000)
	private void shiftVectorToRight() {
		LOG.info("shiftVectorToRight");
		int i=seconds;
		do{
			final Statistics leftElement = sixtySecondStatistics.elementAt(i-1);
			// since vector is shifting to right, lets empty the left index
			sixtySecondStatistics.add(i-1, null);
			sixtySecondStatistics.add(i, leftElement);
		} while (i>0);
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
