Feature: To verify group endpoint

@BookwormTest
Scenario Outline: Verify that BookWorm's POST Group API throws correct exception
Given I hit POST group API with <ParamName> and <ParamValue>
Then I get <StatusCode> response

Examples:
  | ParamName                                    | ParamValue | StatusCode                                 |
  | "email" | "aaa"             | 400 |
  | "startOfTheDay"          | "20250202T02:10:22"             | 400 |
  | "" | "" | 400 |

@BookwormTest
Scenario Outline: Verify that BookWorm's POST Group API successfully creates group when not exist
Given I hit POST group API with <ParamName> and <ParamValue>
Then I get <StatusCode> response
When I hit GET groups API
Then I get 200 response
Then My response contains <ParamName>:<ParamValue>

Examples:
  | ParamName | ParamValue | StatusCode|
  | "name" | "testGroupX"             | 201 |
  |"name" | "testGroupX" | 409|


@BookwormTest
Scenario Outline: Verify that BookWorm's DELETE Group API deletes group when exists
Given I hit DELETE group API with <ParamName> and <ParamValue>
Then I get <StatusCode> response

Examples:
  | ParamName | ParamValue | StatusCode |
  | "name" | "testGroupX" | 204 |
  | "name" | "testGroupX" | 404 |