/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise1;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IFounder;
import com.agentecon.research.IInnovation;

/**
 * An autarkic consumer that produces its own food and does not interact with
 * others.
 */
public class Hermit extends Consumer implements IFounder {
	private IProductionFunction prodFun;
	// private double workFraction = 0.53125;
	boolean first = true;
	double maxLeisureFraction=0;


	public Hermit(IAgentIdGenerator id, Endowment end, IUtility utility) {
		super(id, end, utility);
	}

	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		if (this.prodFun == null) {
			// instead of creating a firm, the hermit will create a production function for
			// himself
			this.prodFun = research.createProductionFunction(HermitConfiguration.POTATOE);
		}
		return null;
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		// Hermit does not trade, produces instead for himself
		produce(getInventory());
	}
/*	public Double getWorkFraction(Inventory inventory) {
		double utility = 0;
		double workFraction = 0;
		double manhour = inventory.getStock(HermitConfiguration.MAN_HOUR).getAmount();
		HashMap<Double, Double> results = new HashMap<Double, Double>();
		
				this.collectDailyEndowment();
				Inventory testInventory = inventory.hide(HermitConfiguration.MAN_HOUR, workFraction * manhour);
				prodFun.produce(testInventory);
				utility = this.consume();
				results.put(workFraction, utility);
				workFraction = 0.5;
				this.collectDailyEndowment();
		return workFraction;
	}*/
			
			
	public Double getLeisureFraction(Inventory inventory) {
		double utility = 0;
		double leisureFraction = 0;
		double manhour = inventory.getStock(HermitConfiguration.MAN_HOUR).getAmount();

		HashMap<Double, Double> results = new HashMap<Double, Double>();
		if (first) {
			first = false;
			//test all fractions and add the fraction and the utility into a Hashmap
			while (leisureFraction < 1) {
				this.collectDailyEndowment();
				Inventory testInventory = inventory.hide(HermitConfiguration.MAN_HOUR, leisureFraction * manhour);
				prodFun.produce(testInventory);
				utility = this.consume();
				results.put(leisureFraction, utility);
				//Step size
				leisureFraction += 0.00001;
				System.out.println(leisureFraction+" "+utility);
			}
			//get the max entry copied from (https://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map)
			HashMap.Entry<Double, Double> maxEntry = null;
			for (HashMap.Entry<Double, Double> entry : results.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
			//reset the day for the next step in the loop
			this.collectDailyEndowment();
			maxLeisureFraction=maxEntry.getKey();
		}
		
		return maxLeisureFraction;
		
	}

	private void produce(Inventory inventory) {
		IStock currentManhours = inventory.getStock(HermitConfiguration.MAN_HOUR);
		// Play here. Maybe you find a better fraction than 60%?
		// getUtilityFunction().getWeights() might help you finding out
		// how the consumer weighs the utility of potatoes and of leisure
		// time (man-hours) relative to each other.
		double plannedLeisureTime;
		// Applying the maximized working work ratio
		this.getUtilityFunction().getLatestExperiencedUtility();
		
		//Calculating the maximum utility with different leisure fractions
		plannedLeisureTime =  currentManhours.getAmount() * getLeisureFraction(inventory);

		/*double plannedLeisureTime = currentManhours.getAmount() * 0.5;
		workFraction = workFraction + 0.005;*/

		// The hide function creates allows to hide parts of the inventory from the
		// production function, preserving it for later consumption.
		Inventory productionInventory = inventory.hide(HermitConfiguration.MAN_HOUR, plannedLeisureTime);
		prodFun.produce(productionInventory);
	}

	@Override
	// this method is only here for better explanation what is going on
	public double consume() {
		// super class already knows how to consume, let it do the work
		// System.out.println("Eating from " + getInventory());
		double utility = super.consume();
		return utility;
	}

	// The "static void main" method is executed when running a class
	public static void main(String[] args) throws SocketTimeoutException, IOException {
		HermitConfiguration config = new HermitConfiguration(null, 0);
		Endowment endowment = config.createEndowment();
		IUtility utilityFunction = config.create(0);

		Hermit bob = new Hermit(new SimpleAgentIdGenerator(), endowment, utilityFunction);
		int endOfTime = 100; // let world end after 100 days
		for (int t = 0; t < endOfTime; t++) {
			bob.collectDailyEndowment();
			bob.considerCreatingFirm(null, config, null);
			bob.tradeGoods(null);
			List<Quantity> inventoryBeforeConsumption = bob.getInventory().getQuantities();
			double utility = bob.consume();
			System.out.println("Bob achieved a utility of " + utility + " on day " + t
					+ ". Inventory before consumption was: " + inventoryBeforeConsumption);
		}
	}

}