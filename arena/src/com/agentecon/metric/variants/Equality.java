package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.agentecon.ISimulation;
import com.agentecon.agent.IAgent;
import com.agentecon.consumer.IConsumer;
import com.agentecon.goods.Good;
import com.agentecon.market.IMarket;
import com.agentecon.market.IMarketListener;
import com.agentecon.market.IStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.TimeSeries;

public class Equality extends SimStats implements IMarketListener {

	private List<TimeSeries> wealth;
	private List<TimeSeries> utility;

	public Equality(ISimulation agents) {
		super(agents);
		this.wealth = new ArrayList<>();
		this.wealth.add(new TimeSeries("Wealth Gini"));
		this.wealth.add(new TimeSeries("Wealth Gini Young (<100 days)"));
		this.wealth.add(new TimeSeries("Wealth Gini Midlife"));
		this.wealth.add(new TimeSeries("Wealth Gini Retirees"));
		this.utility = new ArrayList<>();
		this.utility.add(new TimeSeries("Utility Gini"));
		this.utility.add(new TimeSeries("Utility Gini Young"));
		this.utility.add(new TimeSeries("Utility Gini Midlife"));
		this.utility.add(new TimeSeries("Utility Gini Retirees"));
	}

	private double calculateGini(List<GiniData> list) {
		if (list.size() <= 1) {
			return 0.0;
		} else {
			double totDifference = 0.0;
			double totSum = 0.0;
			for (int i = 0; i < list.size(); i++) {
				double v1 = list.get(i).value;
				totSum += v1;
				for (int j = i + 1; j < list.size(); j++) {
					double v2 = list.get(j).value;
					totDifference += Math.abs(v1 - v2);
				}
			}
			return totDifference / (list.size() * totSum);
		}
	}

	@Override
	public void notifyGoodsMarketOpened(IMarket market) {
		market.addMarketListener(this);
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		int day = stats.getDay();
		List<List<GiniData>> data = getCollections(c -> c.getUtilityFunction().getLatestExperiencedUtility());
		assert data.size() == utility.size();
		for (int i=0; i<data.size(); i++) {
			utility.get(i).set(day, calculateGini(data.get(i)));
		}
	}
	
	private List<List<GiniData>> getCollections(Function<IConsumer, Double> fun){
		List<GiniData> all = getAgents().getConsumers().stream().map(c -> new GiniData(c, fun.apply(c))).collect(Collectors.toList());
		List<GiniData> young = new ArrayList<>(all.stream().filter(c -> c.c.getAge() < 100).collect(Collectors.toList()));
		List<GiniData> midlife = new ArrayList<>(all.stream().filter(c -> c.c.getAge() >= 100 && !c.c.isRetired()).collect(Collectors.toList()));
		List<GiniData> retired = new ArrayList<>(all.stream().filter(c -> c.c.isRetired()).collect(Collectors.toList()));
		return Arrays.asList(all, young, midlife, retired);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(wealth);
		list.addAll(utility);
		return list;
	}

	@Override
	public void notifyTraded(IAgent seller, IAgent buyer, Good good, double quantity, double payment) {
	}

	@Override
	public void notifyTradesCancelled() {
	}

	@Override
	public void notifyMarketClosed(int day) {
		// Calculate wealth after market close so goods that will be consumed soon are included
		IStatistics stats = getStats();
		List<List<GiniData>> data = getCollections(c -> c.getWealth(stats));
		assert data.size() == wealth.size();
		for (int i=0; i<data.size(); i++) {
			wealth.get(i).set(day, calculateGini(data.get(i)));
		}
	}
	
	class GiniData {
		IConsumer c;
		double value;
		
		public GiniData(IConsumer c, double value) {
			this.c = c;
			this.value = value;
		}
	}

}
