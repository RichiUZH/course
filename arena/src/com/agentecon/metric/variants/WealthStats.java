package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Chart;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;

public class WealthStats extends SimStats {

	private TimeSeriesCollector cash, wealth;

	public WealthStats(ISimulation agents, boolean individuals) {
		super(agents);
		this.cash = new TimeSeriesCollector(individuals, getMaxDay());
		this.wealth = new TimeSeriesCollector(individuals, getMaxDay());
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		super.notifyDayEnded(stats);
		int day = stats.getDay();
		for (IAgent a : getAgents().getAgents()) {
			cash.record(day, a, a.getMoney().getAmount());
			wealth.record(day, a, a.getWealth(stats));
		}
		cash.flushDay(day, true);
		wealth.flushDay(day, true);
	}

	@Override
	public Collection<? extends Chart> getCharts() {
		Chart ch = new Chart("Cash", "Average overnight cash holdings by agent type", cash.getTypeTimeSeries());
		ch.setStacking("normal");
		return Collections.singleton(ch);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<>();
		all.addAll(TimeSeries.prefix("Cash of ", cash.getTimeSeries()));
		all.addAll(TimeSeries.prefix("Wealth of ", wealth.getTimeSeries()));
		return all;
	}

}
