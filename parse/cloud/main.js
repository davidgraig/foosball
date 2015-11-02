Parse.Cloud.define("activateTable", function(request, response) {
	tableId = request.params.tableId;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;

	// Set a new value on quantity
	table.set("isInUse", true);

	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("activated");
	  },
	  error: function(table, error) {
	    response.error("Error activating table: " + error);
	  }
	});
});

Parse.Cloud.define("deactivateTable", function(request, response) {
	tableId = request.params.tableId;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;

	// Set a new value on quantity
	table.set("isInUse", false);

	// Save
	table.save(null, {
	  success: function(table) {
	    response.success("deactivated");
	  },
	  error: function(table, error) {
	    response.error("Error activating table: " + error);
	  }
	});
});