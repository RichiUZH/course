/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise4;

import com.agentecon.Simulation;
import com.agentecon.agent.Agent;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.configuration.GrowthConfiguration;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IPriceProvider;
import com.agentecon.production.IProductionFunction;
import com.agentecon.production.PriceUnknownException;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * Unlike the Hermit, the farmer can decide to work at other farms and to buy from others. To formalize these relationships, the farmer does not produce himself anymore, but instead uses his land to
 * found a profit-maximizing firm.
 */
public class Farmer extends MortalConsumer implements IFounder {
	
	private static final double CAPITAL_BUFFER = 0.80;
	public static final double MINIMUM_WORKING_HOURS = 5;

	private Good manhours;

	public Farmer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(FarmingConfiguration.MAN_HOUR);
	}
	
	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome() && statistics.getRandomNumberGenerator().nextDouble() < 0.05 && getAge() < 2000) {
			// I have plenty of land and feel lucky, let's see if we want to found a farm
			IProductionFunction prod = research.createProductionFunction(FarmingConfiguration.POTATOE);
			if (checkProfitability(statistics.getGoodsMarketStats(), myLand, prod)) {
				IShareholder owner = Farmer.this;
				IStock startingCapital = getMoney().hideRelative(0.5);
				Firm farm = new Farm(id, owner, startingCapital, myLand, prod, statistics);
				farm.getInventory().getStock(manhours).transfer(getStock(manhours), 14);
				return farm;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean checkProfitability(IPriceProvider prices, IStock myLand, IProductionFunction prod) {
		try {
			Quantity hypotheticalInput = getStock(manhours).hideRelative(0.5).getQuantity();
			Quantity output = prod.calculateOutput(new Quantity(HermitConfiguration.MAN_HOUR, 12), myLand.getQuantity());
			double profits = prices.getPriceBelief(output) - prices.getPriceBelief(hypotheticalInput);
			return profits > 0;
		} catch (PriceUnknownException e) {
			return true; // market is dead, maybe we are lucky
		}
	}

	@Override
	protected void trade(Inventory inv, IPriceTakerMarket market) {
		// In the beginning, shelves can be empty and thus there is no incentive
		// to work (sell man-hours) either.
		// To kick-start the economy, we require the farmer to sell some of his
		// man-hours anyway, even if he cannot
		// buy anything with the earned money.
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);

		// After having worked the minimum amount, work some more and buy goods for consumption in an optimal balance.
		// Before calling the optimal trade function, we create a facade inventory that hides 80% of the money.
		// That way, we can build up some savings to smoothen fluctuations and to create new firms. In equilibrium,
		// the daily amount spent is the same, but more smooth over time.
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		super.trade(reducedInv, market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

	public static void main(String[] args) {
		GrowthConfiguration configuration = new GrowthConfiguration(Farmer.class);
		
		// In case you want to test a setting with two different types of farmers, you configure the simulation like this:
//		FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class, AlternateFarmer.class);

		System.out.print("Creating and running the simulation...");
		// Create the simulation based on that configuration
		Simulation sim = new Simulation(configuration);
		long t0 = System.nanoTime();
		sim.run(); // run the simulation
		long t1 = System.nanoTime();
		System.out.println(" done after " + (t1 - t0) / 1000000 + "ms");

//		configuration.diagnoseResult(System.out, sim);
	}

}
