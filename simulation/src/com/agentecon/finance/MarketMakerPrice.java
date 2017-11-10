package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.finance.AbstractMarketMaking;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;

public class MarketMakerPrice extends AbstractMarketMaking {
	
	private static final double RELATIVE_OFFER_SIZE = 0.05;
	private static final double TARGET_OWNERSHIP_SHARE = 0.05;
	
	private static final double SPREAD = 0.05;
	private static final double ADJUSTMENT_FACTOR = 1.0 + SPREAD / 2;
	
	private IBelief priceBelief;
	
	private IOffer latestBid;
	private IOffer latestAsk;
	
	public MarketMakerPrice(IStock wallet, Position shares, double initialPrice) {
		super(wallet, shares);
		this.priceBelief = new ExpSearchBelief(initialPrice);
	}

	@Override
	public void trade(IPriceMakerMarket dsm, IAgent owner) {
		adjustPriceBelief();
		placeNewOffers(dsm, owner);
	}

	private void adjustPriceBelief() {
		int shouldIncrease = 0;
		if (latestBid != null && latestBid.isUsed()) {
			shouldIncrease--;
		}
		if (latestAsk != null && latestAsk.isUsed()) {
			shouldIncrease++;
		}
		if (getPosition().getOwnershipShare() < TARGET_OWNERSHIP_SHARE) {
			shouldIncrease++;
		} else if (getPosition().getOwnershipShare() > TARGET_OWNERSHIP_SHARE){
			shouldIncrease--;
		}
		if (shouldIncrease > 0) {
			priceBelief.adapt(true);
		} else if (shouldIncrease < 0) {
			priceBelief.adapt(false);
		}
	}
	
	private void placeNewOffers(IPriceMakerMarket dsm, IAgent owner) {
		double offerSize = getPosition().getAmount() * RELATIVE_OFFER_SIZE;
		latestAsk = super.placeAsk(dsm, owner, offerSize / ADJUSTMENT_FACTOR);
		latestBid = super.placeBid(dsm, owner, Math.max(0.1, offerSize * ADJUSTMENT_FACTOR));
	}

	@Override
	public double getBid() {
		return priceBelief.getValue() / ADJUSTMENT_FACTOR;
	}

	@Override
	public double getAsk() {
		return priceBelief.getValue() * ADJUSTMENT_FACTOR;
	}

}
