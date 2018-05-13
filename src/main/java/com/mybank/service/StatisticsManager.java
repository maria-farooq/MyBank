package com.mybank.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mybank.datatransferobject.Statistics;
import com.mybank.datatransferobject.Transaction;

public class StatisticsManager {

	private static Logger LOG = LoggerFactory.getLogger(StatisticsManager.class);
	private final int seconds = 59; 
	/**
	 * Each index represent a second in last one minute.
	 * and it will contain statistics of transactions happened in that second.
	 * </p>Space cost is constant 60 units of elements.
	 */
	private Vector<Statistics> sixtySecondStatistics = new Vector<>(seconds);
	
	private static StatisticsManager statisticsManagerInstance = null;
	
	public static synchronized StatisticsManager getStatisticsManager() {
	    if (statisticsManagerInstance == null) {
	    	statisticsManagerInstance = new StatisticsManager();
	    }
	    return statisticsManagerInstance;
	}
	
	private StatisticsManager(){
		for (int i=0; i<=seconds; i++) sixtySecondStatistics.add(i, null);
	}

	void setSixtySecondStatistics(Vector<Statistics> sixtySecondStatistics) {
		this.sixtySecondStatistics = sixtySecondStatistics;
	}

	public Vector<Statistics> getSixtySecondStatistics() {
		return sixtySecondStatistics;
	}

	public int getSeconds() {
		return seconds;
	}
	
	public boolean isSixtySecondStatisticsEmpty() {
		if(sixtySecondStatistics == null || sixtySecondStatistics.isEmpty())
			return true;
		for(int i=0; i<= seconds; i++){
			if(sixtySecondStatistics.get(i) != null)
				return false;
		}
		return true;
	}

	/**
	 * Traverse the vector and calculate statistics
	 * </p> time cost is: size of the vector = 60 constant units. O(1)
	 * @return
	 * Statistics
	 */
	public Statistics getStatistics() {
		LOG.info("getStatistics Called");
		Double currentMin=null;
		Double currentMax=null;
		Double sum = 0.0;
		Double avg;
		Long count = 0L;

		for(int i=0; i<=seconds; i++) {

			Statistics currentStatisticsElement = sixtySecondStatistics.elementAt(i);
			LOG.info("getStatistics sixtySecondStatistics["+i+"] is: "+currentStatisticsElement);
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

				LOG.info(String.format("sum: %s, min: %s, max: %s, count: %s", sum, currentMin, currentMax, count));
			}
		}
		
		//prevent divided by Zero
		if(count != 0)
			avg = sum/count;
		else
			avg = sum;
		Statistics statistics = new Statistics(sum, avg, currentMax==null?0.0:currentMax, currentMin==null?0.0:currentMin, count, null);
		LOG.info("Returning: "+statistics);
		return statistics;
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
			Statistics newStatistics = new Statistics(newTransactionAmount, newTransactionAmount, newTransactionAmount, newTransactionAmount, 1L, transaction.getTimestamp());
			sixtySecondStatistics.add(index, newStatistics);
			LOG.info("created new stat: "+newStatistics);
		} else {
			final Long newCount = currentStatistics.getCount()+1;
			final Double newSum = currentStatistics.getSum()+newTransactionAmount;
			final Double newMin = newTransactionAmount < currentStatistics.getMin() ? newTransactionAmount : currentStatistics.getMin();
			final Double newMax = newTransactionAmount > currentStatistics.getMax() ? newTransactionAmount : currentStatistics.getMax();
			final Double newAvg = newSum/newCount;
			Statistics newStatistics = new Statistics(newSum, newAvg, newMax, newMin, newCount, transaction.getTimestamp());
			sixtySecondStatistics.remove(index);
			sixtySecondStatistics.add(index, newStatistics);
			LOG.info("updated existing stats: "+newStatistics +" on index: "+index);
			LOG.info("sixtySecondStatistics after update: "+sixtySecondStatistics);
		}
	}
	
	/**
	 * shifts the vector to right. drops last item and empties first index.
	 */
	/*@Scheduled(fixedRate = 1000, initialDelay=1000)
	private void shiftVectorToRight() {
		LOG.info("shiftVectorToRight");
		int i=seconds;
		do{
			final Statistics leftElement = sixtySecondStatistics.elementAt(i-1);
			// since vector is shifting to right, lets empty the left index
			sixtySecondStatistics.add(i-1, null);
			sixtySecondStatistics.add(i, leftElement);
		} while (i>0);
	}*/
	
}
