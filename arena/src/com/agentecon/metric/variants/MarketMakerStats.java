package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.agentecon.ISimulation;
import com.agentecon.agent.AgentRef;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.IMarketMaker;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class MarketMakerStats extends SimStats {

	private AgentRef marketMaker;
	private TimeSeriesCollector bids, asks;
	private String classname;

	public MarketMakerStats(ISimulation sim, String classname) {
		super(sim);
		this.classname = classname;
		this.bids = new TimeSeriesCollector(getMaxDay());
		this.asks = new TimeSeriesCollector(getMaxDay());
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (marketMaker == null && firm instanceof IMarketMaker && firm.getClass().getSimpleName().equals(classname)) {
			marketMaker = firm.getReference();
		}
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		if (marketMaker != null) {
			int day = stats.getDay();
			IMarketMaker maker = (IMarketMaker) marketMaker.get();
			for (IFirm firm : getAgents().getFirms()) {
				if (firm != maker && firm.isAlive()) {
					bids.record(day, firm, maker.getBid(firm.getTicker()));
					asks.record(day, firm, maker.getAsk(firm.getTicker()));
				}
			}
			bids.flushDay(day, true);
			asks.flushDay(day, true);
		}
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		Collection<TimeSeries> allBids = TimeSeries.prefix("Bid", bids.getTimeSeries());
		Collection<TimeSeries> allAsks = TimeSeries.prefix("Ask", asks.getTimeSeries());
		return interleave(allBids, allAsks);
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
