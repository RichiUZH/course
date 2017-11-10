/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise8;

import com.agentecon.agent.IAgent;
import com.agentecon.finance.AbstractMarketMaking;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceMakerMarket;

/**
 * Exercise 8: implement this class such that it passes as many tests as possible.
 * 
 * To run the test, right-click on "MarketMakingTest" and choose "Debug as.." "JUnit Test".
 * 
 * Feel free to get inspired by my (futile) attempts, MarketMakingOldVersion and MarketMakingWithSpreadBelief.
 */
public class MarketMaking extends AbstractMarketMaking {

	public MarketMaking(IStock wallet, Position shares, double initialPrice, double targetInventoryInNumberOfShares) {
		super(wallet, shares);
	}

	@Override
	public void trade(IPriceMakerMarket dsm, IAgent owner) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public double getBid() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public double getAsk() {
		throw new RuntimeException("Not implemented");
	}

}
