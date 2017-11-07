package com.agentecon.finance;

import java.util.Collection;
import java.util.HashMap;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.Portfolio;
import com.agentecon.firm.Position;
import com.agentecon.firm.Ticker;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.util.Average;

public class MarketMaker extends Firm implements IMarketMaker, IPriceProvider, IMarketParticipant {

	private static final double BUDGET_FRACTION = 0.2;
	private static final double TARGET_OWNER_SHIP_SHARE = 0.02;

	private Portfolio portfolio;
	private HashMap<Ticker, MarketMakerPrice> priceBeliefs;

	public MarketMaker(IAgentIdGenerator id, IStock money, Collection<IFirm> firms) {
		super(id, new Endowment(money));
		this.portfolio = new Portfolio(getMoney(), false);
		this.priceBeliefs = new HashMap<Ticker, MarketMakerPrice>();
		for (IFirm firm : firms) {
			notifyFirmCreated(firm);
		}
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		IMarketParticipant.super.sellSomeGoods(market);
	}

	@Override
	public void managePortfolio(IStockMarket dsm) {
	}

	public void postOffers(IPriceMakerMarket dsm) {
		IStock money = getMoney();
		double budget = money.getAmount() * BUDGET_FRACTION;
		double budgetPerPosition = budget / priceBeliefs.size();
		for (MarketMakerPrice e : priceBeliefs.values()) {
			e.trade(dsm, this, money, budgetPerPosition);
		}
	}

	@Override
	public double notifyFirmClosed(Ticker ticker) {
		this.priceBeliefs.remove(ticker);
		return IMarketMaker.super.notifyFirmClosed(ticker);
	}

	public void notifyFirmCreated(IFirm firm) {
		Position pos = firm.getShareRegister().createPosition(false);
		portfolio.addPosition(pos);
		MarketMakerPrice prev = priceBeliefs.put(pos.getTicker(), new MarketMakerPrice(pos, TARGET_OWNER_SHIP_SHARE));
		assert prev == null;
	}

	@Override
	public double getPriceBelief(Good good) throws PriceUnknownException {
		return getPrice(good);
	}

	public double getPrice(Good output) {
		return priceBeliefs.get(output).getPrice();
	}

	@Override
	public double getBid(Ticker ticker) {
		return priceBeliefs.get(ticker).getBid();
	}

	@Override
	public double getAsk(Ticker ticker) {
		return priceBeliefs.get(ticker).getAsk();
	}

	public Average getAverageOwnershipShare() {
		Average avg = new Average();
		for (Ticker t : priceBeliefs.keySet()) {
			Position pos = portfolio.getPosition(t);
			avg.add(pos.getOwnershipShare());
		}
		return avg;
	}

	private Average getIndex() {
		Average avg = new Average();
		for (MarketMakerPrice mmp : priceBeliefs.values()) {
			avg.add(mmp.getPrice());
		}
		return avg;
	}

	@Override
	protected double calculateDividends(int day) {
//		double cash = getMoney().getAmount();
		double receivedDividend = getPortfolio().getLatestDividendIncome();
//		double portfolioValue = getPortfolio().calculateValue(this);
//		double targetCash = Math.max(MIN_CASH, OFFER_FRACTION * portfolioValue / BUDGET_FRACTION);
//		double excessCash = cash - targetCash;
		double excessAssets = calculateExcessAssets(TARGET_OWNER_SHIP_SHARE);
		return 0.0 + receivedDividend;
//		double ownerShipShare = getAverageOwnershipShare().getAverage();
//		double ownerShipBasedDividend = cash * (ownerShipShare - TARGET_OWNER_SHIP_SHARE);
//		return Math.max(0, Math.max(excessCash / 10, ownerShipBasedDividend));
	}

	private double calculateExcessAssets(double targetShareCount) {
		double excess = 0.0;
		for (Position pos: getPortfolio().getPositions()) {
			excess += getPrice(pos.getTicker()) * (pos.getAmount() - targetShareCount);
		}
		return excess;
	}

	@Override
	public MarketMaker clone() {
		return this; // TEMP todo
	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}

	@Override
	public String toString() {
		return getType() + " with " + getMoney() + ", holding " + getAverageOwnershipShare() + ", price index: " + getIndex().toFullString() + ", dividend " + getShareRegister().getAverageDividend();
	}

}
