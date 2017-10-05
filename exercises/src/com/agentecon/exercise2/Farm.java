/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercise2;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.finance.Producer;
import com.agentecon.firm.IShareholder;
import com.agentecon.firm.decisions.IFinancials;
import com.agentecon.goods.IStock;
import com.agentecon.learning.MarketingDepartment;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;

public class Farm extends Producer {

	private MarketingDepartment marketing;
	private double allprofits=0;
	private double alldividends=0;
	public Farm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, IProductionFunction prodFun, IStatistics stats) {
		super(id, owner, prodFun, stats.getMoney());
		this.marketing = new MarketingDepartment(getMoney(), stats.getGoodsMarketStats(), getStock(FarmingConfiguration.MAN_HOUR), getStock(FarmingConfiguration.POTATOE));
		getStock(land.getGood()).absorb(land);
		getMoney().absorb(money);
		assert getMoney().getAmount() > 0;
	}

	@Override
	public void offer(IPriceMakerMarket market) {
		double budget = calculateBudget();
		marketing.createOffers(market, this, budget);
	}

	private double calculateBudget() {
//		 double fixedCosts = getProductionFunction().getFixedCost(FarmingConfiguration.MAN_HOUR);
//		 double manHoursPrice = marketing.getPriceBelief(FarmingConfiguration.MAN_HOUR);
//		 double availableCash = getMoney().getAmount();
//System.out.println(availableCash);
		// Why not spending 100? :)
		getProductionFunction().getFixedCost(FarmingConfiguration.MAN_HOUR);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();
		double dividends = calculateDividends(this.getAge());
		allprofits=profits+allprofits;
		alldividends=dividends+alldividends;
		if((allprofits-alldividends)/this.getAge()>(profits-dividends)){
			
//			System.out.println((allprofits-alldividends)/this.getAge());
			return 80; 
		}else {
//			System.out.println("hello"+this.getAge());
			return 100; 
		}
		
		// Things that might or might not be useful here:
		// double fixedCosts = getProductionFunction().getFixedCost(FarmingConfiguration.MAN_HOUR);
		// double manHoursPrice = marketing.getPriceBelief(FarmingConfiguration.MAN_HOUR);
		// double availableCash = getMoney().getAmount();
		// etc.
	}

	@Override
	public void adaptPrices() {
		marketing.adaptPrices();
	}

	@Override
	public void produce() {
		super.produce();
	}
	private int daysWithProfit = 0;
	
	@Override
	protected double calculateDividends(int day) {
		double money = getMoney().getAmount();
		getProductionFunction().getFixedCost(FarmingConfiguration.MAN_HOUR);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();		
		double reserves=1100;
		
		if (profits >= 0) {
			daysWithProfit++;
		} else {
			daysWithProfit = 0;
		}
				if((money - reserves)>100) {
					return 50;
				}else {
					if(this.getAge()%5==0) {
					
						return (money - reserves)+50;
					}else {
						return  (money - reserves);
					}
					
				}
	}

	private int daysWithoutProfit = 0;

	@Override
	public boolean considerBankruptcy(IStatistics stats) {
		super.considerBankruptcy(stats);
		IFinancials fin = marketing.getFinancials(getInventory(), getProductionFunction());
		double profits = fin.getProfits();
		if (profits <= 0) {
			daysWithoutProfit++;
		} else {
			daysWithoutProfit = 0;
		}

		return daysWithoutProfit > 1000;
	}

}
