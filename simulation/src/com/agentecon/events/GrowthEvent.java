package com.agentecon.events;

import com.agentecon.world.ICountry;

public abstract class GrowthEvent extends RandomEvent {

	private double births;
	private double probPerConsumer;

	public GrowthEvent(int start, double probPerConsumer) {
		super(start, 1);
		this.probPerConsumer = probPerConsumer;
		this.births = 0;
	}

	@Override
	public void execute(int day, ICountry sim) {
		this.births += sim.getAgents().getConsumers().size() * probPerConsumer;
		while (births >= 1.0) {
			execute(sim);
			births -= 1.0;
		}
//		if (rand.nextDouble() <= probability) {
//			execute(sim);
//		}
	}

	protected abstract void execute(ICountry sim);

}
