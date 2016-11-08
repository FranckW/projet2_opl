package calculator;

public class LevenshteinDistance {

	/**
	 * <p>
	 * This returns the default instance that uses a version of the algorithm
	 * that does not use a threshold parameter.
	 * </p>
	 *
	 * @see LevenshteinDistance#getDefaultInstance()
	 */
	public LevenshteinDistance() {
	}

	/**
	 * <p>
	 * Find the Levenshtein distance between two Strings.
	 * </p>
	 *
	 * <p>
	 * A higher score indicates a greater distance.
	 * </p>
	 *
	 * <p>
	 * The previous implementation of the Levenshtein distance algorithm was
	 * from
	 * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld
	 * .htm</a>
	 * </p>
	 *
	 * <p>
	 * Chas Emerick has written an implementation in Java, which avoids an
	 * OutOfMemoryError which can occur when my Java implementation is used with
	 * very large strings.<br>
	 * This implementation of the Levenshtein distance algorithm is from
	 * <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.
	 * com/ldjava.htm</a>
	 * </p>
	 *
	 * <pre>
	 * distance.apply(null, *)             = IllegalArgumentException
	 * distance.apply(*, null)             = IllegalArgumentException
	 * distance.apply("","")               = 0
	 * distance.apply("","a")              = 1
	 * distance.apply("aaapppp", "")       = 7
	 * distance.apply("frog", "fog")       = 1
	 * distance.apply("fly", "ant")        = 3
	 * distance.apply("elephant", "hippo") = 7
	 * distance.apply("hippo", "elephant") = 7
	 * distance.apply("hippo", "zzzzzzzz") = 8
	 * distance.apply("hello", "hallo")    = 1
	 * </pre>
	 *
	 * @param left
	 *            the first string, must not be null
	 * @param right
	 *            the second string, must not be null
	 * @return result distance, or -1
	 * @throws IllegalArgumentException
	 *             if either String input {@code null}
	 */
	public Integer apply(CharSequence left, CharSequence right) {
		return unlimitedCompare(left, right);
	}

	/**
	 * <p>
	 * Find the Levenshtein distance between two Strings.
	 * </p>
	 *
	 * <p>
	 * A higher score indicates a greater distance.
	 * </p>
	 *
	 * <p>
	 * The previous implementation of the Levenshtein distance algorithm was
	 * from
	 * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld
	 * .htm</a>
	 * </p>
	 *
	 * <p>
	 * Chas Emerick has written an implementation in Java, which avoids an
	 * OutOfMemoryError which can occur when my Java implementation is used with
	 * very large strings.<br>
	 * This implementation of the Levenshtein distance algorithm is from
	 * <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.
	 * com/ldjava.htm</a>
	 * </p>
	 *
	 * <pre>
	 * unlimitedCompare(null, *)             = IllegalArgumentException
	 * unlimitedCompare(*, null)             = IllegalArgumentException
	 * unlimitedCompare("","")               = 0
	 * unlimitedCompare("","a")              = 1
	 * unlimitedCompare("aaapppp", "")       = 7
	 * unlimitedCompare("frog", "fog")       = 1
	 * unlimitedCompare("fly", "ant")        = 3
	 * unlimitedCompare("elephant", "hippo") = 7
	 * unlimitedCompare("hippo", "elephant") = 7
	 * unlimitedCompare("hippo", "zzzzzzzz") = 8
	 * unlimitedCompare("hello", "hallo")    = 1
	 * </pre>
	 *
	 * @param left
	 *            the first String, must not be null
	 * @param right
	 *            the second String, must not be null
	 * @return result distance, or -1
	 * @throws IllegalArgumentException
	 *             if either String input {@code null}
	 */
	private static int unlimitedCompare(CharSequence left, CharSequence right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
		 * The difference between this impl. and the previous is that, rather
		 * than creating and retaining a matrix of size s.length() + 1 by
		 * t.length() + 1, we maintain two single-dimensional arrays of length
		 * s.length() + 1. The first, d, is the 'current working' distance array
		 * that maintains the newest distance cost counts as we iterate through
		 * the characters of String s. Each time we increment the index of
		 * String t we are comparing, d is copied to p, the second int[]. Doing
		 * so allows us to retain the previous cost counts as required by the
		 * algorithm (taking the minimum of the cost count to the left, up one,
		 * and diagonally up and to the left of the current cost count being
		 * calculated). (Note that the arrays aren't really copied anymore, just
		 * switched...this is clearly much better than cloning an array or doing
		 * a System.arraycopy() each time through the outer loop.)
		 * 
		 * Effectively, the difference between the two implementations is this
		 * one does not cause an out of memory condition when calculating the LD
		 * over two very large strings.
		 */

		int n = left.length(); // length of left
		int m = right.length(); // length of right

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		if (n > m) {
			// swap the input strings to consume less memory
			final CharSequence tmp = left;
			left = right;
			right = tmp;
			n = m;
			m = right.length();
		}

		int[] p = new int[n + 1]; // 'previous' cost array, horizontally
		int[] d = new int[n + 1]; // cost array, horizontally
		int[] tempD; // placeholder to assist in swapping p and d

		// indexes into strings left and right
		int i; // iterates through left
		int j; // iterates through right

		char rightJ; // jth character of right

		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			rightJ = right.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = left.charAt(i - 1) == rightJ ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
			}

			// copy current distance counts to 'previous row' distance counts
			tempD = p;
			p = d;
			d = tempD;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}

}
