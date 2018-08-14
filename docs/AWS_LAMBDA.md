# AWS LAMBDA endpoints

The server exposes info about snapshots through AWS Lambda/API Gateway.

Base API URL: https://d3w0k61hzb.execute-api.eu-west-2.amazonaws.com/beta

## /snapshots

> The endpoint /snapshots/{surveyId} is still available, but we encourage the use of this endpoint instead

Description: Gets snapshots indicators from a provided survey and/or application id (hub)

```
curl --header "x-api-key: [YOUR_API_KEY]" https://d3w0k61hzb.execute-api.eu-west-2.amazonaws.com/beta/snapshots?surveyId=1&applicationId=2
```

**Query URL Parameters**

_surveyId_: the id of the survey. If no value provided, will not filter indicators by survey

_applicationId_: the id of the application (hub). If no value provided, will not filter indicators by application

_indicatorName1=indicatorValue1..._;

Can be any of the indicators properties names for that survey. This can vary for each survey.

The indicators properties values can be any of the following:

```
0 (RED), 1 (YELLOW), 2 (GREEN), "NONE"
```

Example:

```
curl --header "x-api-key: [YOUR_API_KEY]" https://d3w0k61hzb.execute-api.eu-west-2.amazonaws.com/beta/snapshots?surveyId=11&applicationId=2&security=2&registeredToVoteAndVotesInElections=2
```

## /surveys/{surveyId}

Description: Gets the survey definition from a provided survey id

```
curl --header "x-api-key: [YOUR_API_KEY]" https://d3w0k61hzb.execute-api.eu-west-2.amazonaws.com/beta/surveys/1
```

**Path parameters**

_surveyId_: the id of the survey
