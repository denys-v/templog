;(function () {

    chartService.$inject = ['moment'];
    function chartService(moment) {

        var self = this;

        /**
         * To display temperature log data in the chart.
         * @param fromDate
         * @param toDate
         * @param logs
         */
        self.drawChart = function(fromDate, toDate, logs) {
            var rows = [];

            var hiddenStart = [moment(fromDate).subtract(1, 'days').toDate(), null, null, 36.6, 37];
            rows.push(hiddenStart);

            _.forEach(logs, function(log) {
                var takenAt = moment(log[0]);
                var temp = log[1];
                var tooltip = '<div style="font-size: large; margin: 10px;"><b>' + temp + '&deg;C</b><br/>' +
                    takenAt.format('D') + '&nbsp;' + takenAt.format('MMM,') + '&nbsp;' + takenAt.format('H:mm') +
                    '</div>';
                // console.log('Tooltip: ' + tooltip);

                var row = _.concat(takenAt.toDate(), temp, tooltip, null, null);
                rows.push(row);
            });

            var hiddenEnd = [moment(toDate).add(1, 'days').toDate(), null, null, 36.6, 37];
            rows.push(hiddenEnd);

            // console.log(angular.toJson(rows, true));

            var data = new google.visualization.DataTable();
            data.addColumn('datetime', 'Time');
            data.addColumn('number', 'Факт. изм.');
            data.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': true}});
            data.addColumn('number', '36.6 C');
            data.addColumn('number', '37 C');

            data.addRows(rows);

            chartOptions.hAxis.viewWindow.min = fromDate;
            chartOptions.hAxis.viewWindow.max = toDate;

            getChart().draw(data, chartOptions);
        };

        /**
         * Internal reference to chart instance - shouldn't be used directly, but via the getChart() accessor.
         * @type {null}
         */
        var chart = null;

        /**
         * Creates the chart object on first call and uses it afterwards.
         * @returns {*}
         */
        var getChart = function() {
            if (chart == null) {
                chart = new google.visualization.ScatterChart(document.getElementById('chartDiv'));
                // google.visualization.events.addListener(chart, 'select', function (e) {
                //     console.log('Select:');
                //     console.log(self.chart.getSelection());
                // });
                // google.visualization.events.addListener(chart, 'click', function (e) {
                //     console.log('Click:');
                //     console.log(e);
                // });
            }

            return chart;
        };

        /**
         * Chart options template.
         * @type {{title: string, height: number, legend: {}, colors: string[], pointSize: number, tooltip: {isHtml: boolean}, hAxis: {format: string, viewWindow: {min: null, max: null}, gridlines: {count: number, units: {days: {format: string[]}}}}, vAxis: {viewWindow: {min: number, max: number}, gridlines: {count: number}}, trendlines: {1: {color: string, tooltip: boolean, pointsVisible: boolean, pointSize: number}, 2: {color: string, tooltip: boolean, pointsVisible: boolean, pointSize: number}}}}
         */
        var chartOptions = {
            title: 'Внесенные измерения температуры',
            // width: 900,
            height: 800,
            legend: {
                // position: 'none'
            },
            colors: ['blue', 'green', 'red'],
            pointSize: 14,
            tooltip: {isHtml: true},
            hAxis: {
                format: 'd/M/yy',
                viewWindow: {
                    min: null, // to be set before drawing
                    max: null  // to be set before drawing
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
                1: {color: 'green', tooltip: false, pointsVisible: false, pointSize: 0},
                2: {color: 'red', tooltip: false, pointsVisible: false, pointSize: 0}
            }
        };
    }

    angular.module('main').service('chartService', chartService);
})();
