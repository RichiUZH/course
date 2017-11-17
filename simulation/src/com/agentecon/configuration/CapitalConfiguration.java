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
import java.util.Random;

import com.agentecon.IAgentFactory;
import com.agentecon.ReflectiveAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.classloader.LocalSimulationHandle;
import com.agentecon.classloader.RemoteLoader;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;
import com.agentecon.consumer.InvestingConsumer;
import com.agentecon.consumer.LogUtilWithFloor;
import com.agentecon.consumer.Weight;
import com.agentecon.events.GrowthEvent;
import com.agentecon.events.IUtilityFactory;
import com.agentecon.events.SimEvent;
import com.agentecon.exercises.FarmingConfiguration;
import com.agentecon.exercises.HermitConfiguration;
import com.agentecon.finance.Fundamentalist;
import com.agentecon.finance.MarketMaker;
import com.agentecon.firm.Farm;
import com.agentecon.firm.IFirm;
import com.agentecon.firm.RealEstateAgent;
import com.agentecon.firm.production.CobbDouglasProduction;
import com.agentecon.firm.production.PersistentProductionFunction;
import com.agentecon.goods.IStock;
import com.agentecon.goods.Stock;
import com.agentecon.market.IStatistics;
import com.agentecon.production.IProductionFunction;
import com.agentecon.research.IInnovation;
import com.agentecon.world.ICountry;

public class CapitalConfiguration extends FarmingConfiguration implements IUtilityFactory, IInnovation {

	private static final String REAL_ESTATE_AGENT = "com.agentecon.exercise9.RealEstateAgent";
	private static final String FUND = "com.agentecon.exercise9.InvestmentFund";
	private static final String FARM_FACTORY = "com.agentecon.exercise9.FarmFactory";

	private static final int BASIC_AGENTS = 100;

	public static final double GROWTH_RATE = 0.005;

	private static final int GROW_UNTIL = 350; // day at which growth stops
	
	protected static final double START_CAPITAL = 1000;

	private Random rand = new Random(1313);
	private int maxAge;
	
	private IProductionFunction landProduction;

	public CapitalConfiguration() throws SocketTimeoutException, IOException {
		this(500);
	}

	public CapitalConfiguration(int maxAgeParam) throws SocketTimeoutException, IOException {
		super(new IAgentFactory() {

			private int number = 1;

			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment end, IUtility utility) {
				int maxAge = number++ * maxAgeParam / BASIC_AGENTS;
				return new InvestingConsumer(id, maxAge, end, utility);
			}
		}, BASIC_AGENTS);
		this.maxAge = maxAgeParam;
		this.landProduction = new PersistentProductionFunction(new CobbDouglasProduction(LAND, new Weight(MAN_HOUR, 0.8)));
		IStock[] dailyEndowment = new IStock[] { new Stock(MAN_HOUR, HermitConfiguration.DAILY_ENDOWMENT) };
		Endowment workerEndowment = new Endowment(getMoney(), new IStock[0], dailyEndowment);
		createBasicPopulation(workerEndowment);
		addMarketMakers();
		addInitialFarms();
		addRealEstateAgents(CapitalConfiguration.class.getClassLoader());
		addInvestmentFunds(CapitalConfiguration.class.getClassLoader());
		addCustomFarms((RemoteLoader) CapitalConfiguration.class.getClassLoader(), "team002");
		addCustomFarms((RemoteLoader) CapitalConfiguration.class.getClassLoader(), "team003");
		addEvent(new CentralBankEvent(POTATOE));
	}

	private void addCustomFarms(RemoteLoader parent, String team) throws IOException {
		try {
			ClassLoader loader = parent.obtainChildLoader(shouldLoadRemoteTeams() ? new GitSimulationHandle("meisser", team, false) : new LocalSimulationHandle(false));
			ILandbuyingFarmFactory factory = (ILandbuyingFarmFactory) loader.loadClass(FARM_FACTORY).newInstance();
			addEvent(new SimEvent(100, 20, 1) {
				
				@Override
				public void execute(int day, ICountry sim, IStatistics stats) {
					Farm farm = factory.considerCreatingNewFarm(sim, new Endowment(getMoney()), createProductionFunction(POTATOE), stats);
					if (farm != null) {
						sim.add(farm);
					}
				}
			});
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.err.println("Could not load custom farms due to " + e);
		}
	}

	private void addRealEstateAgents(ClassLoader loader) throws IOException {
		if (shouldLoadRemoteTeams()) {
			addCustomFirm(createRealEstateFirmFactory((RemoteLoader) loader, "team005"), 500, landProduction);
			addCustomFirm(createRealEstateFirmFactory((RemoteLoader) loader, "team007"), 500, landProduction);
		} else {
			addCustomFirm(createRealEstateFirmFactory((RemoteLoader) loader, ReflectiveAgentFactory.LOCAL), 500, landProduction);
			addCustomFirm(createRealEstateFirmFactory((RemoteLoader) loader, ReflectiveAgentFactory.LOCAL), 500, landProduction);
		}
	}

	protected ReflectiveAgentFactory createRealEstateFirmFactory(RemoteLoader loader, String source) throws IOException {
		return new ReflectiveAgentFactory((RemoteLoader) loader, source, REAL_ESTATE_AGENT) {
			@Override
			protected IFirm createDefaultFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun, Exception e) {
				return new RealEstateAgent(id, end, prodFun);
			}
		};
	}
	
	private void addCustomFirm(ReflectiveAgentFactory factory, int time, IProductionFunction prod) {
		addEvent(new SimEvent(time) {
			
			@Override
			public void execute(int day, ICountry sim) {
				Endowment end = new Endowment(new Stock(getMoney(), START_CAPITAL));
				sim.add(factory.createFirm(sim, end, landProduction));
			}
		});
	}
	
	private void addInvestmentFunds(ClassLoader loader) throws IOException {
		if (shouldLoadRemoteTeams()) {
			addCustomFirm(createFundFactory((RemoteLoader) loader, "team001"), 500, null);
			addCustomFirm(createFundFactory((RemoteLoader) loader, "team010"), 500, null);
		} else {
			addCustomFirm(createFundFactory((RemoteLoader) loader, ReflectiveAgentFactory.LOCAL), 500, null);
			addCustomFirm(createFundFactory((RemoteLoader) loader, ReflectiveAgentFactory.LOCAL), 500, null);
		}
	}

	protected ReflectiveAgentFactory createFundFactory(RemoteLoader loader, String source) throws IOException {
		return new ReflectiveAgentFactory((RemoteLoader) loader, source, FUND) {
			@Override
			protected IFirm createDefaultFirm(IAgentIdGenerator id, Endowment end, IProductionFunction prodFun, Exception e) {
				return new Fundamentalist(id, end);
			}
		};
	}

	@Override
	public int getMaxAge() {
		return maxAge;
	}

	@Override
	public LogUtilWithFloor create(int number) {
		return super.create(number).wiggle(rand);
	}
	
	private void addInitialFarms() {
		addEvent(new SimEvent(0, 0, 10) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					IStock land = new Stock(LAND, 100);
					Endowment end = new Endowment(money.getGood(), new IStock[] {money, land}, new IStock[] {} );
					sim.add(new Farm(sim, end, createProductionFunction(POTATOE)));
				}
			}
		});		
	}


	protected void addMarketMakers() {
		addEvent(new SimEvent(0, 0, 10) {

			@Override
			public void execute(int day, ICountry sim) {
				for (int i = 0; i < getCardinality(); i++) {
					IStock money = new Stock(getMoney(), 1000);
					sim.add(new MarketMaker(sim, money, sim.getAgents().getFirms()));
				}
			}
		});
	}

	protected void createBasicPopulation(Endowment workerEndowment) {
		addEvent(new GrowthEvent(0, GROWTH_RATE, false) {

			@Override
			protected void execute(ICountry sim) {
				if (sim.getDay() < GROW_UNTIL) {
					IConsumer cons = new InvestingConsumer(sim, getMaxAge(), workerEndowment, create(0));
					sim.add(cons);
				}
			}

		});
		addEvent(new GrowthEvent(GROW_UNTIL, 1.0d / getMaxAge(), false) {

			@Override
			protected void execute(ICountry sim) {
				IConsumer cons = new InvestingConsumer(sim, getMaxAge(), workerEndowment, create(0));
				sim.add(cons);
			}

		});
	}

}
