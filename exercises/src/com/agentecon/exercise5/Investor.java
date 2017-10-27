/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise5;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.finance.IStockPickingStrategy;
import com.agentecon.firm.IStockMarket;
import com.agentecon.util.Numbers;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class Investor extends InvestingConsumer {

	private static final double DISCOUNT_RATE = 0.98;

	private IStockPickingStrategy strategy;

	public Investor(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.strategy = new StockPickingStrategy(id.getRand(), getPortfolio());
	}

	@Override
	public void managePortfolio(IStockMarket stocks) {
		boolean retired = isRetired();
		int daysLeft = getMaxAge() - getAge() + 1;
		if (retired) {
			double proceeds = getPortfolio().sell(stocks, this, 1.0d / daysLeft);
			listeners.notifyDivested(this, proceeds); // notify listeners for inflow / outflow statistics
		} else {
			int daysToRetirement = getRetirementAge() - getAge();
			double dividends = getPortfolio().getLatestDividendIncome();
			double constantFactor = Numbers.geometricSum(DISCOUNT_RATE, daysToRetirement);
			double optimalSavings = (getDailySpendings() * (daysLeft - 1) - dividends / (1 - DISCOUNT_RATE)) / constantFactor;
			double actualInvestment = getPortfolio().invest(strategy, stocks, this, optimalSavings);
			listeners.notifyInvested(this, actualInvestment); // notify listeners for inflow / outflow statistics
		}
	}

}
