package com.agentecon.exercise8;

import java.util.Random;

import org.junit.Test;

import com.agentecon.finance.AbstractMarketMaking;
import com.agentecon.finance.DailyStockMarket;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.Ask;
import com.agentecon.market.Bid;
import com.agentecon.util.MovingCovarianceAlt;

public class MarketMakingTest {

	private Random rand;

	private IStock wallet;
	private Position shares;
	private AbstractMarketMaking price;

	private Position investorPosition;
	private IStock investorMoney;

	public MarketMakingTest() {
		// Start with 1000$ and 5 shares
		this.rand = new Random(1313);
		this.wallet = new Stock(new Good("Money"), 1000);
		this.shares = new Position(null, new Ticker("COMP", 1), wallet.getGood(), 2.0, false);
		this.price = new MarketMaking(wallet, shares, 10.0, 2.0);
		this.investorPosition = new Position(null, shares.getTicker(), wallet.getGood(), 10000, true);
		this.investorMoney = new Stock(new Good("Money"), 100000);
	}

	// Test buying.
	@Test
	public void testBuying() {
		double initialPrice = price.getAsk();
		for (int day = 0; day < 5; day++) {
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
		for (int day = 0; day < 5; day++) {
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
//			printDiagnostics(day);
		}
		assert Math.abs(price.getPrice() - equilibriumPrice) < 5.0 : "After 100 days, the price belief should have approached the equilibrium price of 100, but it actually is " + price;
	}
	
	@Test
	public void testMilking() {
		double dividends = testMilking(1000);
		System.out.println(dividends);
	}
		
	public double testMilking(double reserve) {
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 100; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
		}
		double dividends = 0.0;
//		double profitReserve = 0.0;
		MovingCovarianceAlt profitMaximization = new MovingCovarianceAlt(0.8);
		for (int day = 0; day < 1000; day++) {
			double moneyBefore = wallet.getAmount();
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
			double profit = wallet.getAmount() - moneyBefore;
//			profitReserve += profit;
			profitMaximization.add(moneyBefore, profit);
			double dividend = Math.max(0, wallet.getAmount() - reserve);
			double corr = profitMaximization.getCorrelation();
//			double dividend = corr < 0.0 ? wallet.getAmount() * Math.min(0.5, -corr) : 0.0;
			wallet.remove(dividend);
//			profitReserve -= dividend;
//			System.out.println(wallet.getAmount() + "\t" + profit + "\t" + profitMaximization.getCorrelation() + "\t" + dividend);
			dividends += dividend;
//			printDiagnostics(day);
		}
		return dividends;
	}

	@Test
	public void testInventory() {
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 1000; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
//			printDiagnostics(day);
		}
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
		assert Math.abs(price.getPrice() - equilibriumPrice) < 5.0 : "The price belief should have approached the equilibrium price of 100, but it actually is " + price;
	}
	
	/**
	 * Can your algorithm recover from an empty inventory?
	 */
	@Test
	public void testRecovery() {
		shares.consume(); // destroy all shares :)
		double equilibriumPrice = 100;
		double dailySharesTraded = 0.1;
		for (int day = 0; day < 500; day++) {
			DailyStockMarket dsm = new DailyStockMarket(null, rand);
			price.trade(dsm, null);
			dsm.buy(null, investorPosition.getTicker(), investorPosition, investorMoney, equilibriumPrice * dailySharesTraded);
			dsm.sell(null, investorPosition, investorMoney, dailySharesTraded);
//			printDiagnostics(day);
		}
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
