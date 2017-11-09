package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.IRegister;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.util.Numbers;

public class OldMarketMakerPrice extends AbstractMarketMakerPrice {

	static final double MIN_PRICE = 1.0;
	private static final double INITIAL_PRICE_BELIEF = 10;

	public static final double MIN_SPREAD = 0.01;
	public static final double SPREAD_MULTIPLIER = 1.0 + MIN_SPREAD;

	private double targetSharesOwned;

	public OldMarketMakerPrice(IStock pos, double targetOwnerShipShare) {
		this.targetSharesOwned = targetOwnerShipShare * IRegister.SHARES_PER_COMPANY;
		this.floor = new FloorFactor(pos, new ExpSearchBelief(0.1, INITIAL_PRICE_BELIEF / SPREAD_MULTIPLIER) {
			@Override
			protected double getMax() {
				return 0.1;
			}

		});
		this.ceiling = new CeilingFactor(pos, new ExpSearchBelief(0.1, INITIAL_PRICE_BELIEF * SPREAD_MULTIPLIER) {
			@Override
			protected double getMax() {
				return 0.1;
			}
		});
	}
	
	public void trade(IPriceMakerMarket dsm, IAgent owner, IStock wallet) {
		double sharesOwned = ceiling.getStock().getAmount();
		if (ceiling.getStock().getAmount() > 0.0) {
			ceiling.adapt(MIN_PRICE);
			double ownershipShare = sharesOwned / IRegister.SHARES_PER_COMPANY;
			double offerFraction = Math.sqrt(ownershipShare);
			// offer a fraction of the present shares, but offer more if we have more
			double toOffer = sharesOwned * offerFraction;
			if (sharesOwned - targetSharesOwned > toOffer) {
				toOffer = sharesOwned - targetSharesOwned;
			}
			ceiling.createOffers(dsm, owner, wallet, toOffer);
		}
		if (wallet.hasSome()) {
//			double ceilingOfferSize = 5 * Math.max(budget, ceiling.getQuantity() * ceiling.getPrice());
			// double spread = (ceiling.getPrice() - floor.getPrice()) / floor.getPrice();
			// if (spread > 0.1) {
			// budget *= 10;
			// }
			floor.adapt(ceiling.getPrice() / SPREAD_MULTIPLIER);
			floor.createOffers(dsm, owner, wallet, budget);
			assert floor.getPrice() < ceiling.getPrice();
		}
	}

}
