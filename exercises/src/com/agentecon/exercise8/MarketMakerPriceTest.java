package com.agentecon.exercise8;

import java.util.Random;

import org.junit.Test;

import com.agentecon.finance.DailyStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;

public class MarketMakerPriceTest {

	private Random rand;
	
	private IStock wallet;
	private Position shares;
	private MarketMakerPrice price;

	public MarketMakerPriceTest() {
		// Start with 1000$ and no shares
		this.rand = new Random(1313);
		this.wallet = new Stock(new Good("Money"), 1000);
		this.shares = new Position(null, new Ticker("COMP", 1), wallet.getGood(), 0, false);
		this.price = new MarketMakerPrice(wallet, shares, 10.0);
	}

	@Test
	public void test1() {
		assert price.getBid() <= price.getAsk() : "The bid must not be higher than the ask";
	}

	@Test
	public void test2() {
		Position investorPosition = new Position(null, shares.getTicker(), wallet.getGood(), 10000, true);
		IStock investorMoney = new Stock(new Good("Money"), 100000);
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 1000; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
		}
		assert Math.abs(price.getPrice() - equilibriumPrice) < 5.0 : "After 100 days, the price belief should have approached the equilibrium price of 100, but it actually is " + price;
	}

	// @Test
	// public void test() {
	// MarketMakerPrice mmp = new MarketMakerPrice(wallet, shares, initialPrice)
	// }

}
