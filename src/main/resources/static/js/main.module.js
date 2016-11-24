;(function () {
    angular.module('main', ['rzModule', 'angularMoment', 'ngRoute', 'ui.bootstrap', 'security'])
        .config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
            $routeProvider.when('/', {
                templateUrl: 'main.html',
                controller: 'MainController',
                controllerAs: 'ctl'
            }).otherwise('/');


            $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
            $httpProvider.interceptors.push('securityInterceptor');
        }]);
})();