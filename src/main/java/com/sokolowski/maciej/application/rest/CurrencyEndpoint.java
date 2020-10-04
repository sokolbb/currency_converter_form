package com.sokolowski.maciej.application.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sokolowski.maciej.application.model.Currency;
import com.sokolowski.maciej.application.constants.Consts;
import com.sokolowski.maciej.application.handlers.CurrencyHandler;
import com.sokolowski.maciej.application.utils.Converter;
import com.sokolowski.maciej.application.utils.UserInputChecker;

@Path("/")
public class CurrencyEndpoint {

	/**
	 * Retrieve the reference rate data for a given Date for all available
	 * Currencies
	 * 
	 * @param dateStr Date provided by user as string input
	 * @return response that is returned to the user
	 */
	@Path("retrieveRatesOnDate/{date}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// Date has to be in format YYYY-MM-DD
	public Response displayAllByDate(@PathParam("date") String dateStr) {
		Response apiResponse = null;

		// Check if date is in correct format, but keep it as String
		new UserInputChecker().dateFormatChecker(dateStr);

		CurrencyHandler cH = new CurrencyHandler();

		// Map currencies and values to list of currencies
		List<Currency> listOfCurrencies = cH.historicalCurrencyMapper(Consts.URL_TO_ZIP_FILE, dateStr);

		if (listOfCurrencies == null || listOfCurrencies.isEmpty()) {
			apiResponse = Response.noContent().build();
		} else {
			apiResponse = Response.ok(listOfCurrencies).build();
		}

		return apiResponse;
	}

	/**
	 * Given a Date, source Currency (eg. JPY), target Currency (eg. GBP), and an
	 * Amount, returns the Amount given converted from the first to the second
	 * Currency as it would have been on that Date
	 * 
	 * @param dateStr        Date provided by user
	 * @param srcCurrency    source currency name
	 * @param targetCurrency target currency name
	 * @param amount         value of money needed to be converted
	 * @return response that is returned to the user
	 */
	
	@Path("convertCurrency/{date}/{srcCurrency}/{targetCurrency}/{amount}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response convertSourceToTargetCurrency(@PathParam("date") String dateStr,
			@PathParam("srcCurrency") String srcCurrency, @PathParam("targetCurrency") String targetCurrency,
			@PathParam("amount") String amount) {
		Response apiResponse = null;
		double value = 0;
		UserInputChecker checker = new UserInputChecker();
		// Check if input is correct, but keep date as String
		checker.dateFormatChecker(dateStr);
		checker.isCurrencyFormatCorrect(srcCurrency);
		checker.isCurrencyFormatCorrect(targetCurrency);

		try {
			value = Double.parseDouble(amount);
		} catch (NumberFormatException e) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Value input is incorrect: " + e.getMessage()).build());
		}

		CurrencyHandler cH = new CurrencyHandler();
		// Map currencies and values to list of currencies
		List<Currency> listOfCurrencies = cH.historicalCurrencyMapper(Consts.URL_TO_ZIP_FILE, dateStr);

		if (listOfCurrencies == null || listOfCurrencies.isEmpty()) {
			apiResponse = Response.noContent().build();
		}

		// Calculate currency rates of source and target currency using previously
		// retrieved currencies
		Double srcCurrencyRate = cH.retrieveCurrencyRate(srcCurrency, listOfCurrencies);
		Double targetCurrencyRate = cH.retrieveCurrencyRate(targetCurrency, listOfCurrencies);

		// Calculate final conversion value
		apiResponse = new Converter().convertSourceToTarget(value, srcCurrencyRate, targetCurrencyRate);

		return apiResponse;
	}

	/**
	 * Given a start date, end date and currency return to the user return highest
	 * reference exchange rate that the Currency achieved for the period
	 * 
	 * @param startDateStr start date provided by user
	 * @param endDateStr   end date provided by user
	 * @param currency     currency name
	 * @return response that is returned to the user
	 */
	@Path("highestRate/{startDate}/{endDate}/{currency}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveHighestRateForGivenPeriod(@PathParam("startDate") String startDateStr,
			@PathParam("endDate") String endDateStr, @PathParam("currency") String currency) {
		
		Response apiResponse = null;
		UserInputChecker checker = new UserInputChecker();
		// Check if input is correct and convert dates into LocalDate format as we need
		// to retrieve
		LocalDate startDate = checker.dateFormatChecker(startDateStr);
		LocalDate endDate = checker.dateFormatChecker(endDateStr);
		if (!startDate.isBefore(endDate) && !startDate.isEqual(endDate)) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Start date: " + startDate.toString()
							+ " has to be lower or the same as end date: " + endDate.toString() + ".").build());
		}
		checker.isCurrencyFormatCorrect(currency);

		// Map currencies and values to list of currencies
		Map<LocalDate, List<Currency>> rangeOfHistoricalRates = new CurrencyHandler().historicalCurrencyRangeMapper(Consts.URL_TO_ZIP_FILE, startDate, endDate);
		apiResponse = new Converter().findHighestRate(rangeOfHistoricalRates, currency);
		return apiResponse;

	}

	/**
	 * Given a start date, end date and currency return to the user the average
	 * reference exchange rate of that Currency for the period
	 * 
	 * @param startDateStr start date provided by user
	 * @param endDateStr   end date provided by user
	 * @param currency     currency name
	 * @return response that is returned to the user
	 */
	@Path("averageRate/{startDate}/{endDate}/{currency}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAverageRateForGivenPeriod(@PathParam("startDate") String startDateStr,
			@PathParam("endDate") String endDateStr, @PathParam("currency") String currency) {
		
		Response apiResponse = null;
		UserInputChecker checker = new UserInputChecker();
		// Check if input is correct and convert dates into LocalDate format as we need
		// to retrieve
		LocalDate startDate = checker.dateFormatChecker(startDateStr);
		LocalDate endDate = checker.dateFormatChecker(endDateStr);
		if (!startDate.isBefore(endDate) && !startDate.isEqual(endDate)) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Start date: " + startDate.toString()
							+ " has to be lower or the same as end date: " + endDate.toString() + ".").build());
		}
		checker.isCurrencyFormatCorrect(currency);

		// Map currencies and values to list of currencies
		Map<LocalDate, List<Currency>> rangeOfHistoricalRates = new CurrencyHandler().historicalCurrencyRangeMapper(Consts.URL_TO_ZIP_FILE, startDate, endDate);
		apiResponse = new Converter().findAverageRate(rangeOfHistoricalRates, currency);
		return apiResponse;

	}
}