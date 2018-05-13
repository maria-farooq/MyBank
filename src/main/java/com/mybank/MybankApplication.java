package com.mybank;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mybank.service.statistics.StatisticsManager;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableScheduling
public class MybankApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybankApplication.class, args);
	}

	/**
	 * Keeping StatisticsManager SINGLETON in scope of ApplicationContext
	 * @return
	 */
	@Bean("statisticsManager")
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public StatisticsManager getStatisticsManager() {
		return new StatisticsManager();
	}
}
