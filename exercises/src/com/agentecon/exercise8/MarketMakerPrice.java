package com.agentecon.exercise8;

import com.agentecon.agent.IAgent;
import com.agentecon.finance.AbstractMarketMakerPrice;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ConstantFactorBelief;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.learning.IBelief;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;

public class MarketMakerPrice extends AbstractMarketMakerPrice {

	private static final double RELATIVE_OFFER_SIZE = 0.10;
	private static final double TARGET_OWNERSHIP_SHARE = 0.05;

	private static final double INITIAL_SPREAD = 0.05;

	private IBelief priceBelief;
	private IBelief spreadBelief;

	private IOffer latestBid;
	private IOffer latestAsk;

	public MarketMakerPrice(IStock wallet, Position shares, double initialPrice) {
		super(wallet, shares);
		this.priceBelief = new ExpSearchBelief(initialPrice);
		this.spreadBelief = new ConstantFactorBelief(INITIAL_SPREAD, 0.1);
	}

	@Override
	public void trade(IPriceMakerMarket dsm, IAgent owner) {
		adjustPriceBelief();
		placeNewOffers(dsm, owner);
	}

	private void adjustPriceBelief() {
		if (latestBid != null && latestBid.isUsed()) {
			priceBelief.adapt(false);
		}
		if (latestAsk != null && latestAsk.isUsed()) {
			priceBelief.adapt(true);
		}
		if (getPosition().getOwnershipShare() < TARGET_OWNERSHIP_SHARE) {
			spreadBelief.adapt(true);
		} else if (getPosition().getOwnershipShare() > TARGET_OWNERSHIP_SHARE) {
			spreadBelief.adapt(false);
		}
	}

	private void placeNewOffers(IPriceMakerMarket dsm, IAgent owner) {
		double offerSize = getPosition().getAmount() * RELATIVE_OFFER_SIZE;
		double adjustmentFactor = getAdjustmentFactor();
		latestAsk = super.placeAsk(dsm, owner, offerSize / adjustmentFactor);
		double idealOfferSize = TARGET_OWNERSHIP_SHARE * RELATIVE_OFFER_SIZE * IRegister.SHARES_PER_COMPANY;
		latestBid = super.placeBid(dsm, owner, idealOfferSize * adjustmentFactor);
	}

	protected double getAdjustmentFactor() {
		double adjustmentFactor = 1.0 + spreadBelief.getValue() / 2;
		return adjustmentFactor;
	}

	@Override
	public double getBid() {
		return priceBelief.getValue() / getAdjustmentFactor();
	}

	@Override
	public double getAsk() {
		return priceBelief.getValue() * getAdjustmentFactor();
	}

}
