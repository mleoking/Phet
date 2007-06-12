<?php
    include_once("db.inc");
    include_once("web-utils.php");
    
    function db_verify_mysql_result($result, $statement) {
        if (!$result && $statement !== "") {
            $message  = 'Invalid query: ' . mysql_error() . "<br/>";
            $message .= 'Whole query: ' . $statement;

            die($message);
        }
    }

    function db_exec_query($statement) {
        global $connection;
    
        $result = mysql_query($statement, $connection);
    
        db_verify_mysql_result($result, $statement);
    
        return $result;
    }
    
    function db_get_row_by_id($table_name, $id_name, $id_value) {
        $rows = db_exec_query("SELECT * FROM `$table_name` WHERE `$id_name`='$id_value' ");
        
        if (!$rows) return FALSE;

        $assoc = mysql_fetch_assoc($rows);
        
        $cleaned = array();
        
        foreach($assoc as $key => $value) {
            $cleaned["$key"] = format_for_html("$value");
        }
    }
    
    function db_delete_row($table_name, $array) {
        $delete_st = "DELETE FROM $table_name WHERE ";
        
        $is_first = true;
        
        foreach($array as $key => $value) {
            if ($is_first) {
                $is_first = false;
            }
            else {
                $delete_st .= ' AND ';
            }
            
            $value = mysql_real_escape_string($value);
            
            $delete_st .= "`$key`='$value'";
        }
        
        return db_exec_query($delete_st);
    }
    
    function db_insert_row($table_name, $array) {
        $insert_st = "INSERT INTO $table_name ";
        
        if (count($array) > 0) {
            $insert_st .= '(';
            
            $is_first = true;
            
            foreach($array as $key => $value) {
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $insert_st .= ', ';
                }
                
                $insert_st .= '`';                
                $insert_st .= "$key";                
                $insert_st .= '`';                
            }
            
            $insert_st .= ')';
            
            $insert_st .= ' VALUES(';
            
            $is_first = true;
            
            foreach($array as $key => $value) {
                if ($is_first) {
                    $is_first = false;
                }
                else {
                    $insert_st .= ', ';
                }
                
                $value = mysql_real_escape_string($value);
                
                $insert_st .= "'";
                $insert_st .= "$value";
                $insert_st .= "'";
            }
            
            $insert_st .= ') ';
        }
        
        db_exec_query($insert_st);
        
        return mysql_insert_id();
    }
    
    function db_get_blank_row($table_name) {
        $row = array();
        
        $result = mysql_query("SHOW COLUMNS FROM `$table_name` ");
        
        if ($result) {
            while ($column = mysql_fetch_assoc($result)) {
                $field_name = $column['Field'];
                
                $row["$field_name"] = '';
            }
        }
        
        $row["${table_name}_id"] = -1;
        
        return $row;
    }
    
    function db_simplify_sql_timestamp($timestamp) {
        $time = strtotime($timestamp);
    
        return date('n/y', $time);
    }
    
    function db_update_table($table_name, $update_array, $id_field_name = null, $id_field_value = null) {
        if (count($update_array) == 0 || count($update_array) == 1 && isset($update_array["$id_field_name"])) {
            return true;
        }
        
        $heading_st = "UPDATE `$table_name` SET ";
        
        $content_st = '';
        
        $first_item_already_printed = false;
        
        foreach($update_array as $key => $value) {
            if ($key !== $id_field_name) {
                if ($first_item_already_printed) {
                    $content_st .= ", ";
                }
                
                $value = mysql_real_escape_string($value);

                $content_st .= " `$key`='$value' ";
            
                $first_item_already_printed = true;
            }
        }
        
        if ($id_field_name !== null && $id_field_value !== null) {
            $footer_st = " WHERE `$id_field_name`='$id_field_value' ";
        }
        else {
            $footer_st = '';
        }
            
        db_exec_query($heading_st.$content_st.$footer_st);
        
        return true;
    }
?>