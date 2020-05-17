Feature: Job is queued and processed

  Scenario Outline: queue job <spec> and poll job
    Given url "http://localhost:8081/queueJob"
    And request
      """
      {
      "queueId": "airasia",
      "name": <spec>,
      "score": <score>
      }
      """
    When method post
    Then status 200

    Given url "http://localhost:8080/pollJob/airasia"
    And request {}
    When method post
    Then status 200

    Examples:
      | spec   | score |
      | spec1  | 1.0   |
      | spec2  | 1.1   |
      | spec3  | 1.2   |
      | spec4  | 1.3   |
      | spec5  | 1.4   |
      | spec6  | 1.5   |
      | spec7  | 1.6   |
      | spec8  | 1.7   |
      | spec9  | 1.8   |
      | spec10 | 1.9   |
