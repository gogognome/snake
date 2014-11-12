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
		boolean[] initialState = initWithValidSnake(new Primes(maxNum).getNumberPrimes());
		simulatedAnnealing.findSolution(initialState,
				(state, bestState) -> getNeighbour(state, bestState),
				(state) -> calcEnergy(state),
				(bestState, bestEnergy) -> showBestSolution(maxNum, bestState),
				(bestState, bestEnergy) -> showCurrentSolution(maxNum, bestState));
	}

	private void showBestSolution(int maxNum, boolean[] bestState) {
		String solution = formatSnake(bestState);
		System.out.println(solution);
		primeSnakeSolver.solve(solution, maxNum);
	}

	private void showCurrentSolution(int maxNum, boolean[] bestState) {
		String solution = formatSnake(bestState);
		primeSnakeSolver.show(solution, maxNum);
	}

	private boolean[] initWithValidSnake(int numTurns) {
		boolean[] state = new boolean[numTurns];
		do {
			state = createNewRandomSnake(state);
		} while (snake.calcBoundingSquareSize(state) == Integer.MAX_VALUE);
//		String validSolution = "RRLLLRLLRLLRLRLRLLRLRLLRRRLRLLRRLRRLRLRLRLRRLLLLRRLLRRLLRLLLRRLRRLRLRRLLRLLRRRLRRLLRRRLLLRLRRLRLLRRLLRRRLLRLLRLRRLRRLRLRLLRRRLRRLLRLLRLRLLRLLRLRRLLRRLRRLRRLLRRLRLLLRRLLLRRLLRLRRLRRLLRLLRRRLLLRLRLRRLRRRLLLRRRLLRLRLLLRRRLLRRRLRLRLLLRLRRLLRRRLLRRLLRRLRRLLRLRRLRLRRRLLRLLRLRLLRLRRLLLRLRLLRLRLRRLRRLLRRLLRRLLRRRLLRLRLLRLLRLRRLRLRLRRLLRLLRRRRLLRLRRLLRLRLRRRLRLLRRLRLRLRLLRLLRLLRLRRLLRRRLRRLLRLLRLRLRRLRLRRLRLRLRLRRLLRLRRRLLLLRRLLRLRLRRRLRLLLRLLRRRLLLRRRLRLLRLLRLRLRLLRRLLLRRLRLRLRRLRRLLRRRLRLLRRRLLRLLRLRRLRLLRLRRLLRLLLRLRRLLRRLRLRLRLLRLRLRRLLRRRLRLLRRLRRRLLRLRLLLRRLRRLLLLLRRRRLLRLRRLRRLRLLRLLRLLLRRRLRLLRRLLLRLRLLRRRRLLLRRRLLRLLRRLLRRLRLLLRLRLRLRLRRRLLLRRLLLRLRLRLLLRRRLRRL";
//		for (int i=0; i<state.length; i++) {
//			state[i] = validSolution.charAt(i) == 'R';
//		}
		return state;
	}

	private boolean[] getNeighbour(boolean[] state, boolean[] bestState) {
		int diceRoll = random.nextInt(100);
		if (diceRoll < 30) {
			return switchAtLeastTwoTurns(state);
		}
		if (diceRoll < 60) {
			return invertSubsequentTurns(state);
		}
		return createNewRandomSnake(state);
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

	private boolean[] invertSubsequentTurns(boolean[] state) {
		boolean[] newState = state.clone();
		int nrSwitches = random.nextInt(100) + 1;
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
}
