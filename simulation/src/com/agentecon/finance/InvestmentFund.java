package com.agentecon.finance;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.CapitalConfiguration;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.finance.Firm;
import com.agentecon.finance.TradingPortfolio;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
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
		IStock money = getMoney().hide(reserve);
		portfolio.invest(dsm, this, money.getAmount());
		portfolio.sell(dsm, this, 0.01);
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
