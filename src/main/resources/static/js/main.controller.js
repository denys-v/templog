;(function () {

    MainController.$inject = ['$timeout', '$q', 'moment', 'backendService', 'chartService', 'securityContext', 'loginDialog'];
    function MainController($timeout, $q, moment, backendService, chartService, securityContext, loginDialog) {

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

        ctl.submitTempClick = function () {
            ctl.lastSubmitted = null;
            ctl.error = null;

            var temperature = ctl.slider.value;
            var takenAt = moment();

            backendService.submitLog(temperature, takenAt.toISOString()).catch(
                function error(response) {
                    if (response.status == 401 || response.status == 403) {
                        return loginDialog.login().then(function loginSuccessful() {
                            return backendService.submitLog(temperature, takenAt.toISOString());
                        }, function loginCancelled() {
                            return $q.reject(response); // return 'upper' response - to be logged
                        });
                    } else {
                        return $q.reject(response);
                    }
                }
            ).then(function success() {
                ctl.lastSubmitted = 'Внесена температура: ' + temperature +
                    ' at ' + takenAt.format('DD MMM, HH:mm');
                ctl.updateChart();
            }, function error(response) {
                setSubmitError(response);
            });
        };

        function setSubmitError(response) {
            if (response.status == 401) {
                ctl.error = 'Для внесения показаний требуется вход в систему';
            } else if (response.status == 403) {
                ctl.error = 'Недостаточно полномочий для внесения показаний';
            } else {
                if (response.data != undefined) {
                    ctl.error = 'Ошибка: ' +
                        (response.data.error ? response.data.error : '') +
                        (response.data.message ? ': ' + response.data.message : '');
                }
            }
        }

        // Initially set Base Date to current date
        var baseDate = moment().hour(0).minute(0).second(0).millisecond(0);

        ctl.previousWeek = function() {
            baseDate = moment(baseDate).subtract(7, 'days');
            ctl.updateChart();
        };

        ctl.nextWeek = function() {
            baseDate = moment(baseDate).add(7, 'days');
            ctl.updateChart();
        };

        ctl.updateChart = function() {
            var fromDate = moment(baseDate).subtract(6, 'days'); //.toDate();//new Date(2016, 9, 25);
            var toDate = moment(baseDate).add(1, 'days'); //.toDate();//new Date(2016, 10, 1);

            backendService.getLogs(fromDate.toISOString(), toDate.toISOString()).then(
                function success(data) {
                    // console.log('Logs got successfully:');
                    // console.log(angular.toJson(data, true));

                    chartService.drawChart(fromDate.toDate(), toDate.toDate(), data);
                }, function error(response) {
                    console.log('Error getting logs:');
                    console.log(angular.toJson(response, true));
                }
            );
        };

        $('#chartCarousel').carousel({
            interval: false
        });

        $timeout(function () {
            ctl.updateChart();
        }, 1000);
    }

    angular.module('main').controller('MainController', MainController);
})();
