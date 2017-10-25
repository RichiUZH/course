/**
 * Created by Luzius Meisser on Jun 19, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.MinPopulationGrowthEvent;
import com.agentecon.events.SimEvent;
import com.agentecon.exercises.ExerciseAgentLoader;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.RealEstateAgent;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class StocksConfiguration extends FarmingConfiguration implements IUtilityFactory, IInnovation {

	private static final int BASIC_AGENTS = 30;
	public static final String BASIC_AGENT = "com.agentecon.exercise5.Investor";

	public static final double GROWTH_RATE = 0.0025;
	public static final int MAX_AGE = 500;
	private static final int GROW_UNTIL = 2000; // day at which growth stops

	public StocksConfiguration() throws SocketTimeoutException, IOException {
		this(new ExerciseAgentLoader(BASIC_AGENT), BASIC_AGENTS);
	}

	public StocksConfiguration(IAgentFactory loader, int agents) {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
				int maxAge = number++ * MAX_AGE / agents;
				return new InvestingConsumer(id, maxAge, end, utility);
			}
		}, agents);
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createBasicPopulation(workerEndowment);
		addMarketMakers();
		addEvent(new CentralBankEvent(POTATOE));
//		addCustomInvestors(loader, workerEndowment);
	}

	private void addCustomInvestors(IAgentFactory loader, Endowment end) {
		addEvent(new SimEvent(ROUNDS - MAX_AGE - 1, 0, 5) {

			private HashSet<String> types = new HashSet<>();

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < 10; i++) {
					IConsumer newConsumer = loader.createConsumer(sim, MAX_AGE, end, create(0));
					if (newConsumer != null && types.add(newConsumer.getType())) {
						sim.add(newConsumer);
					}
				}
			}
		});
	}

	protected void addMarketMakers() {
		addEvent(new SimEvent(0, 0, 5) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					sim.add(new MarketMaker(sim, money, sim.getAgents().getFirms()));
				}
			}
		});
		addEvent(new SimEvent(0, 0, 5) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					sim.add(new RealEstateAgent(sim, money, LAND));
				}
			}
		});
	}

	protected void createBasicPopulation(Endowment workerEndowment) {
		addEvent(new MinPopulationGrowthEvent(0, BASIC_AGENTS) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
		addEvent(new GrowthEvent(0, GROWTH_RATE) {

			@Override
			protected void execute(ICountry sim) {
				if (sim.getDay() < GROW_UNTIL) {
					IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
					sim.add(cons);
				}
			}

		});
		addEvent(new GrowthEvent(GROW_UNTIL, 1.0d / MAX_AGE) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, MAX_AGE, workerEndowment, create(0));
				sim.add(cons);
			}

		});
	}

}
