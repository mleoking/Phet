<?php

    // THIS FILE IS FOR DEBUGGING PURPOSES ONLY
    // NO FILE ON THE WEBSITE SHOULD REFERNCE THIS
    //
    // Now that that is out of the way, this is for
    // hard to understand errors that happen only
    // on tigercat.  Use it if things aren't downloading
    // and it doesn't make sense.  It is usually needed
    // for weirdness in permissions or configuration
    // that is difficult to replicate exactly.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/sim-utils.php");
    require_once("include/sys-utils.php");
    require_once("include/web-utils.php");
    require_once("include/log-utils.php");

    error_reporting(E_ALL);
    ini_set('display_errors', 1);

    // Get the simulation id
    $sim_id = $_REQUEST['sim_id'];

    // Get the language, if present
    $language_code = "en";
    if (isset($_REQUEST["lang"])) {
        $language_code = $_REQUEST["lang"];
    }

    // Get the simulation data
    $simulation = sim_get_sim_by_id($sim_id);

    // Log this info
    $datefmt = "y/m/d h:i:s A";
    $log_string = join("\t", array(date($datefmt), time(), $simulation['sim_name'], $language_code, $_SERVER['REMOTE_ADDR']))."\n";
    log_message('download-sim.log', $log_string);

    // Get the filename and content
    $download_data = sim_get_run_offline($simulation, $language_code);

    if ($download_data) {
        $filename = $download_data[0];
        $contents = $download_data[1];
        send_file_to_browser($filename, $contents, null, "attachment");
    }

?>
