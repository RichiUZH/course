package com.agentecon.exercise9;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.Farm;
import com.agentecon.firm.IStockMarket;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.IStock;
import com.agentecon.market.Ask;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;

public class LandBuyingFarm extends Farm implements IMarketParticipant {
	
	private static double MINIMUM_AMOUNT_OF_LAND = 10;
	private static double VALUATION = 5000;
	
	/**
	 * Your farms start with an empty inventory. No land. No money.
	 * It will need to raise some money first by selling its own shares to the first investors.
	 * Here, you should aim at raising as much money as possible by setting the price high, but not too high.
	 * 
	 * The statistics might help in figuring out what a good valuation is.
	 */
	public LandBuyingFarm(IAgentIdGenerator id, Endowment end, CobbDouglasProduction prodFun, IStatistics stats) {
		super(id, end, prodFun, stats);
	}
	
	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// we should buy additional land if we need some or we should sell it if we have too much
		// You can look at com.agentecon.firm.InvestmentStrategy for some inspiration
		IStock land = getLand();
		Ask ask = market.getAsk(land.getGood());
		if (ask != null && (getMoney().getAmount() > 1000 || land.getAmount() < MINIMUM_AMOUNT_OF_LAND)) {
			ask.accept(this, getMoney(), land, ask.getQuantity());
		}
	}
	
	/**
	 * After having been founded, the farm is owned by itself.
	 * In order to get any money so it can buy land and start its production, it sells its own shares
	 * The valuation is the minimal price the farm is willing to start selling its
	 * shares. As there are 100 shares per form, a valuation of 2000 would imply a share price of 20.
	 * Thus, the valuation is the minimum amount of money you get if you succeed in selling all shares.
	 * However, if you set the valuation too high or if the investment funds do not choose to buy any shares,
	 * raising capital might fail.
	 */
	@Override
	public double raiseCapital(IStockMarket stockmarket, double valuation) {
		double ownSharesBefore = getShareRegister().getTotalShareCount() - getShareRegister().getFreeFloatShares();
		double raised = super.raiseCapital(stockmarket, VALUATION);
		double ownSharesAfter = getShareRegister().getTotalShareCount() - getShareRegister().getFreeFloatShares();
		if (ownSharesAfter != ownSharesBefore) {
//			System.out.println("Raised " + raised + " by selling own " + (ownSharesBefore - ownSharesAfter) +" shares");
		}
		return raised;
	}
	
	@Override
	public void offer(IPriceMakerMarket market) {
		if (getLand().getAmount() > MINIMUM_AMOUNT_OF_LAND) {
			super.offer(market);
		}
	}

	@Override
	public void produce() {
		super.produce();
	}
	
	@Override
	protected double calculateDividends(int day) {
		if (getLand().getAmount() > MINIMUM_AMOUNT_OF_LAND) {
			return super.calculateDividends(day);
		} else {
			// no dividend until we have acquired some land
			// or maybe we should to lure value investors into buying out stock so we can raise more money?
			return 0.0;
		}
	}

}
