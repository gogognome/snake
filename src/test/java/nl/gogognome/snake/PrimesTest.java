package nl.gogognome.snake;

import static org.junit.Assert.*;

import org.junit.Test;


public class PrimesTest {

	@Test
	public void testPriems() {
		Primes primes = new Primes(5000);

		assertTrue(primes.isPrime(2));
		assertTrue(primes.isPrime(3));
		assertFalse(primes.isPrime(4));
		assertTrue(primes.isPrime(5));
		assertTrue(primes.isPrime(17));
		assertFalse(primes.isPrime(18));

		assertEquals(669, primes.getNumberPrimes());
	}
}
