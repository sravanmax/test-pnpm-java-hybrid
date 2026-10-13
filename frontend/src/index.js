const axios = require('axios');
const _ = require('lodash');
const minimist = require('minimist');

// Frontend vulnerable methods - JavaScript
function makeRequestWithAxios(userUrl) {
  return axios.get(userUrl, {
    proxy: false
  });
}

function mergeConfigFrontend(baseConfig, userConfig) {
  return _.defaultsDeep(baseConfig, userConfig);
}

function parseArgsFrontend(args) {
  return minimist(args);
}

module.exports = {
  makeRequestWithAxios,
  mergeConfigFrontend,
  parseArgsFrontend
};
