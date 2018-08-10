const getSurvey = require("./getSurveyImpl");

exports.handler = (event, context, callback) => {
  // TODO implement
  getSurvey().then(result => {
    callback(null, result);
  });
};
