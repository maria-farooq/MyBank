package com.mybank.datatransferobject;

// singelton??
/**
 * @author mariafarooq
 */
public class Statistics {

	private final Double sum;
	private final Double avg;
	private final Double max;
	private final Double min;
	private final Long count;

	/**
	 * @param sum is a double specifying the total sum of transaction value in the last 60 seconds
	 * @param avg is a double specifying the average amount of transaction value in the last 60 seconds
	 * @param max is a double specifying single highest transaction value in the last 60 seconds
	 * @param min is a double specifying single lowest transaction value in the last 60 seconds
	 * @param count is a long specifying the total number of transactions happened in the last 60 seconds
	 */
	public Statistics(final Double sum, final Double avg, final Double max, final Double min, final Long count) {
		super();
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}
	
	public Double getSum() {
		return sum;
	}
	public Double getAvg() {
		return avg;
	}
	public Double getMax() {
		return max;
	}
	public Double getMin() {
		return min;
	}
	public Long getCount() {
		return count;
	}
	
}
