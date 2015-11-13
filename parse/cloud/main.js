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
		table.increment("player1Score", -1);	
	} else {
		table.increment("player2Score", -1);
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

Parse.Cloud.define("submitGame", function(request, response) {
	Parse.Cloud.useMasterKey();

	tableId = request.params.tableId;
	player1UserId = request.params.player1UserId;
	player2UserId = request.params.player2UserId;
	player1EloScore = request.params.player1EloScore;

	player1Query = new Parse.Query("User");
	player1Query.get(player1UserId);

	player2Query = new Parse.Query("User");
	player2Query.get(player2UserId);

	Parse.Query.or(player1Query, player2Query).find().then(function(players) {
		if (player1UserId === players[0].id) {
			player1Rating = players[0].get("rating");
			player2Rating = players[1].get("rating");
		} else {
			player1Rating = players[1].get("rating");
			player2Rating = players[0].get("rating");
		}
		return Parse.Cloud.httpRequest({url: 'http://174.1.53.209:19212/expectedEloChange?ratingA=' + player1Rating + '&ratingB=' + player2Rating + '&score=' + player1EloScore})
	}).then(function(httpResponse) {
		
		tokens = httpResponse.text.split("   ");
		player1Score = parseInt(tokens[0].split(" ")[3]);
		player2Score = parseInt(tokens[1].split(" ")[3]);

		var User = Parse.Object.extend("User");
		player1 = new User();
		player1.id = player1UserId;
		player1.set("rating", player1Score);

		player2 = new User();
		player2.id = player2UserId;
		player2.set("rating", player2Score);

		return player1.save().then(player2.save());
	}).then(Parse.Cloud.run("resetGame", {"tableId": tableId}))
	.then(function(users) {
		response.success("game submitted");
	}, function(error) {
		response.error(JSON.stringify(error));
	});
});

Parse.Cloud.define("resetGame", function(request, response) {
	tableId = request.params.tableId;

	var Table = Parse.Object.extend("Table");
	var table = new Table();
	table.id = tableId;

	table.set("locked", false);
	table.set("player1", null);
	table.set("player1Score", 0);
	table.set("player2", null);
	table.set("player2Score", 0);
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
