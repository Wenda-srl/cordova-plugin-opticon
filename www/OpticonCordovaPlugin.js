var exec = require('cordova/exec');
// var channel = require('cordova/channel');

module.exports.echo = function (str, callback) {
    exec(callback, function (err) {
    }, 'OpticonCordovaPlugin', 'echo', [str]);
};

module.exports.initOpticon = function (callback) {
    exec(callback, function (err) {
    }, 'OpticonCordovaPlugin', 'initOpticon', []);
};
