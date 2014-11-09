package nl.gogognome.snake;

public class Primes {

	public boolean primes[];

	public Primes(int maxNum) {
		primes = new boolean[maxNum];

		for (int i=2; i<primes.length; i++) {
			primes[i] = true;
		}

		for (int factor=2; factor*factor<=maxNum; factor++) {
			if (primes[factor]) {
				for (int n=2*factor; n<primes.length; n += factor) {
					primes[n] = false;
				}
			}
		}
	}

	public boolean isPrime(int n) {
		return primes[n];
	}

	public int getNumberPrimes() {
		int nrPrimes = 0;
		for (int i=2; i<primes.length; i++) {
			if (isPrime(i)) {
				nrPrimes++;
			}
		}
		return nrPrimes;
	}
}
