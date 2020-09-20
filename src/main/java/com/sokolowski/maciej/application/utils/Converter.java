package com.sokolowski.maciej.application.utils;

public class Converter {
	
	/**
	 * Converter to convert value from source currency rate to target currency rate
	 * 
	 * @param value amount of money used for conversion
	 * @param srcRate rate of source currency
	 * @param targetRate rate of target currency
	 * @return value of the money after conversion
	 */
	public Double convertSourceToTarget(Double value, Double srcRate, Double targetRate) {
		Double result = value/srcRate*targetRate;
		return result;
	}
}
