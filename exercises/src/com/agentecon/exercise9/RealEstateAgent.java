/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise9;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.MarketMaking;
import com.agentecon.finance.Producer;
import com.agentecon.firm.InputFactor;
import com.agentecon.firm.sensor.SensorInputFactor;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;

/**
 * There are only four real estate agent in the simulation, two for each team.
 * 
 * They start with an inventory of 1000 taler and 10 units of land.
 * 
 * All four real estate agents share the same production function, and the production
 * function has a memory! So production gets harder and harder with every function call...
 */
public class RealEstateAgent extends Producer {

	private static final double DISTRIBUTION_RATIO = 0.02;

	private double minCashLevel;
	private MarketMaking priceBelief;
	private InputFactor input;

	public RealEstateAgent(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun) {
		super(id, end, prodFun);

		assert prodFun.getInputs().length == 1;
		Good manhour = prodFun.getInputs()[0];
		this.input = new SensorInputFactor(getInventory().getStock(manhour), new ExpSearchBelief(10));
		IStock land = getInventory().getStock(prodFun.getOutput());
		this.priceBelief = new MarketMaking(getMoney(), land, 10.0, 1.0);
		this.minCashLevel = getMoney().getAmount();
	}

	// calculate the value of our inventory (money, land, man-hours)
	private double calculateCapital() {
		return getInventory().calculateValue(new IPriceProvider() {

			@Override
			public double getPriceBelief(Good good) throws PriceUnknownException {
				if (input.getGood().equals(good)) {
					return input.getPrice();
				} else if (priceBelief.getTicker().equals(good)) {
					return priceBelief.getPrice();
				} else {
					throw new PriceUnknownException();
				}
			}
		});
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		this.priceBelief.trade(market, this); // trade land using the market making formula
		
		// buy some man-hours to produce additional land
		this.input.createOffers(market, this, getMoney(), getMoney().getAmount() / 10);
	}

	@Override
	public void adaptPrices() {
		input.adaptPrice();
		
		// market making already adapts prices during offer phase, no need to act here
	}
	
	@Override
	public void produce() {
		super.produce(); // just use all available man-hours to produce some land 
	}

	@Override
	protected double calculateDividends(int day) {
		return getMoney().getAmount() * DISTRIBUTION_RATIO - minCashLevel;
	}

}
