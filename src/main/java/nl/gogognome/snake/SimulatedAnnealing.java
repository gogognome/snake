package nl.gogognome.snake;

import java.util.Random;

public class SimulatedAnnealing<S> {

	private final Random random = new Random();

	public static interface Neighbour<S> {
		S getNeighbour(S state, S bestState);
	}

	public static interface EnergyCalc<S> {
		int calcEnergy(S state);
	}

	public static interface Solution<S> {
		void foundSolution(S bestState, int bestEnergy);
	}

	public SimulatedAnnealing() {
	}

	public void findSolution(S initialState, Neighbour<S> neighbour, EnergyCalc<S> energyCalc, Solution<S> optimalSolution,
			Solution<S> currentSolution) {
		S state = initialState;
		int energy = energyCalc.calcEnergy(state);

		S bestState = state;
		int bestEnergy = energy;
		optimalSolution.foundSolution(bestState, bestEnergy);

		long k = 0;
		while (k < Long.MAX_VALUE) {
			double temperature = Math.min(1.0, 1.0/((double) (k)/(double) Integer.MAX_VALUE));
			S newState = neighbour.getNeighbour(state, bestState);
			int newEnergy = energyCalc.calcEnergy(newState);
			double transitionProbability = probability(energy, newEnergy, temperature);
			if (transitionProbability > random.nextDouble()) {
				state = newState;
				energy = newEnergy;
				currentSolution.foundSolution(state, energy);
			}
			if (newEnergy < bestEnergy) {
				bestState = newState;
				bestEnergy = newEnergy;
				optimalSolution.foundSolution(bestState, bestEnergy);
			}
			k++;
		}
	}

	private double probability(int energy, int newEnergy, double temperature) {
		if (energy > newEnergy) {
			return 1.0 - (0.1 * temperature);
		} else if (newEnergy == Integer.MAX_VALUE) {
			return 0; // never transit to invalid state
		} else {
			return ((double) energy / (double) newEnergy) * 0.001 * temperature;
		}
	}
}
