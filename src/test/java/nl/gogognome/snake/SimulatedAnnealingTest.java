package nl.gogognome.snake;

import org.junit.Test;


public class SimulatedAnnealingTest {

	@Test
	public void solve() {
		new SimulatedAnnealing(5000).findSolution();
	}
}
