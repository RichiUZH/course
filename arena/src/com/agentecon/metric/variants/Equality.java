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
import com.agentecon.metric.series.AveragingTimeSeries;
import com.agentecon.metric.series.TimeSeries;

public class Equality extends SimStats implements IMarketListener {

	private static final int AGGREGATION_PERIOD = 20;
	
	private List<AveragingTimeSeries> wealth;
	private List<AveragingTimeSeries> utility;

	public Equality(ISimulation agents) {
		super(agents);
		this.wealth = new ArrayList<>();
		this.wealth.add(new AveragingTimeSeries("Wealth Gini", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini Young (<100 days)", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini Midlife (100 - 200)", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini Midlife (200 - 300)", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini Midlife (300 - 400)", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini around Retirement (380 - 420)", getMaxDay()));
		this.wealth.add(new AveragingTimeSeries("Wealth Gini Retirees", getMaxDay()));
		this.utility = new ArrayList<>();
		this.utility.add(new AveragingTimeSeries("Utility Gini", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini Young (<100 days)", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini Midlife (100 - 200)", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini Midlife (200 - 300)", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini Midlife (300 - 400)", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini around Retirement (380 - 420)", getMaxDay()));
		this.utility.add(new AveragingTimeSeries("Utility Gini Retirees", getMaxDay()));
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
			utility.get(i).add(calculateGini(data.get(i)));
		}
		if (day % AGGREGATION_PERIOD == AGGREGATION_PERIOD - 1) {
			for (AveragingTimeSeries ts: wealth) {
				ts.push(day);
			}
			for (AveragingTimeSeries ts: utility) {
				ts.push(day);
			}
		}
	}
	
	private List<List<GiniData>> getCollections(Function<IConsumer, Double> fun){
		List<GiniData> all = getAgents().getConsumers().stream().map(c -> new GiniData(c, fun.apply(c))).collect(Collectors.toList());
		List<GiniData> young = new ArrayList<>(all.stream().filter(c -> c.c.getAge() < 100).collect(Collectors.toList()));
		List<GiniData> midlife1 = new ArrayList<>(all.stream().filter(c -> c.c.getAge() >= 100 && c.c.getAge() < 200).collect(Collectors.toList()));
		List<GiniData> midlife2 = new ArrayList<>(all.stream().filter(c -> c.c.getAge() >= 200 && c.c.getAge() < 300).collect(Collectors.toList()));
		List<GiniData> midlife3 = new ArrayList<>(all.stream().filter(c -> c.c.getAge() >= 300 && c.c.getAge() < 400).collect(Collectors.toList()));
		List<GiniData> aroundRet = new ArrayList<>(all.stream().filter(c -> c.c.getAge() >= 380 && c.c.getAge() < 420).collect(Collectors.toList()));
		List<GiniData> retired = new ArrayList<>(all.stream().filter(c -> c.c.isRetired()).collect(Collectors.toList()));
		return Arrays.asList(all, young, midlife1, midlife2, midlife3, aroundRet, retired);
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> list = new ArrayList<>();
		list.addAll(AveragingTimeSeries.unwrap(wealth));
		list.addAll(AveragingTimeSeries.unwrap(utility));
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
			wealth.get(i).add(calculateGini(data.get(i)));
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
