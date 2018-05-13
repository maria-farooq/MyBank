package com.mybank.service.statistics;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mybank.datatransferobject.Statistics;
import com.mybank.datatransferobject.Transaction;

/**
 * StatisticsManager manages the transactions of last 60 seconds;
 * This class spring singleton
 * So that we have common sixtySecondStatisticsMetrics on application context level
 * 
 * @author mariafarooq
 *
 */
@Component
public class StatisticsManager {

	private static Logger LOG = LoggerFactory.getLogger(StatisticsManager.class);
	private final int seconds = 59;

	/**
	 * Each index in sixtySecondStatisticsMetrics represent a second in past one minute.
	 * and each element represents the statistics of transactions happened in that indexed second.
	 * </p> Hence sixtySecondStatisticsMetrics will always contain 60 elements at MAX.
	 * </p>Space cost is constant 60 units of elements.
	 */
	private Vector<Statistics> sixtySecondStatisticsMetrics = new Vector<>(seconds);
	
	/*
	 * This code was here to support java singleton, in case we want to make StatisticsManager on ClassLoader level
	 * For now we are keeping it singleton at spring context level.
	 * 
	 * private static StatisticsManager statisticsManagerInstance = null;
	
	public static synchronized StatisticsManager getStatisticsManager() {
	    if (statisticsManagerInstance == null) {
	    	statisticsManagerInstance = new StatisticsManager();
	    }
	    return statisticsManagerInstance;
	}*/
	
	public StatisticsManager(){
		LOG.info("StatisticsManager constructor called");
		for (int i=0; i<=seconds; i++) sixtySecondStatisticsMetrics.add(i, null);
	}

	/**
	 * Each index in sixtySecondStatisticsMetrics represent a second in past one minute.
	 * and each element represents the statistics of transactions happened in that indexed second.
	 * </p> Hence sixtySecondStatisticsMetrics will always contain 60 elements at MAX.
	 * 
	 * @param sixtySecondStatisticsMetrics
	 */
	void setSixtySecondStatisticsMetrics(Vector<Statistics> sixtySecondStatisticsMetrics) {
		this.sixtySecondStatisticsMetrics = sixtySecondStatisticsMetrics;
	}

	/**
	 * Each index in sixtySecondStatisticsMetrics represent a second in past one minute.
	 * and each element represents the statistics of transactions happened in that indexed second.
	 * </p> Hence sixtySecondStatisticsMetrics will always contain 60 elements at MAX.
	 * 
	 * @return
	 * SixtySecondStatisticsMetrics
	 */
	public Vector<Statistics> getSixtySecondStatisticsMetrics() {
		return sixtySecondStatisticsMetrics;
	}

	public int getSeconds() {
		return seconds;
	}
	
	/**
	 * @return
	 * true: if metrics is null, empty or contains null at each element.
	 * </p>false: otherwise
	 */
	public boolean isSixtySecondStatisticsMetricsEmpty() {
		if(sixtySecondStatisticsMetrics == null || sixtySecondStatisticsMetrics.isEmpty())
			return true;
		for(int i=0; i<= seconds; i++){
			if(sixtySecondStatisticsMetrics.get(i) != null)
				return false;
		}
		return true;
	}

	/**
	 * Traverse the sixtySecondStatisticsMetrics (Vector) and calculate statistics.
	 * </p>sixtySecondStatisticsMetrics will always contain 60 elements at MAX,
	 * given each index in sixtySecondStatisticsMetrics represent a second in past one minute.
	 * and each element represents the statistics of transactions happened in indexed second.
	 * </p> <b>Time cost is: size of the vector = 60 constant units. O(1)</b>
	 * </p>
	 * @return
	 * Statistics
	 */
	public Statistics getStatistics() {
		Double currentMin=null;
		Double currentMax=null;
		Double sum = 0.0;
		Double avg;
		Long count = 0L;

		for(int i=0; i<=seconds; i++) {

			Statistics currentStatisticsElement = sixtySecondStatisticsMetrics.elementAt(i);
			LOG.trace(String.format("getStatistics sixtySecondStatisticsMetrics[%s] is: %s", i, currentStatisticsElement));
			if (currentStatisticsElement != null) {
				//re evaluate max
				final Double nextIndexMax = currentStatisticsElement.getMax();
				currentMax = currentMax == null ? nextIndexMax : currentMax;
				if (nextIndexMax > currentMax)
					currentMax = nextIndexMax;
				
				// re evaluate min
				final Double nextIndexMin = currentStatisticsElement.getMin();
				currentMin = currentMin == null ? nextIndexMin : currentMin;
				if (nextIndexMin < currentMin)
					currentMin = nextIndexMin;

				//update sum and count
				sum += currentStatisticsElement.getSum();
				count += currentStatisticsElement.getCount();

				LOG.trace(String.format("sum: %s, min: %s, max: %s, count: %s", sum, currentMin, currentMax, count));
			}
		}
		
		//prevent divided by Zero
		if(count != 0)
			avg = sum/count;
		else
			avg = sum;
		Statistics statistics = new Statistics(sum, avg, currentMax==null?0.0:currentMax, currentMin==null?0.0:currentMin, count, null);
		LOG.trace(String.format("getStatistics is returning: %s", statistics));
		return statistics;
	}
	
	/**
	 * Submit new transaction and it will update the statistics accordingly.
	 * Transaction older than 60 seconds will be discarded and will have no impact on statistics.
	 * @param transaction
	 */
	public void submitTransaction (final Transaction transaction) {
		LOG.debug(String.format("Submitting new Transaction in statistics %s", transaction));
		if (transaction.getTimestamp() < (System.currentTimeMillis()-60000)) {
			LOG.debug(String.format("Discard this transaction %s as its already older than 60 seconds so its not helpful in statistics", transaction));
		} else {
			// find out the index
			ZonedDateTime z = ZonedDateTime.ofInstant(Instant.ofEpochMilli(transaction.getTimestamp()), ZoneOffset.UTC);
			int transactionSecond = z.getSecond();
			updateStatisticsVectorIndex(transactionSecond, transaction);
		}
	}
	
	/**
	 * @param index
	 * @param transaction
	 */
	private synchronized void updateStatisticsVectorIndex(final int index, final Transaction transaction){
		final Statistics currentStatistics = sixtySecondStatisticsMetrics.get(index);
		
		final Double newTransactionAmount = transaction.getAmount();
		
		/** 
		 * if there is no stats on this index consider this transaction as first one for this second
		 * and initialize index with base statistics
		 */
		if (currentStatistics == null) {
			Statistics newStatistics = new Statistics(newTransactionAmount, newTransactionAmount, newTransactionAmount, newTransactionAmount, 1L, transaction.getTimestamp());
			sixtySecondStatisticsMetrics.add(index, newStatistics);
			LOG.debug(String.format("Created new statistics %s at index %s ", newStatistics, index));
		} else {
			final Long newCount = currentStatistics.getCount()+1;
			final Double newSum = currentStatistics.getSum()+newTransactionAmount;
			final Double newMin = newTransactionAmount < currentStatistics.getMin() ? newTransactionAmount : currentStatistics.getMin();
			final Double newMax = newTransactionAmount > currentStatistics.getMax() ? newTransactionAmount : currentStatistics.getMax();
			final Double newAvg = newSum/newCount;
			Statistics newStatistics = new Statistics(newSum, newAvg, newMax, newMin, newCount, transaction.getTimestamp());
			sixtySecondStatisticsMetrics.remove(index);
			sixtySecondStatisticsMetrics.add(index, newStatistics);
			LOG.debug(String.format("Updated index %s. Replaced old statistics %s with new %s", index,currentStatistics, newStatistics));
		}
	}
	
}
