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
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.configuration.GrowthConfiguration;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.MortalConsumer;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.DailyStockMarket;
import com.agentecon.finance.Firm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.IStockMarket;
import com.agentecon.goods.Good;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IOffer;
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
	double[] money = new double[501];
	private Good manhours;
	private double savings;
	double[] potatoPrice = new double[500];
	double consumption;
	  
	private int approach=3;

	public Farmer(IAgentIdGenerator id, int maxAge, Endowment end, IUtility utility) {
		super(id, maxAge, end, utility);
		this.savings = 0.0;
		this.manhours = end.getDaily()[0].getGood();
		assert this.manhours.equals(FarmingConfiguration.MAN_HOUR);
	}
	
	@Override
	public void managePortfolio(IStockMarket stocks) {
		// instead of investing in stocks, the farmer simply decides how much money to keep under the pillow
		// note that if this agent is a firm owner, she will still receive dividends even when retired
		
		double yesterdaysSavingsTarget = this.savings; // how much we decided to keep aside yesterday
		double spendings = getDailySpendings(); // how much we are spending on consumption goods at average
		double dividends = getPortfolio().getLatestDividendIncome(); // how much dividends did we get today?
		money[this.getAge()] = getMoney().getAmount();
		boolean retired = isRetired(); // are we retired yet
		int age = getAge(); // the current age
		int retirementAge = getRetirementAge(); // the age at which retirement starts
		int lifeEnd = getMaxAge(); // the age at which the agent dies
		
		if (retired) {
			// Stupid example heuristic: when in retirement, spend 10% of the savings
			if(approach==0) {
				this.savings = yesterdaysSavingsTarget * 0.9;
			}
			if(approach==1||approach==2||approach==3) {
				this.savings=money[this.getAge()]/(100-(this.getAge()-400))*1.001;
			}
			

		} else {
			if(approach==0) {
				this.savings = yesterdaysSavingsTarget + 1;
			}
			if(approach==1) {
	            //this.savings =money-(getPotatoes()*potatoPrice);
				this.savings =money[this.getAge()]-(getPotatoes()*potatoPrice[this.getAge()]);
			}
			if(approach==2) {
				if(this.getAge()==0) {
					this.savings =money[this.getAge()]-(getPotatoes()*potatoPrice[this.getAge()]);
					consumption=getPotatoes()*potatoPrice[this.getAge()];
				}else {
					double newConsumption=(consumption)*(potatoPrice[this.getAge()-1]/potatoPrice[this.getAge()]);
					this.savings =money[this.getAge()]-newConsumption;
					consumption=newConsumption;
				}
			}if(approach==3) {
				if(this.getAge()==0) {
					
					this.savings =money[this.getAge()]-(getPotatoes()*potatoPrice[this.getAge()]);
					consumption=getPotatoes()*potatoPrice[this.getAge()];
				}else {
					double savingsAlt=this.savings;
					this.savings =(money[this.getAge()-1]*money[this.getAge()])/(potatoPrice[this.getAge()]*0.2)+savingsAlt;

				}
			}
			// Stupid example heuristic: try to increase the savings by 5
		
			
		}
	}
	
	double getPotatoes(){
		if(this.getAge()==0) {
			double earnings=0-money[this.getAge()];
			return (earnings*this.getAge())/((this.getAge()+100)*potatoPrice[this.getAge()]);
		}else {
			double earnings=money[this.getAge()-1]-money[this.getAge()];
			return (earnings*this.getAge())/((this.getAge()+100)*potatoPrice[this.getAge()]);
		}
		
	}
	
	@Override
	public IFirm considerCreatingFirm(IStatistics statistics, IInnovation research, IAgentIdGenerator id) {
		IStock myLand = getStock(FarmingConfiguration.LAND);
		if (myLand.hasSome() && statistics.getRandomNumberGenerator().nextDouble() < 0.02) {
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
			potatoPrice[this.getAge()]=prices.getPriceBelief(FarmingConfiguration.POTATOE);
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
		// The trading inventory is created in two stages:
		// - First we hide the savings, which we want to keep for the future
		// - Second we hide a relative amount of what is left as a buffer as usual
		Inventory inventoryWithoutSavings = inv.hide(getMoney().getGood(), savings);
		/*IOffer offer = market.getOffer(FarmingConfiguration.POTATOE, false);
		System.out.println("potatoe offer:"+offer.getPrice().toString());*/
		Inventory reducedInv = inventoryWithoutSavings.hideRelative(getMoney().getGood(), CAPITAL_BUFFER);
		DailyStockMarket stock = new DailyStockMarket(null);
		managePortfolio(stock);
		
		super.workAtLeast(market, MINIMUM_WORKING_HOURS);
		super.trade(reducedInv, market);
	}

	@Override
	public double consume() {
		return super.consume();
	}

	public static void main(String[] args) {
		// You can run this to test whether the simulation actually completes
		// To analyze the results, you should use the SimulationServer
		GrowthConfiguration configuration = new GrowthConfiguration(Farmer.class);
		System.out.print("Creating and running the simulation...");
		// Create the simulation based on that configuration
		Simulation sim = new Simulation(configuration);
		
		System.out.println("Hello");
		
		long t0 = System.nanoTime();
		sim.run(); // run the simulation
		long t1 = System.nanoTime();
		System.out.println(" done after " + (t1 - t0) / 1000000 + "ms");
	}

}
