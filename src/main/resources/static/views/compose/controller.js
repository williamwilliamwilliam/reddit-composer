app.controller('ComposeController', function($scope, $http, $mdDialog, $q, composerService) {
	$scope.outboxStatusMessage = composerService.outboxStatusMessage;
	
	$scope.headerHeight = 156;
	$scope.innerWidth = window.innerWidth;
	$scope.innerHeight = window.innerHeight;
	$scope.data = composerService.data;
	
	
	WebSocketClient.handleConnect = function(){
		WebSocketClient.subscribe($scope.username);
	};
	
	WebSocketClient.handleMessage = function(subscription, object) {
		composerService.handleWebSocketMessage($scope, object);
		$scope.$apply();
	}
	
	$scope.initializeUsername = function(){
		$http.get("/username")
	    .then(function(response) {
	    	$scope.username = response.data.username;
			WebSocketClient.connect();
	    });
	};
	$scope.initializeUsername();
	
	$scope.sendingMessages = false;
	
	$scope.errorDialog = function(event, message){
		if(message.indexOf('401 Unauthorized') > -1){
			message = message+". This means you've probably entered in bad credentials.";
		}
		$mdDialog.show(
			      $mdDialog.alert()
			        .parent(angular.element(document.querySelector('#popupContainer')))
			        .clickOutsideToClose(true)
			        .title('Uh oh!')
			        .textContent(message)
			        .ariaLabel('Error box')
			        .ok('Got it!')
			        .targetEvent(event)
			    );
	};
	
	$scope.successDialog = function(event, message){
		$mdDialog.show(
			      $mdDialog.alert()
			        .parent(angular.element(document.querySelector('#popupContainer')))
			        .clickOutsideToClose(true)
			        .title('Looks good!')
			        .textContent(message)
			        .ariaLabel('Error box')
			        .ok('Got it!')
			        .targetEvent(event)
			    );
	};
	
	

	$scope.testCredentials = function(event) {
		composerService.data.testingCredentials = true;

		if(!composerService.data.credentials){
			$scope.errorDialog(event, "Fill out your Reddit API credentials first - UserID, Password, ClientID, Client Secret.");
			return;
		}
		
		if(!composerService.data.credentials.userID){
			$scope.errorDialog(event, "Enter in your Reddit userID");
			return;
		}
		if(!composerService.data.credentials.password){
			$scope.errorDialog(event, "Enter in your Reddit password");
			return;
		}
		if(!composerService.data.credentials.clientID){
			$scope.errorDialog(event, "Enter in your Client ID");
			return;
		}
		if(!composerService.data.credentials.clientSecret){
			$scope.errorDialog(event, "Enter in your Client Secret");
			return;
		}
		
		$http({
			method : 'POST',
			url : '/testCredentials',
			data : composerService.data.credentials
		}).then(function successCallback(response) {
			$scope.successDialog(event, "Reddit authenticated your credentials. You're good to send messages!")
			composerService.data.testingCredentials = false;
		}, function errorCallback(response) {
			$scope.testCredentialsMessage = response.data.message
			$scope.errorDialog(event, response.data.message);
			composerService.data.testingCredentials = false;
		});
	}
	
	$scope.asyncPOST

	$scope.processOutbox = function(ev) {
		composerService.data.outboxStatusMessage = "Request submitted...";
		$scope.outboxProgress = 0;
		
		if($scope.sendingMessages){
			return;
		}
		$scope.sendingMessages = true;
		
		var sendOutboxToBackend = function(outbox){
			$http({
				method : 'POST',
				url : '/processOutbox',
				data : outbox,
				async : true
			}).then(function successCallback(response) {
				$scope.sendAllMessagesMessage = "Looks good!";
				$scope.sendingMessages = false;
			}, function errorCallback(response) {
				$scope.errorDialog(event, response.data.message);
				$scope.sendingMessages = false;
			});
			
		}
		composerService.buildOutboxMessages().then(sendOutboxToBackend);
	}
	
	composerService.data.batchMessageSpreadsheet;
	
	$scope.addRowsToSpreadsheet = function(rowsToAdd){
		composerService.data.batchMessageSpreadsheet.alter('insert_row', composerService.data.batchMessageSpreadsheet.getData().length, rowsToAdd)
	};
	
	$scope.setExistingSessionOutbox = function(){
		var data = this.data;
		return $q(function(resolve, reject) {
			$http.get("/existingSessionOutbox")
		    .then(function(response) {
		    	data.existingOutboxData = response.data.messages;
		    	resolve();
		    }, function(response){
		    	reject();
		    });
		  });
	}
	
	$scope.initializeHandsOnTable = function(){
		composerService.initializeHandsOnTable($scope);
	};
	
	$scope.setExistingSessionOutbox().then(function(resolve){
		$scope.initializeHandsOnTable();
	}, function(reject){
		$scope.initializeHandsOnTable();
	});
});