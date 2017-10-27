package com.agentecon.exercise6;

import java.util.Random;

/**
 * Just some sample code to show how one can simulate probabilistic population growth
 * that follows a random walk.
 */
public class GrowthTest {
	
	private Random rand;
	private int population;
	private int[] cohorts;
	private double growthRate;
	
	public GrowthTest(int initial, int maxAge, double growthRate) {
		this.growthRate = growthRate;
		this.rand = new Random(13);
		this.population = initial;
		this.cohorts = new int[maxAge];
		int step = Math.max(maxAge / initial, 1);
		int number = Math.max(initial / maxAge, 1);
		for (int i=0; i<maxAge; i+=step) {
			this.cohorts[i] = number;
		}
	}
	
	public int age() {
		int births = calculateBirths();
		int deaths = cohorts[cohorts.length - 1];
		System.arraycopy(cohorts, 0, cohorts, 1, cohorts.length - 1);
		cohorts[0] = births;
		population += births - deaths;
		return population;
	}
	
	
	private int calculateBirths() {
		int births = 0;
		for (int i=0; i<population; i++) {
			if (rand.nextDouble() <= growthRate) {
				births++;
			}
		}
		return births;
	}

	public static void main(String[] args) {
		int days = 1000;
		GrowthTest test = new GrowthTest(100, 500, 0.002);
		for (int i=0; i<days; i++) {
			int pop = test.age();
			System.out.println(i + "\t" + pop);
		}
	}

}
