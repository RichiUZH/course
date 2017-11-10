package com.agentecon.exercise8;

import com.agentecon.agent.IAgent;
import com.agentecon.finance.AbstractMarketMakerPrice;
import com.agentecon.finance.CeilingFactor;
import com.agentecon.finance.FloorFactor;
import com.agentecon.firm.IRegister;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.learning.ExpSearchBelief;
import com.agentecon.market.IPriceMakerMarket;

public class OldMarketMakerPrice extends AbstractMarketMakerPrice {

	static final double MIN_PRICE = 1.0;
	private static final double INITIAL_PRICE_BELIEF = 10;

	public static final double MIN_SPREAD = 0.01;
	public static final double SPREAD_MULTIPLIER = 1.0 + MIN_SPREAD;

	private double targetSharesOwned;
	
	private FloorFactor floor;
	private CeilingFactor ceiling;

	public OldMarketMakerPrice(IStock wallet, Position pos, double targetShareCount) {
		super(wallet, pos);
		this.targetSharesOwned = targetShareCount;
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
	

	@Override
	public void trade(IPriceMakerMarket dsm, IAgent owner) {
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
			ceiling.createOffers(dsm, owner, getWallet(), toOffer);
		}
		if (getWallet().hasSome()) {
//			double ceilingOfferSize = 5 * Math.max(budget, ceiling.getQuantity() * ceiling.getPrice());
			// double spread = (ceiling.getPrice() - floor.getPrice()) / floor.getPrice();
			// if (spread > 0.1) {
			// budget *= 10;
			// }
			floor.adapt(ceiling.getPrice() / SPREAD_MULTIPLIER);
			double budget = floor.getPrice() * targetSharesOwned;
			double maxBudget = getWallet().getAmount() / 10;
			floor.createOffers(dsm, owner, getWallet(), Math.min(maxBudget, budget));
			assert floor.getPrice() < ceiling.getPrice();
		}
	}

	@Override
	public double getBid() {
		return floor.getPrice();
	}

	@Override
	public double getAsk() {
		return ceiling.getPrice();
	}

}
