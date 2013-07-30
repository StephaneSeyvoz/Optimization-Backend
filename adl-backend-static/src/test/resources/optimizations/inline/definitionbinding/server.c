// -----------------------------------------------------------------------------
// Constructor implementation
// -----------------------------------------------------------------------------

CONSTRUCTOR() {
  // initialize the "count" private data
  PRIVATE.count = 0;
}

// int increment(int i)
int METH(s, increment)(int i) {

	// increment the counter that also serves as increment.
	PRIVATE.count ++;

	// increment with the count
	return i + PRIVATE.count;
}

// int increment(int i)
int METH(s, decrement)(int i) {

	// decrement with the count (should match with the increment as its called
	// right after)
	return i - PRIVATE.count;
}
