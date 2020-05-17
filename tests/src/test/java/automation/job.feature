Feature: Job is queued and processed

  Scenario Outline: queue job <spec> and poll job
    Given url "http://localhost:8081/queueJob"
    And request
      """
      {
      "queueId": <queueId>,
      "name": <spec>,
      "score": <score>
      }
      """
    When method post
    Then status 200

    Given url "http://localhost:8080/pollJob/<queueId>"
    And request {}
    When method post
    Then status 200

    Examples:
      | spec   | score | queueId |
      | spec1  | 1.0   | airasia |
      | spec2  | 1.1   | airasia |
      | spec3  | 1.2   | airasia |
      | spec4  | 1.3   | airasia |
      | spec5  | 1.4   | airasia |
      | spec6  | 1.5   | airasia |
      | spec7  | 1.6   | airasia |
      | spec8  | 1.7   | airasia |
      | spec9  | 1.8   | airasia |
      | spec10 | 1.9   | airasia |
