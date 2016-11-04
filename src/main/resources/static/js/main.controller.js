;(function () {

    MainController.$inject = ['$timeout', 'moment', 'backendService', 'chartService'];
    function MainController($timeout, moment, backendService, chartService) {

        var ctl = this;

        ctl.slider = {
            value: 36.6,
            options: {
                floor: 35,
                ceil: 39,
                step: 0.1,
                precision: 1,
                showTicksValues: 1,
                scale: 0.25
            }
        };

        ctl.submitTemp = function() {
            ctl.lastSubmitted = null;
            ctl.error = null;

            var temperature = ctl.slider.value;
            var takenAt = moment();

            backendService.submitLog(temperature, takenAt.toISOString()).then(
                function success(response) {
                    console.log('Temperature submitted sucessfully: ' + temperature + ' at ' + takenAt.toISOString());

                    ctl.lastSubmitted = 'Submitted: ' + temperature + ' at ' + takenAt.format('DD MMM, HH:mm');

                    ctl.updateChart();
                },
                function error(response) {
                    console.log('Error submitting temperature:');
                    console.log(angular.toJson(response, true));

                    ctl.error = response.data.error + ': ' + response.data.message;
                }
            );
        };

        // Initially set Base Date to current date
        ctl.baseDate = moment().hour(0).minute(0).second(0).millisecond(0);

        ctl.previousWeek = function() {
            ctl.baseDate = moment(ctl.baseDate).subtract(7, 'days');
            ctl.updateChart();
        };

        ctl.nextWeek = function() {
            ctl.baseDate = moment(ctl.baseDate).add(7, 'days');
            ctl.updateChart();
        };

        ctl.updateChart = function() {
            var fromDate = moment(ctl.baseDate).subtract(6, 'days'); //.toDate();//new Date(2016, 9, 25);
            var toDate = moment(ctl.baseDate).add(1, 'days'); //.toDate();//new Date(2016, 10, 1);

            backendService.getLogs(fromDate.toISOString(), toDate.toISOString()).then(
                function success(data) {
                    console.log('Logs got successfully:');
                    console.log(angular.toJson(data, true));

                    chartService.drawChart(fromDate.toDate(), toDate.toDate(), data);
                }, function error(response) {
                    console.log('Error getting logs:');
                    console.log(angular.toJson(response, true));
                }
            );
        };

        $timeout(function () {
            ctl.updateChart();
        }, 1000);
    }

    angular.module('main').controller('MainController', MainController);
})();
