;(function () {

    backendService.$inject = ['$q', '$http'];
    function backendService($q, $http) {
        this.getTestValue = function() {
            return 'Test value.';
        };

        this.submitLog = function (temperature, takenAt) {
            return $http.post('/templog/submit', {temperature: temperature, takenAt: takenAt});
        };

        this.getLogs = function(fromDate, toDate) {
            return $http.get('/templog/logs3', {params: {fromDate: fromDate, toDate: toDate}}).then(
                function success(response) {
                    var logs = [];
                    _.forEach(response.data, function (logDto) {
                        logs.push([logDto.takenAt, logDto.temperature]);
                    });

                    return logs;
                }, function error(response) {
                    return $q.reject(response);
                }
            );
            // var logs = [
            //     ['2016-10-30T01:18:00', 36.6],
            //     ['2016-10-30T01:30:00', 36.8],
            //     ['2016-10-30T01:45:00', 37.1]
            // ];
            //
            // return $q.resolve(logs);
        };
    }

    angular.module('main').service('backendService', backendService);

})();
