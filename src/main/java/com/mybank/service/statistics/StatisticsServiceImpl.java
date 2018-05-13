package com.mybank.service.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mybank.datatransferobject.Statistics;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
	StatisticsManager statisticsManager;
	
	@Override
	public Statistics getStatistics() {
		return statisticsManager.getStatistics();
	}

}
