package com.mybank.service.statistics;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mybank.datatransferobject.Statistics;

/**
 * StatisticsUpdateTask is responsible for keeping statistics relevant upto last one minute.
 * 
 * @author mariafarooq
 *
 */
@Component
public class StatisticsUpdateTask {

	private static Logger LOG = LoggerFactory.getLogger(StatisticsManager.class);

	@Autowired
	StatisticsManager statisticsManager;

	/**
	 * We want statistics-metrics (SixtySecondStatisticsMetrics) to hold only statistics of last 60 seconds.
	 * After each passing second this method will update the index of statistics
	 * in vector to the correct index as per current time.
	 * This way a given statistics element will keep shifting in the vector until its older than 60 seconds.
	 * 
	 *  </p> <b>Time cost</b> = since we loop around a constant number of elements (60) = <b>O(1)</b>
	 *  </p> <b>Space cost</b> = since we can have constant number of elements (60) at max = <b>O(1)</b>
	 */
	@Scheduled(fixedRate = 1000)
	private void shiftVectorStatistics() {
		Vector<Statistics> sixtySecondStatistics = statisticsManager.getSixtySecondStatisticsMetrics();
		if(!statisticsManager.isSixtySecondStatisticsMetricsEmpty()){
			Vector<Statistics> sixtySecondUpdatedStatistics = new Vector<>(statisticsManager.getSeconds());
			
			for (int i=0; i<=statisticsManager.getSeconds(); i++) sixtySecondUpdatedStatistics.add(i, null);
			
			for (int i=0; i<=statisticsManager.getSeconds() ; i++) {
				Statistics currentStatisticsElement = sixtySecondStatistics.elementAt(i);
				if (currentStatisticsElement != null) {
					int newStatSecondIndex = getAgeOfStatisticsInLastOneMinute(currentStatisticsElement.getTimestamp());
					// discard stats older than a minute
					if(newStatSecondIndex>=0) {
						sixtySecondUpdatedStatistics.add(newStatSecondIndex, currentStatisticsElement);
						LOG.trace(String.format("sixtySecondStatistics element %s moved from index: %s to index: %s", currentStatisticsElement, i, newStatSecondIndex));	
					} else {
						LOG.trace(String.format("Stats is now older than a min hence will be removed from vector: %s", currentStatisticsElement));
					}
				}
			}
			LOG.trace(String.format("updated statistics vector: %s", sixtySecondUpdatedStatistics));
			statisticsManager.setSixtySecondStatisticsMetrics(sixtySecondUpdatedStatistics);
			sixtySecondUpdatedStatistics = null;
		}
	}
	
	/**
	 * This method will evaluate the timestamp to see how old it is NOW.
	 * 
	 * @param timestamp
	 * 
	 * @return
	 * age of timestamp in seconds.
	 * </p>-1: if timestamp is older than a minute
	 */
	private static int getAgeOfStatisticsInLastOneMinute(final Long timestamp){
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
		ZonedDateTime ztimestampMinusAMin = ZonedDateTime.now().minusMinutes(1);
		int i = zonedDateTime.compareTo(ztimestampMinusAMin);
		if(i<0) {
			return -1;
		} else {
			// calculate the difference from current time to z
			long diff = System.currentTimeMillis()-timestamp;
			ZonedDateTime zDiff = ZonedDateTime.ofInstant(Instant.ofEpochMilli(diff), ZoneOffset.UTC);
			return zDiff.getSecond();
		}
	}
	

	public static void main (String[] str) {
		long timestamp = 1526201885800L;
		System.out.println("timestamp: "+ timestamp+" - ");
		System.out.println("currenttimestamp: "+ System.currentTimeMillis());
		System.out.println(StatisticsUpdateTask.getAgeOfStatisticsInLastOneMinute(timestamp));
	}
}
