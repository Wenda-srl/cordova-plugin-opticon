var exec = require('cordova/exec');
// var channel = require('cordova/channel');

module.exports.echo = function (str, callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'echo', [str]);
};

module.exports.initOpticon = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'initOpticon', []);
};

module.exports.takeSnapshot = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'takeSnapshot', []);
};

module.exports.startDecode = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'startDecode', []);
};

module.exports.stopDecode = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'stopDecode', []);
};

module.exports.startTrigger = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'startTrigger', []);
};

module.exports.stopTrigger = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'stopTrigger', []);
};

module.exports.isConnected = function (callback, error) {
    exec(callback, error, 'OpticonCordovaPlugin', 'isConnected', []);
};

