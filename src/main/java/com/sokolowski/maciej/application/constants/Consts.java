package com.sokolowski.maciej.application.constants;

public final class Consts {
	
	private Consts(){
		//this prevents any class, even native from calling constructor
		throw new AssertionError();
	}
	
	//European Central Bank connection details for CSV file
	public final static String URL_TO_ZIP_FILE = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip";
	
	//Details to handle files
	public final static String NAME_TO_CURRENCY_ZIP_FILE = "eurofxref-hist.zip";
	public final static String GENERAL_PATH_TO_FILES = "./";
	
	//Details to handle data
	public final static String REGEX_TO_SPLIT_CURRENCY = ",";
	public final static String CURRENCY_INPUT_REGEX = "[a-zA-Z]{3}";
	
	//Date pattern
	public final static String DATE_FORMAT = "yyyy-MM-dd"; 
}
