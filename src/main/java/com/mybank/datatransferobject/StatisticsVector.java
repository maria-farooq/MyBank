package com.mybank.datatransferobject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

// singelton??
public class StatisticsVector implements Runnable {

	/**
	 * Each index represent a second in last one minute.
	 * and it will contain statistics of transactions happened in that second.
	 * </p>Space cost is constant 60 units of elements.
	 */
	final private Vector<Statistics> sixtySecondStatistics = new Vector<>(60);
	
	/**
	 * This will look at the time and will update the vector index accordingly
	 * @param transaction
	 */
	public void update (final Transaction transaction) {
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
	 * Traverse the vector and calculate statistics
	 * </p> time cost is: size of the vector = 60 constant units. O(1)
	 * @return
	 * Statistics
	 */
	public Statistics getStatistics(){
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


	@Override
	public void run() {
		// shifts the vector to right and drops last item after every second
		int i=sixtySecondStatistics.size();
		do{
			sixtySecondStatistics.add(i, sixtySecondStatistics.elementAt(i-1));
		} while (i>0);
		
		//since all vector is shifted to right, lets empty the first index
		sixtySecondStatistics.add(0, null);
		
	}
}
