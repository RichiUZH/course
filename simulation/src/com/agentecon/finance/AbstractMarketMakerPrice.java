package com.agentecon.finance;

import com.agentecon.agent.IAgent;
import com.agentecon.firm.Position;
import com.agentecon.goods.IStock;
import com.agentecon.market.IOffer;
import com.agentecon.market.IPriceMakerMarket;
import com.agentecon.market.Price;

public abstract class AbstractMarketMakerPrice {

	private Position shares;
	private IStock wallet;

	public AbstractMarketMakerPrice(IStock wallet, Position shares) {
		this.wallet = wallet;
		this.shares = shares;
	}

	protected Position getPosition() {
		return shares;
	}

	protected IStock getWallet() {
		return wallet;
	}

	public abstract void trade(IPriceMakerMarket dsm, IAgent owner);

	public abstract double getBid();

	public abstract double getAsk();

	protected IOffer placeBid(IPriceMakerMarket dsm, IAgent owner, double sharesToBuy) {
		if (sharesToBuy > 0.0) {
			BidFin bid = new BidFin(owner, wallet, shares, new Price(shares.getGood(), getBid()), sharesToBuy);
			dsm.offer(bid);
			return bid;
		} else {
			return null;
		}
	}

	protected IOffer placeAsk(IPriceMakerMarket dsm, IAgent owner, double sharesToOffer) {
		if (sharesToOffer > 0.0) {
			AskFin ask = new AskFin(owner, wallet, shares, new Price(shares.getGood(), getAsk()), sharesToOffer);
			dsm.offer(ask);
			return ask;
		} else {
			return null;
		}
	}

	public double getPrice() {
		return (getBid() + getAsk()) / 2;
	}

	public double getSpread() {
		return getAsk() - getBid();
	}

	@Override
	public String toString() {
		return getBid() + " to " + getAsk();
	}

}