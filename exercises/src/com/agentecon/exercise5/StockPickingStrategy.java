package com.agentecon.exercise5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.agentecon.finance.IStockPickingStrategy;
import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.market.Ask;

public class StockPickingStrategy implements IStockPickingStrategy {

	private Random random; // a random number generator, useful to make a random choice
	private Portfolio portfolio;

	public StockPickingStrategy(Random random, Portfolio portfolio) {
		this.portfolio = portfolio;
		this.random = random;
	}

	/**
	 * Select a stock to buy.
	 */
	@Override
	public Ticker findStockToBuy(IStockMarket stocks) {
		Collection<Ticker> listedStocks = stocks.getTradedStocks(); 
		Ticker best = null;
		double bestYield = 0.0;
		for (Ticker ticker: listedStocks) {
			Ask ask = stocks.getAsk(ticker);
			if (ask != null) {
				double price = ask.getPrice().getPrice();
				FirmFinancials fin = stocks.getFirmData(ticker);
				double div = fin.getDailyDividendPerShare();
				double dividendYield = div / price;
				if (dividendYield > bestYield) {
					best = ticker;
					bestYield = dividendYield;
				}
			}
		}
		return best;
	}

	protected Ticker selectRandomFarm(IStockMarket stocks) {
		Collection<Ticker> listedStocks = stocks.getTradedStocks(); 
		ArrayList<Ticker> farms = new ArrayList<>();
		for (Ticker t: listedStocks) {
			if (t.getType().endsWith("Farm") && stocks.hasAsk(t)){
				farms.add(t);
			}
		}
		if (farms.isEmpty()) {
			return null;
		} else {
			return farms.get(random.nextInt(farms.size()));
		}
	}

}
