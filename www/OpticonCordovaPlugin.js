cordova.define("cordova-plugin-opticon.OpticonCordovaPlugin", function (require, exports, module) {
    var exec = require('cordova/exec');
    var channel = require('cordova/channel');

    var OpticonCordovaPlugin = function (require, exports, module) {

        function EchoService() {

            this.echo = function (str, callback) {
                cordova.exec(callback, function (err) {
                }, "OpticonCordovaPlugin", "echo", [str]);
            }

            this.initOpticon = function (callback) {
                console.log('init opticon')
                cordova.exec(callback, function (err) {
                }, "OpticonCordovaPlugin", "initOpticon", []);
            }
        }

        module.exports = new EchoService();
    }

    OpticonCordovaPlugin(require, exports, module);

    cordova.define("cordova/plugin/EchoService", OpticonCordovaPlugin);
});
