package nl.gogognome.snake;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Snake {

	private final int[] deltaX = new int[] { 0, 1, 0, -1 };
	private final int[] deltaY = new int[] { -1, 0, 1, 0 };

	private final int maxLen;
	private final Primes primes;

	public Snake(int maxLen) {
		this.maxLen = maxLen;
		primes = new Primes(maxLen);
	}

	public int calcBoundingSquareSize(boolean[] snake) {
		return calcBoundingSquareSize(snake, Integer.MAX_VALUE);
	}

	public int calcBoundingSquareSize(boolean[] snake, int maxIndex) {
		int x = 0;
		int y = 0;
		int direction = 0;
		Set<Point> positions = new HashSet<>();
		positions.add(new Point(x, y));

		int index = 0;
		while (positions.size() < maxLen && index <= maxIndex) {
			x += deltaX[direction];
			y += deltaY[direction];
			if (!positions.add(new Point(x, y))) {
				return Integer.MAX_VALUE; // collision detected
			}

			if (primes.isPrime(positions.size() - 1)) {
				direction = (direction +  (snake[index] ? 1 : (deltaX.length - 1))) % deltaX.length;
				index++;
			}
		}

		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Point point : positions) {
			minX = Math.min(minX, point.x);
			maxX = Math.max(maxX, point.x);
			minY = Math.min(minY, point.y);
			maxY = Math.max(maxY, point.y);
		}

		return Math.max(maxX - minX, maxY - minY);
	}
}
