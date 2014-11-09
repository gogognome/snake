package nl.gogognome.snake;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrimeSnakeSolver extends JPanel {

	private static final long serialVersionUID = 1L;

	// Adjust to see more detail
	private static final int ZOOM = 1;

	// Set to true to print more information
	private final boolean DEBUG = false;

	public static void main(String[] args) {

		PrimeSnakeSolver solver = new PrimeSnakeSolver();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		frame.add(solver);
		frame.setVisible(true);

		solver.solve(
				"RLLRRLLRRLLRRLLRRLLRLRLLRLRLLRRLLRRLLRRLLRRLLRRLLRRLRRLLRRLLRRLLRRLLRRLLRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLRRLLRRLLRRLLRRLLRRLRLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLRRLLRLLRRLLRRLLRRLLRLRLLRRLLRRLLRRLLRRLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLRLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLRRLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLLRLRRLLRRLRLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRLRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLRRLLRRLLRRLLRRLLRRLLRRLLRRLRLRRLLRRLLRRLLRRLLRRLLRLLRRLLRRLLRRLLRRLLRRLLRRLLRLRLLRRLLRRLLRRLLRRLLRRLLRRLRRLLRRLLRRLLRLLRRLLRRLLRRLLRRLLRRLLRRLLRRLLRLRL",
				5000);
	}

	private final Snake snake = new Snake();

	private void solve(String solution, int snakeLength) {

		char[] input = solution.toCharArray();
		List<Integer> primeGaps = sieveGaps(snakeLength);

		// Validate length:
		if (input.length != primeGaps.size()) {
			throw new IllegalArgumentException("Size of steps doesn't match, "
					+ primeGaps.size() + " steps expected but "
					+ input.length + " received");
		}

		// Apply input solution to the snake:
		int stepsTaken = 0;
		for (int i = 0; i < input.length; i++) {

			int stepsUntilNextTurn = primeGaps.get(i);
			snake.step(stepsUntilNextTurn);
			snake.turn(input[i]);

			stepsTaken += stepsUntilNextTurn;
		}

		// Take the final steps to create a snake of the desired total length:
		snake.step(snakeLength - stepsTaken);

		// Calculate the final bounding square:
		int xmin = 0, ymin = 0, xmax = 0, ymax = 0;
		for(Coordinate coordinate:snake.allLocations) {
			xmax = Math.max(xmax,  coordinate.x);
			xmin = Math.min(xmin,  coordinate.x);
			ymax = Math.max(ymax,  coordinate.y);
			ymin = Math.min(ymin,  coordinate.y);
		}

		//Print the snake:
		System.out.println("Smallest bounding square/score: "+ Math.max(xmax-xmin, ymax-ymin));

		// Invalidate/repaint panel:
		invalidate();
	}

	@Override
	protected void paintComponent(Graphics g) {

		// Simple visual for debug purposes, start from the center:
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		int cx = getWidth() / 2;
		int cy = getHeight() / 2;

		if(ZOOM > 3) {

			//If the zoom is large enough, draw a grid to make it clearer
			cx -= (cx%ZOOM);
			cy -= (cy%ZOOM);

			g2d.setColor(Color.GRAY);
			for(int x = 0; x<getWidth();x+=ZOOM) {
				for(int y = 0; y<getHeight();y+=ZOOM) {
					g2d.drawOval(x-1, y-1, 1, 1);
				}
			}
		}

		g2d.setColor(Color.BLACK);
		for (int i = 0; i < snake.allLocations.size() - 1; i++) {
			Coordinate c1 = snake.allLocations.get(i);
			Coordinate c2 = snake.allLocations.get(i + 1);
			g2d.setStroke(new BasicStroke(4));
			g2d.drawLine(
					cx + (c1.x * ZOOM),
					cy + (c1.y * ZOOM),
					cx + (c2.x * ZOOM),
					cy + (c2.y * ZOOM));
		}

		// Show origin
		g2d.setColor(Color.RED);
		g2d.drawRect(cx - 1, cy - 1, 1, 1);
	}

	/**
	 * Store the state of the snake
	 */
	private class Snake {

		private final int LEFT = -1;
		private final int RIGHT = 1;

		private final Coordinate[] DIRECTIONS = new Coordinate[] {
				new Coordinate(0, -1), // North
				new Coordinate(1, 0),  // East
				new Coordinate(0, 1),  // South
				new Coordinate(-1, 0)  // West
		};

		// Our current heading (pointer into DIRECTIONS array), start going north
		private int currentHeading = 0;

		// Our current location:
		private Coordinate currentLocation = new Coordinate(0, 0);

		// All the previously visited locations:
		private final List<Coordinate> allLocations = new ArrayList<Coordinate>();

		public Snake() {
			//Add initial position:
			allLocations.add(currentLocation);

			if(DEBUG) {
				System.out.println(currentLocation + " <- start");
			}
		}

		/**
		 * Take N steps in the current direction
		 */
		private void step(int length) {
			if(DEBUG) {
				System.out.println("Take steps: " + length);
			}

			for (int i = 0; i < length; i++) {

				// New location:
				currentLocation = new Coordinate(
						currentLocation.x + DIRECTIONS[currentHeading].x,
						currentLocation.y + DIRECTIONS[currentHeading].y);

				if(DEBUG) {
					System.out.println(currentLocation);
				}

				// Check if there is a crossing (slow method, going through a list)
				if (allLocations.contains(currentLocation)) {
					if(DEBUG) {
						System.out.println("Oh no, a crossing!");
						System.out.println("This is the path: ");
						System.out.println(allLocations);
					}
					throw new IllegalArgumentException("Crossing detected at: "
							+ currentLocation + " after "
							+ allLocations.size() + " steps");
				}
				allLocations.add(currentLocation);
			}
		}

		/**
		 * Turn the snake [L]eft or [R]ight
		 *
		 * @param L or R
		 */
		private void turn(char direction) {
			if(DEBUG) {
				System.out.println("Turn " + direction);
			}
			if (direction == 'L') {
				currentHeading = (4 + (currentHeading + LEFT)) % 4;
			} else {
				currentHeading = (currentHeading + RIGHT) % 4;
			}
		}
	}

	private class Coordinate {

		private final int x;
		private final int y;

		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Coordinate)) {
				return false;
			}
			Coordinate other = (Coordinate) obj;
			return other.x == x && other.y == y;
		}
	}

	/**
	 * Sieve all the primes up to a certain number and return all the gaps.
	 */
	private List<Integer> sieveGaps(int N) {

		// Sieve of Eratosthenes
		boolean[] isPrime = new boolean[N + 1];
		for (int i = 2; i <= N; i++) {
			isPrime[i] = true;
		}
		for (int i = 2; i * i <= N; i++) {
			if (isPrime[i]) {
				for (int j = i; i * j <= N; j++) {
					isPrime[i * j] = false;
				}
			}
		}

		// Return the gaps:
		List<Integer> gaps = new ArrayList<Integer>();
		int lastPrime = 0;
		for (int i = 2; i <= N; i++) {
			if (isPrime[i]) {
				gaps.add(i - lastPrime);
				lastPrime = i;
			}
		}
		return gaps;
	}

}