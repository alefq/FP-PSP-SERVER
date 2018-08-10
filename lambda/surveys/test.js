var getSurvey = require("./getSurveyImpl");

const params = {
  surveyId: 11
};

getSurvey(params)
  .then(resp => console.log(resp))
  .catch(error => console.log({ error }));
