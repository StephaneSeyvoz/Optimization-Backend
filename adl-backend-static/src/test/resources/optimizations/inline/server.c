
/* -----------------------------------------------------------------------------
   Implementation of the s interface with signature increment.Service.
   -------------------------------------------------------------------------- */

/* int increment(int i) */
int METH(s, increment)(int i) {
  return i + 1;
}

/* int increment(int i) */
int METH(s, decrement)(int i) {
  return i - 1;
}

/* int increment(int i) */
int METH(s2, increment)(int i) {
  return i + 1;
}

/* int increment(int i) */
int METH(s2, decrement)(int i) {
  return i - 1;
}
