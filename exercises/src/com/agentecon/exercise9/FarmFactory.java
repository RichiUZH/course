package com.agentecon.exercise9;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.ILandbuyingFarmFactory;
import com.agentecon.firm.production.CobbDouglasProductionWithFixedCost;
import com.agentecon.market.IStatistics;

/**
 * In the beginning of the simulation, your FarmFactory is instantiated. Every 20 days, the method considerCreatingNewFarm is called.
 */
public class FarmFactory implements ILandbuyingFarmFactory {

	private int farmsCreated;
	
	public FarmFactory() {
		this.farmsCreated = 0;
	}

	/**
	 * Return a new farm if you want to create one given the market statistics.
	 */
	public LandBuyingFarm considerCreatingNewFarm(IAgentIdGenerator id, Endowment end, CobbDouglasProductionWithFixedCost prodFun, IStatistics stats) {
		if (id.getRand().nextDouble() < 0.1 && farmsCreated < 1000) {
			this.farmsCreated++;
			// create a farm with a probability of 1%, so every 2000 days...
			// you should change this...
			return new LandBuyingFarm(id, end, prodFun, stats);
		} else {
			return null;
		}
	}

}
