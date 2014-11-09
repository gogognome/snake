package nl.gogognome.snake;

import static org.junit.Assert.*;

import org.junit.Test;


public class SnakeTest {

	@Test
	public void calcBoundingBoxTest() {
		assertEquals(Integer.MAX_VALUE, new Snake(10).calcBoundingSquareSize(convert("LLL")));
		assertEquals(3, new Snake(10).calcBoundingSquareSize(convert("LRL")));
	}

	private boolean[] convert(String state) {
		boolean[] result = new boolean[state.length()];
		for (int i=0; i<state.length(); i++) {
			result[i] = state.charAt(i) == 'R';
		}
		return result;
	}
}
