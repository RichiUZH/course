/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.firm;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.AbstractMarketMaking;
import com.agentecon.finance.Firm;
import com.agentecon.goods.Good;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IGoodsTrader;

public class RealEstateAgent extends Firm implements IGoodsTrader {
	
	private static final double DISTRIBUTION_RATIO = 0.02;
	
//	private double capital;
	private double minCashLevel;
	private AbstractMarketMaking priceBelief;
//	private QuadraticMaximizer profitModel; 
	
	public RealEstateAgent(IAgentIdGenerator id, Endowment end) {
		this(id, end, FarmingConfiguration.LAND);
	}

	public RealEstateAgent(IAgentIdGenerator id, Endowment initialMoney, Good land) {
		super(id, initialMoney);
		
		this.minCashLevel = getMoney().getAmount();
//		this.priceBelief = new MarketMakerPrice(getStock(land), 0.1);
//		this.profitModel = new QuadraticMaximizer(0.98, id.getRand().nextLong(), initialMoney.getAmount(), initialMoney.getAmount() * 1000);
//		this.capital = calculateCapital();
	}

//	private double calculateCapital() {
//		return getInventory().calculateValue(new IPriceProvider() {
//			
//			@Override
//			public double getPriceBelief(Good good) throws PriceUnknownException {
//				return priceBelief.getPrice();
//			}
//		});
//	}

	@Override
	public void offer(IPriceMakerMarket market) {
//		this.priceBelief.trade(market, this, getMoney());
	}
	
	@Override
	public void adaptPrices() {
		// done during offer phase
	}
	
	@Override
	protected double calculateDividends(int day) {
//		double profits = calculateProfits();
		return getMoney().getAmount() * DISTRIBUTION_RATIO - minCashLevel;
	}

//	private double calculateProfits() {
//		double currentCapital = calculateCapital();
//		double prevCapital = this.capital;
//		double profits = currentCapital - prevCapital;
////		this.profitModel.update(prevCapital, profits);
//		this.capital = currentCapital;
////		System.out.println(prevCapital + "\t" + profits);
//		return profits;
//	}

}
