;(function () {

    chartService.$inject = ['moment'];
    function chartService(moment) {

        this.drawChart = function(fromDate, toDate, logs) {

            var rows = [];

            var hiddenStart = [moment(fromDate).subtract(1, 'days').toDate(), null, 36.6, 37];
            rows.push(hiddenStart);

            _.forEach(logs, function(log) {
                var row = _.concat(moment(log[0]).toDate(), log[1], null, null);
                rows.push(row);
            });

            var hiddenEnd = [moment(toDate).add(1, 'days').toDate(), null, 36.6, 37];
            rows.push(hiddenEnd);

            // console.log(angular.toJson(rows, true));

            var data = new google.visualization.DataTable();
            data.addColumn('datetime', 'Time');
            data.addColumn('number', 'Факт. изм.');
            data.addColumn('number', '36.6 C');
            data.addColumn('number', '37 C');

            data.addRows(rows);

            var options = {
                title: 'Внесенные измерения температуры',
                // width: 900,
                height: 800,
                legend: {
                    // position: 'none'
                },
                colors: ['blue', 'green', 'red'],
                hAxis: {
                    format: 'd/M/yy',
                    viewWindow: {
                        min: fromDate,
                        max: toDate
                    },
                    gridlines: {
                        count: -1,
                        units: {
                            days: {format: ['d MMM']}
                        }
                    }
                },
                vAxis: {
                    viewWindow: {
                        min: 35,
                        max: 39
                    },
                    gridlines: {
                        count: 5
                    }
                },
                trendlines: {
                    1: {color: 'green', tooltip: false},
                    2: {color: 'red', tooltip: false}
                }
            };

            var chart = new google.visualization.ScatterChart(document.getElementById('chartDiv'));
            chart.draw(data, options);
        };
    }

    angular.module('main').service('chartService', chartService);
})();
