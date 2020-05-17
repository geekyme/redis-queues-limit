package automation;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@KarateOptions(tags = {"~@ignore"})
class KarateRunner {
  @Test
  void testParallel() {
    Results results = Runner.parallel(getClass(), 5, "build/surefire-reports");
    assertEquals(0, results.getFailCount(), results.getErrorMessages());
  }
}
