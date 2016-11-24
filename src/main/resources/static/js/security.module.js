;(function () {

    securityService.$inject = ['$q', '$http'];
    function securityService($q, $http) {
        // this.login1 = function(username, password) {
        //     var headers = {authorization : "Basic "
        //     + btoa(username + ":" + password)
        //     };
        //
        //     $http.get('/user_login', {headers : headers}).then(function(response) {
        //         console.log(angular.toJson(response, true));
        //         $http.defaults.headers.common['X-Auth-Token'] = response.data.token;
        //     }, function(response) {
        //         console.log(angular.toJson(response, true));
        //     });
        // };

        this.login = function(username, password, rememberMe) {
/*
            angular.element(document).injector().get('securityService').login('writer', 'writer');
*/
            var payload = 'username=' + encodeURIComponent(username) +
                '&password=' + encodeURIComponent(password) +
                (rememberMe ? '&remember-me=true' : '');

            var config = {
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            };

            return $http.post('/login', payload, config)
                .then(function(response) {
                    // console.log(angular.toJson(response, true));
                    return response;
                }, function(response) {
                    console.log(angular.toJson(response, true));

                    return $q.reject(response);
                });
        };

        this.clearAuthHeader = function() {
            // $http.defaults.headers.common['X-Auth-Token'] = undefined;
        };

        this.logout = function() {
            $http.post('/logout', {}).then(function success(response) {
                // perform additional request to renew CSRF token
                return $http.head('/after_logout').then(function success(response) {
                }, function error(response) {
                    console.log(angular.toJson(response, true));
                });
            }, function error(response) {
                console.log(angular.toJson(response, true));
            });
        };
    }

    var AUTH_TOKEN_KEY = 'X-Auth-Token';
    var AUTH_TOKEN_HEADER = AUTH_TOKEN_KEY;

    securityContext.$inject = ['localStorageService'];
    function securityContext(localStorageService) {

        this.isAuthenticated = function() {
            return this.getAuthToken() != undefined;
        };

        this.getAuthToken = function() {
            return localStorageService.get(AUTH_TOKEN_KEY);
        };

        this.setAuthToken = function(authToken) {
            localStorageService.set(AUTH_TOKEN_KEY, authToken);
        };

        this.clearAuthToken = function() {
            localStorageService.remove(AUTH_TOKEN_KEY);
        };
    }

    securityInterceptor.$inject = ['$q', 'securityContext'];
    function securityInterceptor($q, securityContext) {
        return {
            'request': function(config) {
                if (securityContext.isAuthenticated()) {
                    config.headers[AUTH_TOKEN_HEADER] = securityContext.getAuthToken();
                }

                return config;
            },
            'response': function (response) {
                // console.log('SecurityInterceptor - response:');
                // console.log(angular.toJson(response, true));

                var xAuthToken = response.headers(AUTH_TOKEN_HEADER);
                if (xAuthToken != undefined) {
                    if (xAuthToken.length > 0) {
                        // we got auth token from the backend (successful login or remember me)
                        securityContext.setAuthToken(xAuthToken);
                    } else {
                        // we got auth token header with empty value (successful logout or invalid session)
                        securityContext.clearAuthToken();
                    }
                }

                return response;
            },
            'responseError': function (response) {
                console.log('SecurityInterceptor - responseError:');
                console.log(angular.toJson(response, true));

                return $q.reject(response);
            }
        };
    }

    angular.module('security', ['LocalStorageModule'])
        .service('securityService', securityService)
        .service('securityContext', securityContext)
        .factory('securityInterceptor', securityInterceptor);

})();
