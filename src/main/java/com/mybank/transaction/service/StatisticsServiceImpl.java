package com.mybank.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mybank.datatransferobject.Statistics;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

	@Override
	public Statistics getStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}
