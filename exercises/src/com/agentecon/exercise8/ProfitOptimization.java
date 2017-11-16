package com.agentecon.exercise8;

public class ProfitOptimization {
	
	public ProfitOptimization() {
	}
	
	public void run(double start, double end, double step) {
		for (double reserve = start; reserve < end; reserve += step) {
			MarketMakingTest test = new MarketMakingTest();
			System.out.println(reserve + "\t" + test.testMilking(reserve));
		}
	}
	
	public static void main(String[] args) {
		ProfitOptimization opt = new ProfitOptimization();
		opt.run(10, 1000, 10);
	}

}
