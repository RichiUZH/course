package com.agentecon.web.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.agentecon.ISimulation;
import com.agentecon.metric.EMetrics;
import com.agentecon.metric.SimStats;
import com.agentecon.metric.series.Point;
import com.agentecon.metric.series.TimeSeries;
import com.agentecon.metric.series.TimeSeriesData;
import com.agentecon.runner.SimulationStepper;
import com.agentecon.web.data.JsonData;

public class ChartMethod extends SimSpecificMethod {

	private static final String KEY_PREFIX = "Chart-";

	public static final String CHOICE_PARAMETER = DownloadCSVMethod.CHOICE_PARAMETER;
	public static final String ROW = "row";

	public ChartMethod(ListMethod listing) {
		super(listing);
	}

	@Override
	protected String createExamplePath() {
		String superSample = super.createExamplePath();
		int dayIndex = superSample.indexOf(Parameters.DAY);
		if (dayIndex >= 0) {
			superSample = superSample.substring(0, dayIndex);
		}
		return superSample + DownloadCSVMethod.CHOICE_PARAMETER + "=" + EMetrics.PRODUCTION.getName();
	}

	private String getCacheKey(EMetrics metric) {
		return KEY_PREFIX + metric.getName();
	}

	@Override
	public JsonData getJsonAnswer(Parameters params) throws IOException, InterruptedException {
		EMetrics metric = EMetrics.parse(params.getParam(DownloadCSVMethod.CHOICE_PARAMETER));
		if (metric == null) {
			return new ChartData();
		} else {
			SimulationStepper stepper = getSimulation(params);
			CompletableFuture<Collection<TimeSeries>> myResult = new CompletableFuture<>();
			CompletableFuture<Collection<TimeSeries>> first = (CompletableFuture<Collection<TimeSeries>>) stepper.getOrSetCachedItem(getCacheKey(metric), myResult);
			if (myResult == first) {
				try {
					ISimulation sim = stepper.getSimulation(0).getItem();
					SimStats stats = metric.createAndRegister(sim, params.getSelection(), false);
					sim.run();
					myResult.complete(stats.getTimeSeries());
				} catch (IOException | RuntimeException | Error e) {
					myResult.completeExceptionally(e);
				}
				if (myResult.isCompletedExceptionally()) {
					stepper.putCached(getCacheKey(metric), null);
				}
			} else {
				first.join();
			}
			try {
				return new ChartData(metric.getDescription(), first.get(), Arrays.asList(params.getParam(ROW).split(",")));
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}
	}

	class ChartData extends JsonData {

		private static final int MAX_OPTIONS = 16;

		private String description;
		private TimeSeriesData[] data;
		private ArrayList<String> options;

		public ChartData() {
			this("No valid metric selected", Collections.emptyList(), Collections.emptyList());
		}

		public ChartData(String description, Collection<TimeSeries> series, List<String> selected) {
			this.description = description;
			this.options = new ArrayList<>();
			for (TimeSeries ts : series) {
				if (ts.isInteresting()) {
					options.add(ts.getName());
				}
			}
			List<String> validSelection = new ArrayList<>();
			for (String sel : selected) {
				if (options.contains(sel) && !validSelection.contains(sel)) {
					validSelection.add(sel);
				}
			}
			while (options.size() > MAX_OPTIONS) {
				options.remove(options.size() - 1);
			}
			if (validSelection.isEmpty() && options.isEmpty()) {
				data = new TimeSeriesData[] { new TimeSeriesData("No data", Arrays.asList(new Point(0, 1.0f), new Point(1000, 1.0f))) };
			} else {
				if (validSelection.isEmpty()) {
					validSelection = Arrays.asList(options.get(0));
				}
				ArrayList<TimeSeriesData> data = new ArrayList<>();
				// make sure that time series have same order as selection
				for (String sel : validSelection) {
					for (TimeSeries ts : series) {
						if (sel.equals(ts.getName())) {
							data.add(ts.getRawData());
							break;
						}
					}
				}
				this.data = data.toArray(new TimeSeriesData[data.size()]);
			}
		}
	}

}
