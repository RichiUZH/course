package com.agentecon.exercise8;

import java.util.Random;

import org.junit.Test;

import com.agentecon.finance.AbstractMarketMakerPrice;
import com.agentecon.finance.DailyStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;

public class MarketMakerPriceTest {
	
	private double TOTAL_SHARES = 100;
	private double TARGET_SHARE_COUNT = 5;

	private Random rand;

	private IStock wallet;
	private Position shares;
	private AbstractMarketMakerPrice price;

	private Position investorPosition;
	private IStock investorMoney;

	public MarketMakerPriceTest() {
		// Start with 1000$ and 5 shares
		this.rand = new Random(1313);
		this.wallet = new Stock(new Good("Money"), 1000);
		this.shares = new Position(null, new Ticker("COMP", 1), wallet.getGood(), TARGET_SHARE_COUNT, false);
		this.price = new OldMarketMakerPrice(wallet, shares, TARGET_SHARE_COUNT);
		// this.price = new MarketMakerPrice(wallet, shares, 10.0);
		this.investorPosition = new Position(null, shares.getTicker(), wallet.getGood(), 10000, true);
		this.investorMoney = new Stock(new Good("Money"), 100000);
	}

	@Test
	public void testSpread() {
		assert price.getBid() <= price.getAsk() : "The bid must not be higher than the ask";
	}

	@Test
	public void testBuying() {
		double initialPrice = price.getAsk();
		for (int day = 0; day < 10; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			Ask ask = dsm.getAsk(investorPosition.getTicker());
			ask.accept(null, investorMoney, investorPosition, ask.getQuantity());
		}
		assert initialPrice < price.getAsk() : "Buying shares should move the price up";
	}

	@Test
	public void testSelling() {
		double initialPrice = price.getAsk();
		for (int day = 0; day < 10; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			Bid bid = dsm.getBid(investorPosition.getTicker());
			bid.accept(null, investorMoney, investorPosition, bid.getQuantity());
		}
		assert initialPrice > price.getAsk() : "Selling shares should move the price down";
	}

	@Test
	public void testInventoryWhenBuying() {
		for (int day = 0; day < 100; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			Ask ask = dsm.getAsk(investorPosition.getTicker());
			ask.accept(null, investorMoney, investorPosition, ask.getQuantity());
		}
		assert shares.hasSome() : "The market maker should never run out of shares";
	}

	@Test
	public void testEquilibrium() {
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 100; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
			printDiagnostics(day);
		}
		assert Math.abs(price.getPrice() - equilibriumPrice) < 5.0 : "After 100 days, the price belief should have approached the equilibrium price of 100, but it actually is " + price;
	}
	
	@Test
	public void testInventory() {
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		double initialInventory = shares.getAmount();
		for (int day = 0; day < 1000; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
			printDiagnostics(day);
		}
		assert Math.abs(initialInventory - shares.getAmount()) < initialInventory / 10 : "The inventory should recover back to " + initialInventory + " over time, but it is " + shares.getAmount();
	}

	@Test
	public void testUpAndDown() {
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 1500; day++) {
			if (day < 1000) {
				if (day % 100 == 0) {
					equilibriumPrice = 150;
				} else if (day % 50 == 0) {
					equilibriumPrice = 100;
				}
			}
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
			printDiagnostics(day);
		}
		assert Math.abs(price.getPrice() - equilibriumPrice) < 5.0 : "After 100 days, the price belief should have approached the equilibrium price of 100, but it actually is " + price;
	}

	private boolean printedLabels = false;

	private void printDiagnostics(int day) {
		if (!printedLabels) {
			System.out.println("Day\tBid\tAsk\tCash\tStocks");
			printedLabels = true;
		}
		System.out.println(day + "\t" + price.getBid() + "\t" + price.getAsk() + "\t" + wallet.getAmount() + "\t" + shares.getAmount());
	}

}
