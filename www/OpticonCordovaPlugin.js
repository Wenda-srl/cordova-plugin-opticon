var exec = require('cordova/exec');
// var channel = require('cordova/channel');

module.exports.echo = function (str, callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'echo', str);
};

module.exports.initOpticon = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'initOpticon', []);
};
