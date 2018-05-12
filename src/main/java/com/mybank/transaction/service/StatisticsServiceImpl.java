package com.mybank.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mybank.datatransferobject.Statistics;
import com.mybank.datatransferobject.StatisticsManager;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

	@Override
	public Statistics getStatistics() {
		StatisticsManager statisticsManager = StatisticsManager.getStatisticsCalculator();
		return statisticsManager.getStatistics();
	}

}
