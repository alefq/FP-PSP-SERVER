var qs = require("qs");
var axios = require("axios");
var request = require("request");
const config = require("./config");

const COLORS_TO_CODE = { RED: 0, YELLOW: 1, GREEN: 2 };

function getSnapshotsFromApi({
  accessToken,
  surveyId,
  applicationId,
  indicatorsFilter
}) {
  const instance = axios.create({
    baseURL: config.BASE_URL,
    headers: { Authorization: `Bearer ${accessToken}` }
  });
  const params = {
    survey_id: surveyId,
    application_id: applicationId
  };
  const filterFunc = createFilterFunc(indicatorsFilter);
  return instance
    .get(`/api/v1/snapshots`, { params })
    .then(resp =>
      resp.data
        .map(flatAllIndicators)
        .map(mapToColorCodes)
        .filter(filterFunc)
    )
    .catch(error => Promise.reject(error));
}

function flatAllIndicators(snapshot) {
  const economic = snapshot.economic_survey_data;
  const location = economic.familyUbication;
  if (location) {
    const lat = location.split(",")[0].trim();
    const lonG = location.split(",")[1].trim();
    return { ...snapshot.indicator_survey_data, lat, lonG };
  }
  return snapshot.indicator_survey_data;
}

function mapToColorCodes(surveyData) {
  const newSurveyData = {};
  for (key in surveyData) {
    const colorCode = COLORS_TO_CODE[surveyData[key]];
    newSurveyData[key] =
      typeof colorCode !== "undefined" ? colorCode : surveyData[key];
  }
  return newSurveyData;
}

function isNumeric(num) {
  return !isNaN(num);
}

function areValuesEqual(value1, value2) {
  if (isNumeric(value1) && isNumeric(value2)) {
    return parseFloat(value1) === parseFloat(value2);
  }
  return value1 === value2;
}

function createFilterFunc(indicatorsFilter) {
  if (!indicatorsFilter || Object.keys(indicatorsFilter).length === 0) {
    return () => true;
  }
  const { match, ...indicators } = indicatorsFilter;
  const matchAll = !match || match === "all";
  return snapshot => {
    const totalIndicatorsEqual = Object.keys(snapshot)
      .filter(key => Object.keys(indicators).includes(key))
      .reduce((totalEqual, key) => {
        if (areValuesEqual(indicators[key], snapshot[key])) {
          return totalEqual + 1;
        }
        return 0;
      }, 0);
    if (matchAll) {
      return totalIndicatorsEqual === Object.keys(indicators).length;
    }
    return totalIndicatorsEqual > 0;
  };
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

function getToken(token) {
  const instance = axios.create({
    baseURL: config.BASE_URL,
    headers: { Authorization: `Bearer ${token}` }
  });

  return instance.get(`/oauth/decode`).catch(error => Promise.reject(error));
}

function getSnapshotsByHub(params) {
  return login().then(resp =>
    getSnapshotsFromApi({ accessToken: resp.data.access_token, ...params })
  );
}

module.exports = getSnapshotsByHub;
