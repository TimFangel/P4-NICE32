// Does so we don't have to run it through main.
#define DOCTEST_CONFIG_IMPLEMENT_WITH_MAIN
#include "doctest.h"

int fact(int n) { return n <= 1 ? n : fact(n - 1) * n; }

TEST_CASE("Testing the factorial function") {
  CHECK(fact(0) == 1); // Should fail.
  CHECK(fact(1) == 1); // Here and down should pass.
  CHECK(fact(2) == 2);
  CHECK(fact(3) == 6);
  CHECK(fact(10) == 3628800);
}