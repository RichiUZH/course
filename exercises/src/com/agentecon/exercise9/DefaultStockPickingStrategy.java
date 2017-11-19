package com.agentecon.exercise9;

import java.util.Collection;

import com.agentecon.finance.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Ticker;

public class DefaultStockPickingStrategy implements IStockPickingStrategy {

	public DefaultStockPickingStrategy(Collection<Ticker> sharesWeAlreadyHave) {
	}

	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Ticker random = stocks.getRandomStock(true);
//		int maxTries = 20;
//		while (random.getType().contains("Fund")) {
//			random = stocks.getRandomStock(true);
//			if (maxTries-- <= 0) {
//				return null;
//			}
//		}
		return random;
	}

}
