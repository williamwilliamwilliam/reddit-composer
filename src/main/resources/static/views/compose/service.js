app.service('composerService', function($q) {
	this.data = {
			credentials:{
				
			},
			spreadsheetData:[],
			batchMessageSpreadsheet:{},
			existingOutboxData:null,
			outboxStatusMessage:"This is a test status message. This is a test status message. This is a test status message",
			outboxProgress:0,
			headerHeight:156
	};
	
	this.handleWebSocketMessage = function($scope, object) {
		if(object.messageType === "statusMessage"){
			if(object.message){
				this.data.outboxStatusMessage = object.message;
			}
			if(object.progress){
				this.data.outboxProgress = object.progress;
			}
		}else if (object.messageType === "outboxUpdate"){
			this.data.spreadsheetData[object.index].status = object.status;
			this.data.batchMessageSpreadsheet.render();
		}
	};
	
	this.copyProperties = function(from, to){
		var newObject = {};
		for(var k in to) newObject[k]=to[k];
		for(var k in from) newObject[k]=from[k];
		return newObject;
	};
	
	this.buildOutboxMessages = function(){
		var data = this.data;
		var copyProperties = this.copyProperties;
		
		return $q(function(resolve, reject) {
			//long-running operation on main thread. setTimeout to give buttons time to animate. TODO: web worker
			setTimeout(function(){ 
				var outbox = copyProperties(data.credentials, {});
				outbox.messages = [];
				
				for(var i = 0; i < data.batchMessageSpreadsheet.getData().length; i++){
					outbox.messages.push({
							to:data.batchMessageSpreadsheet.getData()[i][0],
							subject:data.batchMessageSpreadsheet.getData()[i][1],
							message:data.batchMessageSpreadsheet.getData()[i][2],
							status:data.batchMessageSpreadsheet.getData()[i][3],
							index:i
					});
				}
				resolve(outbox);
			}, 500)
			
		});
	};
	
	this.initializeHandsOnTable = function($scope){
		for(var i = 0; i < 500; i++){
			this.data.spreadsheetData.push({
				to:'',
				subject:'',
				message:'',
				status:''
			});
		}
		
		if(this.data.existingOutboxData){
			for(var i = 0; i < this.data.existingOutboxData.length; i++){
				this.data.spreadsheetData[i].message = this.data.existingOutboxData[i].message;
				this.data.spreadsheetData[i].subject = this.data.existingOutboxData[i].subject;
				this.data.spreadsheetData[i].to = this.data.existingOutboxData[i].to;
				this.data.spreadsheetData[i].status = this.data.existingOutboxData[i].status;
			}
		}
		
		var hotSettings = {
			data : this.data.spreadsheetData,
			columns : [ {
				data : 'to',
				type : 'text',
				width : 40
			}, {
				data : 'subject',
				type : 'text',
				width : 40
			}, {
				data : 'message',
				type : 'text',
				width : 500
			}, {
				data : 'status',
				type : 'text',
				readOnly : true,
				width : 40
			} ],
			stretchH : 'all',
			width : '100%',
			autoWrapRow : true,
			height : window.innerHeight - this.data.headerHeight - 55,
			maxRows : 500,
			rowHeaders : true,
			colHeaders : [ 'To', 'Subject', 'Message', 'Status' ]
		};
		
		var hotElement = document.querySelector('#batchMessageSheet');
		this.data.batchMessageSpreadsheet = new Handsontable(hotElement, hotSettings);
	}
});