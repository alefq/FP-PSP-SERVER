var qs = require("qs");
var axios = require("axios");
var request = require("request");
const config = require("./config");

function getSurveyDefinition(accessToken, surveyId, indictarosFilter) {
  const instance = axios.create({
    baseURL: config.BASE_URL,
    headers: { Authorization: `Bearer ${accessToken}` }
  });

  return instance
    .get(`/api/v1/surveys/${surveyId}`)
    .then(resp => resp.data)
    .catch(error => Promise.reject(error));
}

function getCreds() {
  return {
    username: config.username,
    password: config.password,
    grant_type: config.grant_type,
    clientId: config.clientId,
    clientSecret: config.clientSecret
  };
}
// TODO Agregar logica para leer
// el token de env y ver si aún es válido
// si no es válido realizar el login
// con las credenciales
function login(creds = getCreds()) {
  const { clientId, clientSecret, ...restCreds } = creds;
  const options = {
    method: "POST",
    auth: { username: clientId, password: clientSecret },
    data: qs.stringify(restCreds),
    url: config.AUTH_URL
  };

  return axios(options);
}

function getSurvey({ surveyId }) {
  return login().then(resp =>
    getSurveyDefinition(resp.data.access_token, surveyId)
  );
}

module.exports = getSurvey;
