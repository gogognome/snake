package nl.gogognome.snake;

import java.util.Random;

public class SnakeSolver {

	private final Snake snake;
	private final Random random = new Random();
	private final PrimeSnakeSolver primeSnakeSolver = new PrimeSnakeSolver();

	public static void main(String[] args) {
		new SnakeSolver(5000).findSolution(5000);
	}

	public SnakeSolver(int maxNum) {
		this.snake = new Snake(maxNum);
		PrimeSnakeSolver.showFrame(primeSnakeSolver);
	}

	public void findSolution(int maxNum) {
		SimulatedAnnealing<boolean[]> simulatedAnnealing = new SimulatedAnnealing<boolean[]>();
		boolean[] initialState = new boolean[new Primes(maxNum).getNumberPrimes()];
		initWithValidSnake(initialState);
		simulatedAnnealing.findSolution(initialState,
				(state, bestState) -> getNeighbour(state, bestState),
				(state) -> calcEnergy(state),
				(bestState, bestEnergy) -> primeSnakeSolver.solve(formatSnake(bestState), maxNum));
	}

	private void initWithValidSnake(boolean[] state) {
		String validSolution = "RLLLRRLLRLRLLRRRRLLRLRLRLLRLRRLLRRLRRRLLRLLRRLLRRLLRRLLRRLLRLRRRLLRLRRLLLRLRRRLLRRLLRRLRLLRLLRLRLRRRLLLRLRLRRLLLRRLRLLLRRRLLRRLRRLRLRRLLRRLLRLRLRRLRLRRLLRRLLLRLRLRLLLRRRLLRRRLLRLRLLLRLRRLRRLRRLRLLRRRLRLRLLLRLRRLLRLLRRLRRLRRLRRLLLRRLLRRLLRLRLLRLLRRLLRRRLRLRLRLRLRLRLRRRLRLLRLRLLRLRRLLLRLRLRRLRRLLRRLLRRLRLRLRRLLRLRLRLRRLLLRLRLRRLLRLLLRRLRLRRLLRRLRLRLLRRRLLRRLRLLRLRRRLRLLRRLRLRLLLRRRLRLRLRRRLLRRLLLRLRLRLRRLRRLRLLRRLLRLLRLRLRLRLRRLRRLLRLLLRLRRRLRLLRLLRRLLRLRLLRRLLLRRLRLRLRRLLRLLRRRRLLRRLRRLRRLLRLLLRRLRRLLRLRLLRRRLLRLLLRRLRLLRLRRRLLLRLRRRLRLRLRRLRLLRLLRLLRRRLLLRRLLLLRRRLRLRRLRRLLRLRLLRLLRRLRLRLRRRLLRLLLRRLLLRRLRRLRLRLRRLLRLLRLRLLRRRLLRLLRLRLRRLLRLRLRRRLRLLRRLLRLLRLLL";
		for (int i=0; i<state.length; i++) {
			state[i] = validSolution.charAt(i) == 'R';
		}
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

	private int calcEnergy(boolean[] state) {
		return snake.calcBoundingSquareSize(state);
	}

	private String formatSnake(boolean[] state) {
		StringBuilder sb = new StringBuilder(state.length);
		for (int i=0; i<state.length; i++) {
			sb.append(state[i] ? 'R' : 'L');
		}
		return sb.toString();
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
