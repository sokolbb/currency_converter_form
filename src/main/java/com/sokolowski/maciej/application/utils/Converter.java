package com.sokolowski.maciej.application.utils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import com.sokolowski.maciej.application.model.Currency;

public class Converter {

	/**
	 * Converter to convert value from source currency rate to target currency rate
	 * 
	 * @param value      amount of money used for conversion
	 * @param srcRate    rate of source currency
	 * @param targetRate rate of target currency
	 * @return response that is returned to the user
	 */
	public Response convertSourceToTarget(Double value, Double srcRate, Double targetRate) {
		Double result = value / srcRate * targetRate;
		return Response.ok(result).build();
	}

	/**
	 * Method to find average rate for given currency and given range of values
	 * 
	 * @param rangeOfHistoricalRates Map of list of currencies for a given date range
	 * @param currencyStr currency name provided by user
	 * @return response that is returned to the user
	 */
	public Response findAverageRate(Map<LocalDate, List<Currency>> rangeOfHistoricalRates, String currencyStr) {
		Response apiResponse = null;
		int index = 0;
		double sum = 0;

		// Iterate through each record in a map
		for (Map.Entry<LocalDate, List<Currency>> entry : rangeOfHistoricalRates.entrySet()) {
			// Iterate through every currency to find value of currency needed by user
			for (Currency currency : entry.getValue()) {
				if (currencyStr.equalsIgnoreCase(currency.getCurrencyName())) {
					try {
						double rate = Double.parseDouble(currency.getReferenceRate());
						sum = sum + rate;
						index++;
						// The moment currency is found, we don't need to iterate through currencies
						// anymore
						break;
					} catch (NumberFormatException e) {
						// In case of exception, that means there was no rate value for that period, we
						// don't want to stop execution, only skip that record. The moment currency is
						// found we don't need to iterate through rest of currencies, even when currency
						// rate was unavailable
						break;
					}
				}
			}
		}

		if (index == 0) {
			apiResponse = Response.noContent().entity("There is no rate value for given currency: " + currencyStr + ".").build();
		} else {
			Double result = sum / index;
			apiResponse = Response.ok(result).build();
		}
		return apiResponse;
	}

	/**
	 * Method to find highest rate for given currency and given range of values
	 * 
	 * @param rangeOfHistoricalRates Map of list of currencies for a given date range
	 * @param currencyStr currency name provided by user
	 * @return response that is returned to the user
	 */
	public Response findHighestRate(Map<LocalDate, List<Currency>> rangeOfHistoricalRates, String currencyStr) {
		Response apiResponse = null;
		int index = 0;
		double highestRate = 0;

		// Iterate through each record in a map
		for (Map.Entry<LocalDate, List<Currency>> entry : rangeOfHistoricalRates.entrySet()) {
			// Iterate through every currency to find value of currency needed by user
			for (Currency currency : entry.getValue()) {
				if (currencyStr.equalsIgnoreCase(currency.getCurrencyName())) {
					try {
						double rate = Double.parseDouble(currency.getReferenceRate());
						
						//During first iteration, we take first value as highest rate
						if (index == 0) {
							highestRate = rate;
						}else {
							int compareVal = Double.compare(highestRate, rate);
							if (compareVal < 0) {
								highestRate = rate;
							}
						}
						index++;
						// The moment currency is found, we don't need to iterate through currencies
						// anymore
						break;
					} catch (NumberFormatException e) {
						// In case of exception, that means there was no rate value for that period, we
						// don't want to stop execution, only skip that record. The moment currency is
						// found we don't need to iterate through rest of currencies, even when currency
						// rate was unavailable
						break;
					}
				}
			}
		}

		if (index == 0) {
			apiResponse = Response.noContent().entity("There is no rate value for given currency: " + currencyStr + ".").build();
		} else {
			apiResponse = Response.ok(highestRate).build();
		}
		return apiResponse;
	}
}
