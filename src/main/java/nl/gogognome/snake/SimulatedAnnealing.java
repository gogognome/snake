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
		long k = 0;
		while (k < Long.MAX_VALUE) {
			double temperature = 1.0/k;
			boolean[] newState = getNeighbour(state);
			int newEnergy = calcEnergy(newState);
			if (probability(energy, newEnergy, temperature) > random.nextDouble()) {
				state = newState;
				energy = newEnergy;
			}
			if (newEnergy < bestEnergy) {
				bestState = newState;
				bestEnergy = newEnergy;
				System.out.println("bounding square: " + bestEnergy + " " + formatSnake(bestState));
			}
			k++;
		}
	}

	private void initWithValidSnake(boolean[] state) {
		for (int i=0; i<state.length; i++) {
			state[i] = (i % 2) == 0;
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

	private boolean[] getNeighbour(boolean[] state) {
		int diceRoll = random.nextInt(100);
		if (diceRoll < 40) {
			return switchTwoSubsequentTurns(state);
		}
		if (diceRoll < 97) {
			return createNewRandomSnake(state);
		}
		return switchOneTurn(state);
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

				boolean turn = (turnIndex % 4) < 2;
				newState[index++] = turn;
				turnIndex++;
				firstTurnOfStraight = !turn;
			}
		} catch (IndexOutOfBoundsException e) {
			// this exception is a trick to end the loops
		}
		return newState;
	}

	private boolean[] switchOneTurn(boolean[] state) {
		boolean[] newState = state.clone();
		int index = random.nextInt(state.length);
		newState[index] = !newState[index];
		return newState;
	}

	private boolean[] switchTwoSubsequentTurns(boolean[] state) {
		boolean[] newState = state.clone();
		int index = random.nextInt(state.length-1);
		newState[index] = !newState[index];
		newState[index+1] = !newState[index+1];
		return newState;
	}

}
