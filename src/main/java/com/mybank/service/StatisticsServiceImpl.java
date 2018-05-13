package com.mybank.service;

import org.springframework.stereotype.Service;

import com.mybank.datatransferobject.Statistics;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Override
	public Statistics getStatistics() {
		StatisticsManager statisticsManager = StatisticsManager.getStatisticsManager();
		return statisticsManager.getStatistics();
	}

}
