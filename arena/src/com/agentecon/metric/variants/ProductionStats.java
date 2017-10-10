// Created on May 28, 2015 by Luzius Meisser

package com.agentecon.metric.variants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.agentecon.ISimulation;
import com.agentecon.firm.IFirm;
import com.agentecon.goods.Good;
import com.agentecon.goods.Quantity;
import com.agentecon.market.IStatistics;
import com.agentecon.market.MarketStatistics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.IAgentType;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesCollector;
import com.agentecon.production.IProducer;
import com.agentecon.production.IProducerListener;
import com.agentecon.sim.IOptimalityIndicator;
import com.agentecon.util.InstantiatingHashMap;

/**
 * Compares what the firms of one type produced with what they could have produced given the input factors they acquired.
 */
public class ProductionStats extends SimStats {

	private IOptimalityIndicator[] indicators;
	private Map<Good, TimeSeries> optimalProduction;
	private Map<Good, MarketStatistics> usedInputs;
	private TimeSeriesCollector collector;

	public ProductionStats(ISimulation sim) {
		super(sim);
		this.collector = new TimeSeriesCollector();
		this.indicators = sim.getConfig().getOptimalProductionIndicators();
		this.optimalProduction = new InstantiatingHashMap<Good, TimeSeries>() {

			@Override
			protected TimeSeries create(Good key) {
				return new TimeSeries("Optimal average " + key.getName().toLowerCase() + " output given inputs");
			}
		};
		this.usedInputs = new InstantiatingHashMap<Good, MarketStatistics>() {

			@Override
			protected MarketStatistics create(Good key) {
				return new MarketStatistics();
			}
		};
	}

	protected void includeInput(Quantity[] inputs, Quantity output) {
		MarketStatistics stats = usedInputs.get(output.getGood());
		for (Quantity input : inputs) {
			if (input.getAmount() > 0.0) {
				stats.notifyTraded(null, null, input.getGood(), input.getAmount(), 1.0);
			}
		}
	}

	protected void addSingleInput(List<Quantity> list, Quantity input) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getGood().equals(input.getGood())) {
				list.set(i, new Quantity(input.getGood(), input.getAmount() + list.get(i).getAmount()));
				return;
			}
		}
		list.add(input);
	}

	@Override
	public void notifyFirmCreated(IFirm firm) {
		if (firm instanceof IProducer) {
			IProducer prod = (IProducer) firm;
			prod.addProducerMonitor(new IProducerListener() {

				@Override
				public void reportResults(IProducer inst, double revenue, double cogs, double expectedProfits) {
				}

				@Override
				public void notifyProduced(IProducer inst, Quantity[] inputs, Quantity output) {
					includeInput(inputs, output);
					collector.record(getDay(), new IAgentType() {

						@Override
						public boolean isFirm() {
							return true;
						}

						@Override
						public boolean isConsumer() {
							return false;
						}

						@Override
						public String[] getTypeKeys() {
							return new String[] { inst.getType(), output.getGood().getName().toLowerCase() + " producers" };
						}

						@Override
						public String getIndividualKey() {
							return inst.getName();
						}
					}, output.getAmount());
				}
			});
		}
	}

	@Override
	public void notifyDayEnded(IStatistics stats) {
		for (MarketStatistics market: usedInputs.values()) {
			market.notifyMarketClosed(stats.getDay());
		}
		for (IOptimalityIndicator indicator : indicators) {
			MarketStatistics volumeStatistics = usedInputs.get(indicator.getOutputGood());
			optimalProduction.get(indicator.getOutputGood()).set(stats.getDay(), indicator.getOptimum(volumeStatistics));
		}
		collector.flushDay(stats.getDay(), false);
		usedInputs.clear();
	}

	@Override
	public Collection<TimeSeries> getTimeSeries() {
		ArrayList<TimeSeries> all = new ArrayList<>();
		all.addAll(optimalProduction.values());
		all.addAll(collector.getTimeSeries());
		return all;
	}

}
