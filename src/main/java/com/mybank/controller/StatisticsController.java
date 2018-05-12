package com.mybank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mybank.datatransferobject.Statistics;
import com.mybank.transaction.service.StatisticsService;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
	
	private final StatisticsService statisticsService;
	
	@Autowired
	public StatisticsController(final StatisticsService statisticsService){
		this.statisticsService = statisticsService;
	}

	@GetMapping
    public Statistics getStatistics() {
		return statisticsService.getStatistics();
	}

}
