var getSnapshotsByHub = require("./getSnapshotsByHubImpl");

const queryString = "security=2&registeredToVoteAndVotesInElections=2";

const indicatorsFilter = {
  // security: "2",
  // registeredToVoteAndVotesInElections: "2",
  // regularityOfMeals: "NONE",
  // lat: "54.979645",
  electricityAccess: "0",
  // garbageDisposal: "1",
  // lat: "-25.295787959832193",
  // stableIncome: 0,
  // entrepreneurialSpirit: 2,
  // knowledgeAndSkillsToGenerateIncome: 2,
  match: "any"
};

const params = {
  surveyId: 6,
  applicationId: 8,
  indicatorsFilter
};

getSnapshotsByHub(params)
  .then(resp => console.log(resp))
  .catch(error => console.log({ error }));
