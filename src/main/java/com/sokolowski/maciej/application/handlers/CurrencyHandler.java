package com.sokolowski.maciej.application.handlers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.sokolowski.maciej.application.constants.Consts;
import com.sokolowski.maciej.application.model.Currency;

public class CurrencyHandler {

	private ArrayList<Currency> currencyList = new ArrayList<Currency>();

	public ArrayList<Currency> listAll() {
		return currencyList;
	}

	public void add(Currency currency) {
		if (currency != null) {
			currencyList.add(currency);
		}
	}

	/**
	 * Method to retrieve necessary files to be able to map those files to model
	 * file and create list of all the values on given date
	 * 
	 * @param url     Connection details to get the zip file
	 * @param dateStr date on which user wants to retrieve rates of currencies
	 * @return list of Currency models for a given date
	 */
	public List<Currency> historicalCurrencyMapper(String url, String dateStr) {
		FileHandler fH = new FileHandler();
		fH.retrieveFileFromUrl(url, Consts.GENERAL_PATH_TO_FILES, Consts.NAME_TO_CURRENCY_ZIP_FILE);

		List<String> listOfFilenames = fH.unzipFile(Consts.GENERAL_PATH_TO_FILES + Consts.NAME_TO_CURRENCY_ZIP_FILE);
		List<Currency> listOfCurrencies = new ArrayList<Currency>();

		// We take only first file from the list as we know that's all that's in
		// historical currency file
		try (FileReader fr = new FileReader(Consts.GENERAL_PATH_TO_FILES + listOfFilenames.get(0))) {
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			Map<String, List<String>> currencyDateMap = new TreeMap<String, List<String>>();
			// Check added so that first line can be treated differently from the rest of
			// the lines and first line can be used as currency names
			if ((line = br.readLine()) != null) {
				String[] currencyArray = line.split(Consts.REGEX_TO_SPLIT_CURRENCY);
				for (int i = 1; i < currencyArray.length; i++) {
					// ID is set up with "-1" value to match values in index of currencyValueList
					Currency currency = new Currency(i - 1, currencyArray[i]);
					listOfCurrencies.add(currency);
				}
				// Use each date as key and list of rate as value in a map
				while ((line = br.readLine()) != null) {
					String[] valuesArray = line.split(",");
					List<String> currencyValueList = new ArrayList<String>();
					for (int i = 1; i < valuesArray.length; i++) {
						currencyValueList.add(valuesArray[i]);
					}
					currencyDateMap.put(valuesArray[0], currencyValueList);
				}
			}

			if (currencyDateMap.get(dateStr) == null || currencyDateMap.get(dateStr).isEmpty()) {
				throw new WebApplicationException(Response.noContent()
						.entity("There is no currency data for this date or date is incorrect").build());
			}

			// Assign reference rate to currency model
			for (int i = 0; i < listOfCurrencies.size(); i++) {
				String currencyRef = currencyDateMap.get(dateStr).get(i);
				if (currencyRef != null && !currencyRef.isEmpty()) {
					listOfCurrencies.get(i).setReferenceRate(currencyRef);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listOfCurrencies;
	}

	/**
	 * Method to retrieve necessary files to be able to map those files to model
	 * file and create list of all the values for given period
	 * 
	 * @param url       Connection details to get the zip file
	 * @param startDate start date provided by user
	 * @param endDate   end date provided by user
	 * @return list of Currency models for a given date
	 */
	public Map<LocalDate, List<Currency>> historicalCurrencyRangeMapper(String url, LocalDate startDate,
			LocalDate endDate) {
		FileHandler fH = new FileHandler();
		fH.retrieveFileFromUrl(url, Consts.GENERAL_PATH_TO_FILES, Consts.NAME_TO_CURRENCY_ZIP_FILE);

		List<String> listOfFilenames = fH.unzipFile(Consts.GENERAL_PATH_TO_FILES + Consts.NAME_TO_CURRENCY_ZIP_FILE);
		List<Currency> listOfCurrencies = new ArrayList<Currency>();
		Map<LocalDate, List<Currency>> rangeOfCurrencyDateMap = new TreeMap<LocalDate, List<Currency>>();
		// We take only first file from the list as we know that's all that's in
		// historical currency file
		try (FileReader fr = new FileReader(Consts.GENERAL_PATH_TO_FILES + listOfFilenames.get(0))) {
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			Map<LocalDate, List<String>> currencyDateMap = new TreeMap<LocalDate, List<String>>();
			// Check added so that first line can be treated differently from the rest of
			// the lines and first line can be used as currency names
			if ((line = br.readLine()) != null) {
				String[] currencyArray = line.split(Consts.REGEX_TO_SPLIT_CURRENCY);
				for (int i = 1; i < currencyArray.length; i++) {
					// ID is set up with "-1" value to match values in index of currencyValueList
					Currency currency = new Currency(i - 1, currencyArray[i]);
					listOfCurrencies.add(currency);
				}
				// Use each date as key and list of rate as value in a map
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Consts.DATE_FORMAT);
				LocalDate date = null;
				while ((line = br.readLine()) != null) {
					String[] valuesArray = line.split(",");
					List<String> currencyValueList = new ArrayList<String>();
					for (int i = 1; i < valuesArray.length; i++) {
						currencyValueList.add(valuesArray[i]);
					}
					try {
						// If parsing returns exception is means format was incorrect
						date = LocalDate.parse(valuesArray[0], formatter);
						currencyDateMap.put(date, currencyValueList);
					} catch (DateTimeParseException e) {
						e.printStackTrace();
					}

				}
			}

			while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
				// Check if there are values on the day
				if (currencyDateMap.get(startDate) != null && !currencyDateMap.get(startDate).isEmpty()) {
					// Making copy of all currencies to always have fresh set of currencies (no
					// rates are passed from previous days)
					List<Currency> clearCopyOfCurrencies = new ArrayList<Currency>();
					for (Currency c : listOfCurrencies) {
						clearCopyOfCurrencies.add(new Currency(c));
					}

					// Assign reference rate to currency model
					for (int i = 0; i < clearCopyOfCurrencies.size(); i++) {

						String currencyRef = currencyDateMap.get(startDate).get(i);
						if (currencyRef != null && !currencyRef.isEmpty()) {
							clearCopyOfCurrencies.get(i).setReferenceRate(currencyRef);
						}

					}
					rangeOfCurrencyDateMap.put(startDate, clearCopyOfCurrencies);
				}
				startDate = startDate.plusDays(1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rangeOfCurrencyDateMap;
	}

	/**
	 * Method retrieves currency rate for a given currency name and list of
	 * currencies
	 * 
	 * @param currency         currency name for which user wants to retrieve rate
	 *                         value
	 * @param listOfCurrencies list of all currencies retrieved
	 * @return double rate value for a given currency
	 */
	public Double retrieveCurrencyRate(String currency, List<Currency> listOfCurrencies) {
		Double result;
		String strValue = null;
		boolean currencyExist = false;
		for (Currency c : listOfCurrencies) {
			if (currency.equalsIgnoreCase(c.getCurrencyName())) {
				strValue = c.getReferenceRate();
				currencyExist = true;
				break;
			}
		}
		if (currencyExist) {
			try {
				result = Double.parseDouble(strValue);
			} catch (NumberFormatException e) {
				throw new WebApplicationException(Response.noContent()
						.entity("Currency value unavailable for " + currency + ": " + e.getMessage()).build());
			}
		} else {
			throw new WebApplicationException(Response.noContent()
					.entity("There is no currency rate information for: " + currency + ".").build());
		}

		return result;
	}

}
