Parse.Cloud.define("lockTable", function(request, response) {
	tableId = request.params.tableId;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;

	// Set a new value on quantity
	table.set("locked", true);

	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("locked table");
	  },
	  error: function(table, error) {
	    response.error("Error locking table: " + error);
	  }
	});
});

Parse.Cloud.define("unlockTable", function(request, response) {
	tableId = request.params.tableId;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;

	table.set("locked", false);

	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("unlocked");
	  },
	  error: function(table, error) {
	    response.error("Error unlocking table: " + error);
	  }
	});
});

Parse.Cloud.define("playerScored", function(request, response) {
	tableId = request.params.tableId;
	playerNumber = request.params.playerNumber;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;
	if (playerNumber === 1) {
		table.increment("player1Score");	
	} else {
		table.increment("player2Score");
	}
	
	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("score updated");
	  },
	  error: function(table, error) {
	    response.error("Error updating score: " + error);
	  }
	});
});

Parse.Cloud.define("playerGoalDisallowed", function(request, response) {
	tableId = request.params.tableId;
	playerNumber = request.params.playerNumber;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;
	if (playerNumber === 1) {
		table.decrement("player1Score");	
	} else {
		table.decrement("player2Score");
	}
	
	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("score updated");
	  },
	  error: function(table, error) {
	    response.error("Error updating score: " + error);
	  }
	});
});