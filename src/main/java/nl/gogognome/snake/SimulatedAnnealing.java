package nl.gogognome.snake;

import java.util.Random;

public class SimulatedAnnealing {

	private final int maxNum;
	private final Random random = new Random();
	private final Snake snake;


	public SimulatedAnnealing(int maxNum) {
		this.maxNum = maxNum;
		this.snake = new Snake(maxNum);
	}

	public void findSolution() {
		boolean[] state = new boolean[new Primes(maxNum).getNumberPrimes()];
		initWithValidSnake(state);
		int energy = calcEnergy(state);

		boolean[] bestState = state;
		int bestEnergy = energy;
		printBestState(bestState, bestEnergy);

		long k = 0;
		while (k < Long.MAX_VALUE) {
			double temperature = 1.0/k;
			boolean[] newState = getNeighbour(state, bestState);
			int newEnergy = calcEnergy(newState);
			if (probability(energy, newEnergy, temperature) > random.nextDouble()) {
				state = newState;
				energy = newEnergy;
			}
			if (newEnergy < bestEnergy) {
				bestState = newState;
				bestEnergy = newEnergy;
				printBestState(bestState, bestEnergy);
			}
			k++;
		}
	}

	private void printBestState(boolean[] bestState, int bestEnergy) {
		System.out.println("bounding square: " + bestEnergy + " " + formatSnake(bestState));
	}

	private void initWithValidSnake(boolean[] state) {
		String validSolution = "RLLLRRLLRLRLLRRRRLLRLRLRLLRLRRLLRRLRRRLLRLLRRLLRRLLRRLLRRLLRLRRRLLRLRRLLLRLRRRLLRRLLRRLRLLRLLRLRLRRRLLLRLRLRRLLLRRLRLLLRRRLLRRLRRLRLRRLLRRLLRLRLRRLRLRRLLRRLLLRLRLRLLLRRRLLRRRLLRLRLLLRLRRLRRLRRLRLLRRRLRLRLLLRLRRLLRLLRRLRRLRRLRRLLLRRLLRRLLRLRLLRLLRRLLRRRLRLRLRLRLRLRLRRRLRLLRLRLLRLRRLLLRLRLRRLRRLLRRLLRRLRLRLRRLLRLRLRLRRLLLRLRLRRLLRLLLRRLRLRRLLRRLRLRLLRRRLLRRLRLLRLRRRLRLLRRLRLRLLLRRRLRLRLRRRLLRRLLLRLRLRLRRLRRLRLLRRLLRLLRLRLRLRLRRLRRLLRLLLRLRRRLRLLRLLRRLLRLRLLRRLLLRRLRLRLRRLLRLLRRRRLLRRLRRLRRLLRLLLRRLRRLLRLRLLRRRLLRLLLRRLRLLRLRRRLLLRLRRRLRLRLRRLRLLRLLRLLRRRLLLRRLLLLRRRLRLRRLRRLLRLRLLRLLRRLRLRLRRRLLRLLLRRLLLRRLRRLRLRLRRLLRLLRLRLLRRRLLRLLRLRLRRLLRLRLRRRLRLLRRLLRLLRLLL";
		for (int i=0; i<state.length; i++) {
			state[i] = validSolution.charAt(i) == 'R';
		}
	}

	private String formatSnake(boolean[] state) {
		StringBuilder sb = new StringBuilder(state.length);
		for (int i=0; i<state.length; i++) {
			sb.append(state[i] ? 'R' : 'L');
		}
		return sb.toString();
	}

	private double probability(int energy, int newEnergy, double temperature) {
		if (energy > newEnergy) {
			return 1.0 - (1.0 / temperature);
		} else if (newEnergy == Integer.MAX_VALUE) {
			return 0; // never transit to invalid state
		} else {
			return (1.0 / (newEnergy - energy)) * temperature;
		}
	}

	private int calcEnergy(boolean[] state) {
		return snake.calcBoundingSquareSize(state);
	}

	private boolean[] getNeighbour(boolean[] state, boolean[] bestState) {
		int diceRoll = random.nextInt(100);
		if (diceRoll < 30) {
			return switchAtLeastTwoTurns(state);
		}
		if (diceRoll < 97) {
			return createNewRandomSnake(state);
		}
		if (diceRoll < 98) {
			return switchAtLeastOneSubsequentTurns(state);
		}
		return bestState;
	}

	private boolean[] createNewRandomSnake(boolean[] state) {
		boolean[] newState = new boolean[state.length];
		int index = 0;
		int turnIndex = 0;

		try {
			boolean firstTurnOfStraight = true;
			while (index < newState.length) {
				int straightLineSize = random.nextInt(10);
				for (int i=0; i<straightLineSize; i++) {
					newState[index++] = firstTurnOfStraight;
					newState[index++] = !firstTurnOfStraight;
					newState[index++] = !firstTurnOfStraight;
					newState[index++] = firstTurnOfStraight;
				}

				if (random.nextBoolean()) {
					boolean turn = (turnIndex % 4) < 2;
					newState[index++] = turn;
					turnIndex++;
					firstTurnOfStraight = !turn;
				} else {
					turnIndex += 2;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// this exception is a trick to end the loops
		}
		return newState;
	}

	private boolean[] switchAtLeastOneSubsequentTurns(boolean[] state) {
		boolean[] newState = state.clone();
		int nrSwitches = random.nextInt(10) + 1;
		int index = random.nextInt(state.length - (nrSwitches-1));
		for (int i=0; i<nrSwitches; i++) {
			newState[index] = !newState[index];
			index++;
		}
		return newState;
	}

	private boolean[] switchAtLeastTwoTurns(boolean[] state) {
		boolean[] newState = state.clone();
		int nrSwitches = random.nextInt(4) + 2;
		for (int i=0; i<nrSwitches; i++) {
			int index = random.nextInt(state.length);
			newState[index] = !newState[index];
		}
		return newState;
	}
}
