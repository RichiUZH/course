package com.agentecon.firm;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IMarketParticipant;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.goods.IStock;
import com.agentecon.market.IPriceTakerMarket;
import com.agentecon.market.IStatistics;

public class LandBuyingFarm extends Farm implements IMarketParticipant {
	
	private InvestmentStrategy investments;

	public LandBuyingFarm(IAgentIdGenerator id, IShareholder owner, IStock money, IStock land, CobbDouglasProduction prodFun, IStatistics stats) {
		super(id, owner, money, land, prodFun, stats);
		this.investments = new InvestmentStrategy(prodFun, stats.getDiscountRate());
	}

	@Override
	public void tradeGoods(IPriceTakerMarket market) {
		this.investments.invest(this, getInventory(), getFinancials(), market);
	}

}
