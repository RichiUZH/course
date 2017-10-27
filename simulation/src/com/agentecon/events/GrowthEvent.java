package com.agentecon.events;

import java.util.Random;

import com.agentecon.world.ICountry;

public abstract class GrowthEvent extends SimEvent {
	
	private Random rand = new Random(1332);
	private double probPerConsumer;
	
	public GrowthEvent(int start, double probPerConsumer) {
		super(start, 1, 1);
		this.probPerConsumer = probPerConsumer;
	}

	@Override
	public void execute(int day, ICountry sim) {
		double probability = sim.getAgents().getConsumers().size() * probPerConsumer;
		while (probability >= 1.0){
			execute(sim);
			probability -= 1.0;
		}
		if (rand.nextDouble() <= probability){
			execute(sim);
		}
	}

	protected abstract void execute(ICountry sim);

}
