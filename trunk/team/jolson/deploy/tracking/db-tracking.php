<?php
	include("db-tracking-login.php");
	
	// define sim_type to correspond with website code
	define("SIM_TYPE_JAVA", "0");
	define("SIM_TYPE_FLASH", "1");
	
	// used for every mysql query that needs to be made
	// useful for debugging and error catching
	function phet_mysql_query($query) {
		print "<p>" . $query . "</p>";
		
		// actually execute the query
		$result = mysql_query($query);
		
		//print "<p>" . mysql_error() . "</p>";
		//$result | die();
		return $result;
	}
	
	// get the id value corresponding to a unique value. if it doesn't exist, create a new row
	//
	// IMPORTANT: $table_field_id should be the AUTO_INCREMENT field for using this function
	// IMPORTANT: mysql_real_escape_string should be called on $table_value beforehand, since
	// strings should be quoted before they reach here (mysql_real_escape_string here would
	// ruin those quotes)
	//
	// example: get_id_value('flash_os', 'id', 'name', 'Mac OS X');
	// this would check to see whether 'Mac OS X' existed in the flash_os table under name.
	// if it exists, it returns the value of 'id' for that row
	// if it does not exist, it is inserted, and the value of the auto_increment field (usually id) is returned
	function get_id_value($table_name, $table_field_id, $table_field_value, $table_value) {
		// POSSIBLE TODO: update with things similar to "INSERT INTO java_vendor (name) SELECT 'Not Sun' FROM java_vendor WHERE NOT EXISTS (SELECT id FROM java_vendor WHERE name = 'Not Sun');"
		
		// we cannot use "= NULL" since NULL != NULL, thus we need a separate check if our
		// value is NULL
		if($table_value != "NULL") {
			// selects all rows which have that value (should be just 1, but will not error if there are more)
			$query = "SELECT {$table_field_id} FROM {$table_name} WHERE {$table_field_value} = {$table_value}";
		} else {
			// selects all rows which have that value (should be just 1, but will not error if there are more)
			$query = "SELECT {$table_field_id} FROM {$table_name} WHERE {$table_field_value} IS NULL";
		}
		
		// execute the query
		$result = phet_mysql_query($query);
		
		// the number of rows that match the SELECT statement above
		// should be either 0 or 1, however this will work if more are selected
		// if 0, our value is not in the table
		$num_rows = mysql_num_rows($result);
		
		if($num_rows == 0) {
			// our value is not in the table, so we need to insert it
			$insert_query = "INSERT INTO {$table_name} ({$table_field_value}) VALUES ({$table_value})";
			phet_mysql_query($insert_query);
			
			// return the value of the auto_increment field (should be ID).
			// this allows us to not execute another query
			return mysql_insert_id();
		} else {
			// our value is in the table. fetch the first row (should be the only row)
			$row = mysql_fetch_row($result);
			
			// return the ID
			return $row[0];
		}
	}
	
	// surround a string with quotes, and escape it
	function quo($str) {
		return "'" . mysql_real_escape_string($str) . "'";
	}
	
	// return either a quoted string, or NULL if the value is one of the strings mapped to NULL
	function quote_null_if_none($value) {
		if($value == "none" || $value == "null" || $value == "undefined") {
			return "NULL";
		} else {
			return quo($value);
		}
	}
	
	// turn a table name and associative array into an insert statement into that table
	// IMPORTANT: everything in $values should be safe for mysql (use mysql_real_escape_string
	// on things that came from external input)
	function query_from_values($table_name, $values) {
		$query = "INSERT INTO {$table_name} (";
		$query .= join(', ', array_keys($values));
		$query .= ") VALUES (";
		$query .= join(', ', array_values($values));
		$query .= ");";
		return $query;
	}
	
	// insert data into the session table
	function insert_session($data) {
		// get IDs from normalized tables
		$sim_project_ID = get_id_value("sim_project", "id", "name", quo($data['sim_project']));
		$sim_name_ID = get_id_value("sim_name", "id", "name", quo($data['sim_name']));
		$sim_deployment_ID = get_id_value("deployment", "id", "name", quote_null_if_none($data['sim_deployment']));
		$sim_distribution_tag_ID = get_id_value("distribution_tag", "id", "name", quote_null_if_none($data['sim_distribution_tag']));
		$host_simplified_os_ID = get_id_value("simplified_os", "id", "name", quo($data['host_simplified_os']));
		
		$values = array(
			'message_version' => mysql_real_escape_string($data['message_version']),
			'sim_type' => mysql_real_escape_string($data['sim_type']),
			'sim_project' => $sim_project_ID,
			'sim_name' => $sim_name_ID,
			'sim_major_version' => mysql_real_escape_string($data['sim_major_version']),
			'sim_minor_version' => mysql_real_escape_string($data['sim_minor_version']),
			'sim_dev_version' => mysql_real_escape_string($data['sim_dev_version']),
			'sim_svn_revision' => mysql_real_escape_string($data['sim_svn_revision']),
			'sim_locale_language' => quo($data['sim_locale_language']),
			'sim_locale_country' => quote_null_if_none($data['sim_locale_country']),
			'sim_sessions_since' => mysql_real_escape_string($data['sim_sessions_since']),
			'sim_sessions_ever' => mysql_real_escape_string($data['sim_sessions_ever']),
			'sim_deployment' => $sim_deployment_ID,
			'sim_distribution_tag' => $sim_distribution_tag_ID,
			'sim_dev' => mysql_real_escape_string($data['sim_dev']),
			'host_locale_language' => quo($data['host_locale_language']),
			'host_locale_country' => quote_null_if_none($data['host_locale_country']),
			'host_simplified_os' => $host_simplified_os_ID,
		);
		
		// build query from values to be inserted
		$query = query_from_values("session", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert data into the flash_info table
	function insert_flash_info($data) {
		// get IDs from normalized tables
		$host_flash_version_type_ID = get_id_value("flash_version_type", "id", "name", quo($data['host_flash_version_type']));
		$host_flash_domain_ID = get_id_value("flash_domain", "id", "name", quo($data['host_flash_domain']));
		$host_flash_os_ID = get_id_value("flash_os", "id", "name", quo($data['host_flash_os']));
		
		$values = array(
			'session_id' => mysql_real_escape_string($data['session_id']),
			'host_flash_version_type' => $host_flash_version_type_ID,
			'host_flash_version_major' => mysql_real_escape_string($data['host_flash_version_major']),
			'host_flash_version_minor' => mysql_real_escape_string($data['host_flash_version_minor']),
			'host_flash_version_revision' => mysql_real_escape_string($data['host_flash_version_revision']),
			'host_flash_version_build' => mysql_real_escape_string($data['host_flash_version_build']),
			'host_flash_time_offset' => mysql_real_escape_string($data['host_flash_time_offset']),
			'host_flash_accessibility' => mysql_real_escape_string($data['host_flash_accessibility']),
			'host_flash_domain' => $host_flash_domain_ID,
			'host_flash_os' => $host_flash_os_ID
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_flash_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert data into the java_info table
	function insert_java_info($data) {
		// get IDs from normalized tables
		$host_java_os_name_ID = get_id_value("java_os_name", "id", "name", quo($data['host_java_os_name']));
		$host_java_os_version_ID = get_id_value("java_os_version", "id", "name", quo($data['host_java_os_version']));
		$host_java_os_arch_ID = get_id_value("java_os_arch", "id", "name", quo($data['host_java_os_arch']));
		$host_java_vendor_ID = get_id_value("java_vendor", "id", "name", quo($data['host_java_vendor']));
		$host_java_webstart_version_ID = get_id_value("java_webstart_version", "id", "name", quote_null_if_none($data['host_java_webstart_version']));
		$host_java_timezone_ID = get_id_value("java_timezone", "id", "name", quo($data['host_java_timezone']));
		
		$values = array(
			'session_id' => mysql_real_escape_string($data["session_id"]),
			'host_java_os_name' => $host_java_os_name_ID,
			'host_java_os_version' => $host_java_os_version_ID,
			'host_java_os_arch' => $host_java_os_arch_ID,
			'host_java_vendor' => $host_java_vendor_ID,
			'host_java_version_major' => mysql_real_escape_string($data['host_java_version_major']),
			'host_java_version_minor' => mysql_real_escape_string($data['host_java_version_minor']),
			'host_java_version_maintenance' => mysql_real_escape_string($data['host_java_version_maintenance']),
			'host_java_webstart_version' => $host_java_webstart_version_ID,
			'host_java_timezone' => $host_java_timezone_ID
		);
		
		// build query from values to be inserted
		$query = query_from_values("session_java_info", $values);
		
		phet_mysql_query($query);
		
		// return the ID (value of the auto_increment field) of the row we inserted, so we can
		// use it for other queries
		return mysql_insert_id();
	}
	
	// insert an entire flash message
	function insert_flash_message($data) {
		// this is a Flash sim
		$data['sim_type'] = SIM_TYPE_FLASH;
		
		// calculate hostSimplifiedOS
		$data['host_simplified_os'] = "Unknown";
		$type = $data['host_flash_version_type'];
		if($type == 'WIN') {
			$data['host_simplified_os'] = "Windows - General";
			$os = $data['host_flash_os'];
			if(stripos($os, 'Vista') !== false) {
				$data['host_simplified_os'] = "Windows - Vista";
			} else if(stripos($os, 'XP') !== false) {
				$data['host_simplified_os'] = "Windows - XP";
			}
		} else if($type == 'MAC') {
			$data['host_simplified_os'] = "Mac - General";
		} else if($type == 'LNX') {
			$data['host_simplified_os'] = "Linux - General";
		} else if($type == 'UNIX') {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		$data['session_id'] = $sessionID;
		
		insert_flash_info($data);
		
		return $sessionID;
	}
	
	// insert an entire java message
	function insert_java_message($data) {
		// this is a Java sim
		$data["sim_type"] = SIM_TYPE_JAVA;
		
		// calculate hostSimplifiedOS
		$data['host_simplified_os'] = "Unknown";
		$osname = $data['host_java_os_name'];
		if(stripos($osname, 'Windows') !== false) {
			$data['host_simplified_os'] = "Windows - General";
			if(stripos($osname, 'Vista') !== false) {
				$data['host_simplified_os'] = "Windows - Vista";
			} else if(stripos($osname, 'XP') !== false) {
				$data['host_simplified_os'] = "Windows - XP";
			}
		} else if(stripos($osname, 'Mac') !== false) {
			$data['host_simplified_os'] = "Mac - General";
		} else if(stripos($osname, 'Linux') !== false) {
			$data['host_simplified_os'] = "Linux - General";
		} else if(stripos($osname, 'Unix') !== false) {
			$data['host_simplified_os'] = "Unix - General";
		}
		
		// store the ID of the inserted session
		$sessionID = insert_session($data);
		
		$data['session_id'] = $sessionID;
		
		insert_java_info($data);
		
		return $sessionID;
	}
	
	// insert/update data for the user table
	function update_user(
		$userPreferencesFileCreationTime,
		$userTotalSessions
	) {
		$safe_time = mysql_real_escape_string($userPreferencesFileCreationTime);
		$safe_sessions = mysql_real_escape_string($userTotalSessions);
		// we need to find out whether an entry exists for this particular file creation time
		$query = "SELECT user_preferences_file_creation_time FROM user WHERE user_preferences_file_creation_time = " . $safe_time . ";";
		$result = phet_mysql_query($query);
		
		// number of rows that match the above query. should be 1 if the user has been seen before,
		// and 0 if they haven't been seen
		$num_rows = mysql_num_rows($result);
		
		if($num_rows === 0) {
			// first time this user is seen
			
			// values to be inserted
			$values = array(
				'user_preferences_file_creation_time' => $safe_time,
				'user_total_sessions' => $safe_sessions,
				'first_seen_month' => quo(date("Y-m-01", time())), // current year and month
				'last_seen_month' => quo(date("Y-m-01", time())) // current year and month
			);
			$insert_query = query_from_values("user", $values);
			phet_mysql_query($insert_query);
		} else {
			// user already in table, update values
			
			// update total sessions
			$update_query = "UPDATE user SET user_total_sessions = {$safe_sessions} WHERE user_preferences_file_creation_time = {$safe_time}";
			phet_mysql_query($update_query);
			
			// update last_seen_month with current year and month
			$last_seen_month = quo(date("Y-m-01", time()));
			$update_query = "UPDATE user SET last_seen_month = {$last_seen_month} WHERE user_preferences_file_creation_time = {$safe_time}";
			phet_mysql_query($update_query);
		}
	}
?>