package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

import com.agentecon.ISimulation;
import com.agentecon.agent.AgentRef;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.util.InstantiatingHashMap;

public class MarketMakerStats extends SimStats {

	private ArrayList<AgentRef> marketMakers;
	private TimeSeriesCollector bids, asks, size;
	private String classname;

	public MarketMakerStats(ISimulation sim, String classname) {
		super(sim);
		this.classname = classname;
		this.marketMakers = new ArrayList<>();
		this.bids = new TimeSeriesCollector(false, getMaxDay());
		this.asks = new TimeSeriesCollector(false, getMaxDay());
		this.size = new TimeSeriesCollector(getMaxDay());
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (firm instanceof IMarketMaker && firm.getClass().getSimpleName().equals(classname)) {
			marketMakers.add(firm.getReference());
		}
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		HashMap<IFirm, Spread> spreads = new InstantiatingHashMap<IFirm, Spread>() {

			@Override
			protected Spread create(IFirm key) {
				return new Spread();
			}
		};
		for (AgentRef ref : marketMakers) {
			IMarketMaker maker = (IMarketMaker) ref.get();
			for (IFirm firm : getAgents().getFirms()) {
				if (firm != maker && firm.isAlive()) {
					spreads.get(firm).record(maker.getBid(firm.getTicker()), maker.getAsk(firm.getTicker()));
				}
			}
		}
		int day = stats.getDay();
		for (AgentRef ref : marketMakers) {
			size.record(day, ref.get(), ref.get().getWealth(stats));
		}
		spreads.forEach(new BiConsumer<IFirm, Spread>() {

			@Override
			public void accept(IFirm t, Spread u) {
				bids.record(day, t, u.bid);
				asks.record(day, t, u.ask);
			}
		});

		bids.flushDay(day, true);
		asks.flushDay(day, true);
		size.flushDay(day, true);
	}

	class Spread {

		private double bid = 0.0;
		private double ask = Double.MAX_VALUE;

		public void record(double bid, double ask) {
			this.bid = Math.max(bid, this.bid);
			this.ask = Math.min(ask, this.ask);
		}

	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> allBids = TimeSeries.prefix("Bid", bids.getTimeSeries());
		Collection<TimeSeries> allAsks = TimeSeries.prefix("Ask", asks.getTimeSeries());
		Collection<TimeSeries> temp = interleave(allBids, allAsks);
		temp.addAll(TimeSeries.prefix("Wealth of ", size.getTimeSeries()));
		return temp;
	}

	private Collection<TimeSeries> interleave(Collection<TimeSeries> allBids, Collection<TimeSeries> allAsks) {
		ArrayList<TimeSeries> ts = new ArrayList<>(allBids.size() + allAsks.size());
		Iterator<TimeSeries> iter1 = allBids.iterator();
		Iterator<TimeSeries> iter2 = allAsks.iterator();
		while (iter1.hasNext()) {
			ts.add(iter1.next());
			if (iter2.hasNext()) {
				ts.add(iter2.next());
			}
		}
		while (iter2.hasNext()) {
			ts.add(iter2.next());
		}
		return ts;
	}

}
