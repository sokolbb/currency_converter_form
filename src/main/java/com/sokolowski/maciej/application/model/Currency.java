package com.sokolowski.maciej.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Currency {
	
	private int id;
    
    @JsonProperty ("currency_name")
    private String currencyName;
    
    @JsonProperty("reference_rate")
    private String referenceRate;
	
	public Currency() {
	}
	
	public Currency(Currency currency) {
		this.id = currency.getId();
		this.currencyName = currency.getCurrencyName();
		this.referenceRate = currency.getReferenceRate();
	}
	
	public Currency(int id, String currencyName) {
		this.id = id;
		this.currencyName = currencyName;
		this.referenceRate = "N/A";
	}
	
	public Currency(int id, String currencyName, String referenceRate) {
		this.id = id;
		this.currencyName = currencyName;
		this.referenceRate = referenceRate;		
	}

    public String getReferenceRate() {
		return referenceRate;
	}

	public void setReferenceRate(String referenceRate) {
		this.referenceRate = referenceRate;
	}
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
}
