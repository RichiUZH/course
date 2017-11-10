package com.agentecon.exercise8;

import java.util.Collection;

import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.finance.MarketMaker;
import com.agentecon.finance.MarketMakerPrice;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;

public class CustomMarketMaker extends MarketMaker {

	public CustomMarketMaker(IAgentIdGenerator id, IStock money, Collection<IFirm> firms) {
		super(id, money, firms);
	}

	@Override
	protected MarketMakerPrice createPriceBelief(IStock wallet, Position pos, double initialPrice, double targetShareCount) {
		return new MarketMakerPrice(wallet, pos, initialPrice);
	}

}
