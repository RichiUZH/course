package com.agentecon.firm;

public interface IFinancialMarketData {
	
	public default FirmFinancials getFirmData(Ticker ticker){
		throw new RuntimeException("Not implemented");
	}

}
