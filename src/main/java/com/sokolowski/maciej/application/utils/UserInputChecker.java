package com.sokolowski.maciej.application.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sokolowski.maciej.application.constants.Consts;

public class UserInputChecker {

	/**
	 * Checks if date provided is in correct format, if not it returns exception to
	 * the user and stops execution
	 * 
	 * @param dateStr date as String
	 * 
	 */
	public void dateFormatChecker(String dateStr) {
		if (dateStr == null || dateStr.isEmpty() || dateStr.trim().isEmpty()) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Date has to be provided as input ").build());
		}

		try {
			//If parsing returns exception is means format was incorrect
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate.parse(dateStr, formatter);
		} catch (DateTimeParseException e) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Couldn't parse date string: " + e.getMessage()).build());
		}
	}

	/**
	 * Check if provided currency is in correct format
	 * 
	 * @param currencyStr currency name provided as input
	 * 
	 */
	public void isCurrencyFormatCorrect(String currencyStr) {

		//If currency incorrect, return exception to end user
		if (currencyStr == null || currencyStr.isEmpty() || !currencyStr.matches(Consts.CURRENCY_INPUT_REGEX)) {
			throw new WebApplicationException(
					Response.status(Status.BAD_REQUEST).entity("Currency input is incorrect.").build());
		}

	}
}
