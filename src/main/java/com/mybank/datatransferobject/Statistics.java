package com.mybank.datatransferobject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mariafarooq
 */
public class Statistics {

	private final Double sum;
	private final Double avg;
	private final Double max;
	private final Double min;
	private final Long count;

	@JsonIgnore
	private final Long timestamp;

	/**
	 * @param sum is a double specifying the total sum of transaction value in the last 60 seconds
	 * @param avg is a double specifying the average amount of transaction value in the last 60 seconds
	 * @param max is a double specifying single highest transaction value in the last 60 seconds
	 * @param min is a double specifying single lowest transaction value in the last 60 seconds
	 * @param count is a long specifying the total number of transactions happened in the last 60 seconds
	 * @param timestamp represent state of the statistics at the given second
	 */
	public Statistics(final Double sum, final Double avg, final Double max, final Double min, final Long count, final Long timestamp) {
		super();
		this.sum = sum;
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
		this.timestamp = timestamp;
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
	public Long getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avg == null) ? 0 : avg.hashCode());
		result = prime * result + ((count == null) ? 0 : count.hashCode());
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
		result = prime * result + ((sum == null) ? 0 : sum.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statistics other = (Statistics) obj;
		if (avg == null) {
			if (other.avg != null)
				return false;
		} else if (!avg.equals(other.avg))
			return false;
		if (count == null) {
			if (other.count != null)
				return false;
		} else if (!count.equals(other.count))
			return false;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		if (sum == null) {
			if (other.sum != null)
				return false;
		} else if (!sum.equals(other.sum))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Statistics [sum=" + sum + ", avg=" + avg + ", max=" + max + ", min=" + min + ", count=" + count
				+ ", timestamp=" + timestamp + "]";
	}

}
