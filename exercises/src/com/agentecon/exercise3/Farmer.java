/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise3;

import com.agentecon.Simulation;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
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
public class Farmer extends Consumer implements IFounder {
	
	private static double capitalBuffer = 0.8;
	private static double farmBuffer = 0.0;

	public static final double MINIMUM_WORKING_HOURS = 5;

	private Good manhours;

	public Farmer(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(HermitConfiguration.MAN_HOUR);
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
		Inventory reducedInv = inv.hideRelative(getMoney().getGood(), capitalBuffer);
		super.trade(reducedInv, market);
	}
	public static void setCapitalBuffer(double buffer) {
		capitalBuffer = buffer;
	}
	public static void setFarmBuffer(double buffer) {
		farmBuffer = buffer;
	}

	@Override
	public double consume() {
		return super.consume();
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) {
		// Create the simulation configuration and specify which agent classes should participate
		// The simulation will create multiple instances of every class.
		
		
		// In case you want to test a setting with two different types of farmers, you configure the simulation like this:
//		FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class, AlternateFarmer.class);

		System.out.print("Creating and running the simulation...");
		// Create the simulation based on that configuration
		int test=3;
if(test==1) {
	for(int i =0;i<10;i++) {
		setCapitalBuffer(i/10.f);
		FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class);
		Simulation sim = new Simulation(configuration);
		sim.run(); // run the simulation
		
		System.out.println(sim.getStatistics().getGoodsMarketStats().getStats(HermitConfiguration.MAN_HOUR));
	}
}if(test==2){
	for(int i =0;i<100;i++) {
		FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class);
		configuration.addEvent(new InterestEvent(0.001+i/100.f, 10));
		Simulation sim = new Simulation(configuration);
		sim.run(); // run the simulation
		System.out.println(Math.round((0.001+i/100.f)*1000)/1000f+"\t"+sim.getStatistics().getGoodsMarketStats().getStats(FarmingConfiguration.POTATOE));
	}
}if(test==3) {
		for(int i =0;i<24;i++) {
			int factor=1;
			if(i<10)factor=1;
			else if(i<15)factor=10;
			else if(i<20)factor=50;
			else if(i<25)factor=1000;
			FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class);
			configuration.addEvent(new HelicopterMoneyEvent(0, 1, 1, i*factor));
			Simulation sim = new Simulation(configuration);
			sim.run(); // run the simulation
			System.out.println(i*factor+"\t\t"+sim.getStatistics().getGoodsMarketStats().getStats(FarmingConfiguration.POTATOE)+"\t\t"+sim.getStatistics().getAverageUtility());
			//System.out.println(i*factor+"\t\t"+sim.getStatistics().getGoodsMarketStats().getStats(HermitConfiguration.MAN_HOUR)+"\t\t"+sim.getStatistics().getAverageUtility());

			}		
		
	}else {
	
	FarmingConfiguration configuration = new FarmingConfiguration(Farmer.class);
	//configuration.addEvent(new InterestEvent(0.001, 10));
	Simulation sim = new Simulation(configuration);
	sim.run(); // run the simulation
    configuration.diagnoseResult(System.out, sim);
	
}
		
		// The configuration has a nice method to analyse the simulation for relevant metrics


		System.out.println();
		System.out.println("A more advanced way of running the simulation is to start the class com.agentecon.web.SimulationServer from the Arena project.");
	}

}
