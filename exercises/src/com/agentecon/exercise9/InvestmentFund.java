package com.agentecon.exercise9;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CapitalConfiguration;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.finance.Firm;
import com.agentecon.finance.IStockPickingStrategy;
import com.agentecon.finance.TradingPortfolio;
import com.agentecon.firm.FirmFinancials;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceTakerMarket;

public class InvestmentFund extends Firm implements IShareholder, IMarketParticipant {
	
	private double reserve;
	private TradingPortfolio portfolio;

	public InvestmentFund(IAgentIdGenerator world, Endowment end) {
		super(world, end);
		this.reserve = 1000;
		this.portfolio = new TradingPortfolio(getMoney(), false);
	}

	public void managePortfolio(IStockMarket dsm) {
		// really simple basic strategy:
		
		// 1. hide some money as reserve
		IStock money = getMoney().hide(reserve);
		
		// 2a. invest in some startups (if available)
		investInYoungFirms(dsm, money, money.getAmount() / 10);
		// 2b. spend the rest on some random shares (random choice weighted by market cap)
		portfolio.invest(new DefaultStockPickingStrategy(getPortfolio().getPositionTickers()), dsm, this, money.getAmount());
		
		// 3. sell 1% of our shares again
		portfolio.sell(dsm, this, 0.01);
		
		// Knowledge you might want to use:
		for (Ticker firm: dsm.getTradedStocks()) {
			if (firm.getType().startsWith("team003")) {
				// this firm was implemented by team003
				// possible values are: local, course, team001, team002, team003, team005, team007, team010
				// when running locally, the type starts with "local"
				// the source is followed by a dash, so the complete type looks like course-RealEstateAgent
			}
			if (firm.getType().contains("RealEstateAgent")) {
				// this is a real estate agent
			}
			// here, you get all kinds of data that might be of interest
			FirmFinancials financials= dsm.getFirmData(firm);
			
			// however, if you want to know how something changed over time, you need to track that yourself
			
		}
	}

	protected void investInYoungFirms(IStockMarket dsm, IStock money, double budget) {
		portfolio.invest(new IStockPickingStrategy() {
			
			@Override
			public Ticker findStockToBuy(IStockMarket stocks) {
				for (Ticker t: stocks.getTradedStocks()) {
					FirmFinancials fin = stocks.getFirmData(t);
					if (fin.getFirmAge() < 50 && stocks.hasAsk(t)) {
						return t;
					}
				}
				return null;
			}
		}, dsm, this, budget);
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	protected double calculateDividends(int day) {
		return getMoney().getAmount() * 0.01;
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// In case one of the farms we have invested in went bankrupt, we might have ended up
		// with some land in our inventory. In that case, let's try to sell it on the goods market.
		market.sellSome(this, getMoney(), getInventory().getStock(CapitalConfiguration.LAND));
	}
	
	@Override
	public String toString() {
		return getTicker() + " with " + portfolio;
	}

}
