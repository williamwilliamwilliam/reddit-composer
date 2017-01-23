app = angular.module('reddit-composer', [ 'ngRoute', 'ngMaterial', 'ngCookies' ]);

app.config(function($routeProvider, $httpProvider, $mdThemingProvider) {
	$routeProvider.when('/compose', {
		templateUrl : '/views/compose/compose.html',
		controller : 'ComposeController'
	}).otherwise({
		redirectTo : '/compose'
	});

	$mdThemingProvider.theme('default');
});
