package frontend.abstract_syntax;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BaseProgramTest {
    static long seed;
    static java.util.Random rng;

    @BeforeAll
    static void seedIncluded() {
        // Read variable form System Properties
        String seedString = System.getProperty("test.seed");

        if (seedString != null && !seedString.isEmpty()) {
            seed = Long.parseLong(seedString);
        } else {
            throw new RuntimeException("Could not find seed. Got '" + seedString + "'");
        }
        rng = new java.util.Random(seed);
    }

    @Test
    void testWithSeed() {
        int i = rng.nextInt(100);
        System.out.println("random number: " + i);

        Assertions.assertTrue(i > 49);
    }
}
