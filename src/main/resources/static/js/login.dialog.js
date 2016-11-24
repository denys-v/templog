;(function () {

    loginDialog.$inject = ['$q', '$uibModal'];
    function loginDialog($q, $uibModal) {

        this.login = function () {
            var deferred = $q.defer();

            var modal = $uibModal.open({
                templateUrl: 'login.html',
                size: 'sm',
                controller: 'LoginDialogController',
                controllerAs: 'ctl'
            });

            modal.result.then(function closed() {
                deferred.resolve();
            }, function dismissed() {
                deferred.reject();
            });

            return deferred.promise;
        }
    }

    LoginDialogController.$inject = ['$uibModalInstance', 'securityService'];
    function LoginDialogController($uibModalInstance, securityService) {
        var ctl = this;

        ctl.credentials = {
            username: null,
            password: null,
            rememberme: false
        };

        ctl.submit = function () {
            ctl.error = undefined;
            console.log(angular.toJson(ctl.credentials, true));

            securityService.login(ctl.credentials.username, ctl.credentials.password, ctl.credentials.rememberme)
                .then(
                    function success(response) {
                        // successful login - close modal dialog
                        $uibModalInstance.close();
                    }, function failure(response) {
                        // login failure - display error message in modal dialog
                        if (response.status == 401) {
                            ctl.error = 'Неверное имя пользователя или пароль';
                        } else if (response.data != undefined) {
                            ctl.error = 'Ошибка: ' +
                                (response.data.error ? response.data.error : '') +
                                (response.data.message ? ': ' + response.data.message : '');
                        } else {
                            ctl.error = 'Ошибка (статус ' + response.status + ')';
                        }
                    }
                );
        }
    }

    angular.module('main')
        .service('loginDialog', loginDialog)
        .controller('LoginDialogController', LoginDialogController);
})();