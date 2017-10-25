/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.metric;

import java.util.ArrayList;
import java.util.List;

import com.agentecon.ISimulation;
import com.agentecon.metric.variants.CashStats;
import com.agentecon.metric.variants.Demographics;
import com.agentecon.metric.variants.DividendStats;
import com.agentecon.metric.variants.InventoryStats;
import com.agentecon.metric.variants.MarketMakerStats;
import com.agentecon.metric.variants.MarketStats;
import com.agentecon.metric.variants.MonetaryStats;
import com.agentecon.metric.variants.OwnershipStats;
import com.agentecon.metric.variants.ProductionStats;
import com.agentecon.metric.variants.StockMarketStats;
import com.agentecon.metric.variants.TypeStatistics;
import com.agentecon.metric.variants.UtilityRanking;
import com.agentecon.metric.variants.UtilityStats;
import com.agentecon.web.query.AgentQuery;

public enum EMetrics {

	CASH, DEMOGRAPHICS, DIVIDENDS, INVENTORY, MARKET, MARKETMAKER, MONETARY, OWNERSHIP, STOCKMARKET, PRODUCTION, RANKING, UTILITY, TYPE;

	public SimStats createAndRegister(ISimulation sim, List<String> list, boolean details) {
		ArrayList<AgentQuery> queries = new ArrayList<>();
		for (String query : list) {
			queries.add(new AgentQuery(query));
		}
		SimStats stats = instantiate(sim, queries, details);
		sim.addListener(stats);
		return stats;
	}

	public String getDescription() {
		switch (this) {
		case CASH:
			return "Total nightly cash holdings.";
		case DEMOGRAPHICS:
			return "The size of the population and related figures.";
		case DIVIDENDS:
			return "Average dividend payments of firms.";
		case RANKING:
			return "The agent ranking over time.";
//		case FIRM:
		case INVENTORY:
			return "Average amount of goods held by firms and consumers after trading (before consumption and before production).";
		case MARKET:
			return "Average market price and trading volume of all goods, including a volume-weighted price index.";
		case MARKETMAKER:
			return "The bid and ask price beliefs of one selected market maker.";
		case MONETARY:
			return "Money supply, money velocity; prices and trade volume on the goods market.";
		case OWNERSHIP:
			return "Some general statistics on firm ownership.";
		case PRODUCTION:
			return "Production volume of each firm.";
		case STOCKMARKET:
			return "Various stock market statistics: average prices, trading volumes, p/e ratios, inflows, outflows, etc."; 
		case UTILITY:
			return "Daily average utility, as well as the minimum and maximum experienced by an agent.";
		case TYPE:
			return "How many agents of each type there are in the simulation at each point in time.";
//		case VALUATION:
		default:
			return "no description available";
		}
	}

	private SimStats instantiate(ISimulation sim, ArrayList<AgentQuery> agents, boolean details) {
		switch (this) {
		case DEMOGRAPHICS:
			return new Demographics(sim);
		case DIVIDENDS:
			return new DividendStats(sim, agents);
//		case FIRM:
//			return new FirmStats(sim);
		case INVENTORY:
			return new InventoryStats(sim, details);
		case MARKET:
			return new MarketStats(sim, true);
		case MARKETMAKER:
			return new MarketMakerStats(sim, "MarketMaker");
		case CASH:
			return new CashStats(sim);
		case MONETARY:
			return new MonetaryStats(sim);
		case OWNERSHIP:
			return new OwnershipStats(sim);
		case PRODUCTION:
			return new ProductionStats(sim, details);
		case STOCKMARKET:
			return new StockMarketStats(sim, false);
		case RANKING:
			return new UtilityRanking(sim, true);
		case UTILITY:
			return new UtilityStats(sim);
		case TYPE:
			return new TypeStatistics(sim);
//		case VALUATION:
//			return new ValuationStats(sim);
		default:
			return null;
		}
	}

	public static EMetrics parse(String metric) {
		for (EMetrics candidate : EMetrics.values()) {
			if (candidate.getName().equals(metric)) {
				return candidate;
			}
		}
		return null;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

}