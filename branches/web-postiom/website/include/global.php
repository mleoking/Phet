<?php

  // TEMP TO TEST PRE AND POST IOM STUFF
    define('PORTAL_ROOT', '../'.SITE_ROOT);
    if (strpos($_SERVER['SCRIPT_NAME'], '/PhET-method1') === 0) {
        // Case of POST IOM code setting, POST IOM sims
        $GLOBALS['PREIOM_CODE'] = FALSE;
        $GLOBALS['IOM_METHOD'] = 1;
        $GLOBALS['EXTRA_TITLE'] = 'POST IOM CODE ONLY, POST IOM SIMS';
        define("SIMS_ROOT", PORTAL_ROOT.'sims-postiom/');
    }
    else if (strpos($_SERVER['SCRIPT_NAME'], '/PhET-method2') === 0) {
        // Case of PRE IOM code setting, POST IOM sims
        $GLOBALS['PREIOM_CODE'] = TRUE;
        $GLOBALS['IOM_METHOD'] = 2;
        $GLOBALS['EXTRA_TITLE'] = 'PRE IOM CODE ENABLED, POST IOM SIMS';
        define("SIMS_ROOT", PORTAL_ROOT.'sims-postiom/');
    }
    else if (strpos($_SERVER['SCRIPT_NAME'], '/PhET-method3') === 0) {
        // Case of POST IOM code setting, PRE IOM sims
        $GLOBALS['PREIOM_CODE'] = FALSE;
        $GLOBALS['IOM_METHOD'] = 3;
        $GLOBALS['EXTRA_TITLE'] = 'POST IOM CODE ONLY, PRE IOM SIMS';
        define("SIMS_ROOT", PORTAL_ROOT.'sims-current/');
    }
    else if (strpos($_SERVER['SCRIPT_NAME'], '/PhET-method4') === 0) {
        // Case of PRE IOM code setting, PRE IOM sims
        $GLOBALS['PREIOM_CODE'] = TRUE;
        $GLOBALS['IOM_METHOD'] = 4;
        $GLOBALS['EXTRA_TITLE'] = 'PRE IOM CODE ENABLED, PRE IOM SIMS';
        define("SIMS_ROOT", PORTAL_ROOT.'sims-current/');
    }
    else {
        // act normally
        $GLOBALS['PREIOM_CODE'] = FALSE;
        $GLOBALS['IOM_METHOD'] = 0;
        $GLOBALS['EXTRA_TITLE'] = 'Running Normally';
    }

    // Temp hack to get around magic_quotes_gpc until admins can set it
    //
    if (get_magic_quotes_gpc()) {
        function undoMagicQuotes($array, $topLevel=true) {
            $newArray = array();
            foreach($array as $key => $value) {
                if (!$topLevel) {
                    $key = stripslashes($key);
                }
                if (is_array($value)) {
                    $newArray[$key] = undoMagicQuotes($value, false);
                }
                else {
                    $newArray[$key] = stripslashes($value);
                }
            }
            return $newArray;
        }
        $_GET = undoMagicQuotes($_GET);
        $_POST = undoMagicQuotes($_POST);
        $_COOKIE = undoMagicQuotes($_COOKIE);
        $_REQUEST = undoMagicQuotes($_REQUEST);
    }

    // This should be included, first thing
    require_once('update-include-path.php');
    @include_once("local-debug-settings.php");

    // This will set up the classes to autoload
    require_once("class-loader.php");

    function debug_is_on() {
        if (isset($GLOBALS["DEBUG"]) && $GLOBALS["DEBUG"]) {
            return true;
        }
        else {
            return false;
        }
    }

    if (!debug_is_on()) {
        error_reporting(0);
        ini_set('display_errors', 0);
        assert_options(ASSERT_ACTIVE, 0);
    }

    
    // TODO: move this back into the NavBar class
define("NAV_NOT_SPECIFIED",  -1);
define("NAV_INVALID0",  0);
define("NAV_INVALID1",  1);
define("NAV_SIMULATIONS",  2);
define("NAV_TEACHER_IDEAS",  3);
define("NAV_GET_PHET",  4);
define("NAV_TECH_SUPPORT",  5);
define("NAV_CONTRIBUTE",  6);
define("NAV_RESEARCH",  7);
define("NAV_ABOUT_PHET",  8);
define("NAV_ADMIN",  9);
define("NAV_COUNT",  10);

    //
    // Defines
    //
    // If you want to change these to be specific to
    // your development machine, set them in 
    // local-debug-settings.php
    //

    // SITE_ROOT is used for locating things relative to the
    // site's root, which may be different than DOCUMENT_ROOT
    // It is used for things like referencing images or css
    // files.  It should already be defined elsewhere, this
    // is just for safety.
    assert(defined("SITE_ROOT"));
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // PORTAL_ROOT is slightly different than SITE_ROOT in
    // that it is used for looking for directories and other
    // things "outside the scope of the webserver directories".
    // This allows you to have things like 'phet-dist' and
    // 'sims' in a different directory than the site's root.
    // This is how it is on my development machine, it makes it
    // easier to manage SVN and a few other things.
    if (!defined("PORTAL_ROOT")) define("PORTAL_ROOT", SITE_ROOT);

    // CACHE_ROOT is used to tell where to put the cache directory.
    // I also like to keep this out of my website directories.
    // TODO: make this point to the root directory of the cache,
    // not the directory in which the webcache file will be made
    if (!defined("CACHE_ROOT")) define("CACHE_ROOT", PORTAL_ROOT);

    // CACHE_DIRNAME is used for the name under which all cached
    // files will be placed.
    if (!defined("CACHE_DIRNAME")) define("CACHE_DIRNAME", "webcache");

    // SIMS_ROOT is where to look for the simulation files
    if (!defined("SIMS_ROOT")) define("SIMS_ROOT", PORTAL_ROOT.'sims/');

    // PHET_DIST_ROOT is the root of the phet-dist directory
    if (!defined("PHET_DIST_ROOT")) define("PHET_DIST_ROOT", PORTAL_ROOT.'phet-dist/');
    
    // Convenience defines for PhET related contact
    define("PHET_DOMAIN_NAME", "phet.colorado.edu");
    define("PHET_HELP_EMAIL", "phethelp@colorado.edu");

    // Latest versions of flash and java
    define("OS_MIN_VERSION_WIN", "Microsoft Windows 98SE/2000/XP/Vista");
    define("OS_MIN_VERSION_OSX", "10.4");

    define("JAVA_MIN_VERSION_GENERIC", "1.5");
    define("JAVA_MIN_VERSION_WIN", "1.5.0_15");
    define("JAVA_MIN_VERSION_OSX", "1.5.0_16");
    define("JAVA_MIN_VERSION_LIN", "1.5.0_15");
    define("JAVA_MIN_VERSION_GENERIC_FULL", JAVA_MIN_VERSION_GENERIC."1.5");
    define("JAVA_MIN_VERSION_WIN_FULL", "Sun Java ".JAVA_MIN_VERSION_WIN);
    define("JAVA_MIN_VERSION_OSX_FULL", "Apple Java ".JAVA_MIN_VERSION_OSX);
    define("JAVA_MIN_VERSION_LIN_FULL", "Sun Java ".JAVA_MIN_VERSION_LIN);
    define("FLASH_MIN_VERSION", "8");
    define("FLASH_MIN_VERSION_FULL", "Macromedia Flash 8");

    // TODO: move this out of this class to where it belongs
    /*
    define("NAV_NOT_SPECIFIED",  -1);
    define("NAV_INVALID0",  0);
    define("NAV_INVALID1",  1);
    define("NAV_SIMULATIONS",  2);
    define("NAV_TEACHER_IDEAS",  3);
    define("NAV_GET_PHET",  4);
    define("NAV_TECH_SUPPORT",  5);
    define("NAV_CONTRIBUTE",  6);
    define("NAV_RESEARCH",  7);
    define("NAV_ABOUT_PHET",  8);
    define("NAV_ADMIN",  9);
    define("NAV_COUNT",  10);
    */

    // When a simulation is created, an empty simulation is added
    // to the database (yes, before the data has been populated, and
    // yes that's not so great but I've been told repeatedly not to
    // spend much time fixing this blemish).  This is the temparary
    // name of that simulation.
    define("DEFAULT_NEW_SIMULATION_NAME", "New Simulation");

    ini_set('upload_max_filesize',    '20M');
    ini_set("session.gc_maxlifetime", "10800"); 
    ini_set("session.cache_expire",   "180");
    /*
     * Not sure why these are so high... makes for a lot of garbage piling up
     * changing these to lower numbers, about 3 hours
    ini_set("session.gc_maxlifetime", "999999999"); 
    ini_set("session.cache_expire",   "999999999");
    */

    if (isset($GLOBALS['IE6_DOWNLOAD_WORKAROUND']) &&
        ($GLOBALS['IE6_DOWNLOAD_WORKAROUND'])) {
        // Workaround for IE6 which has a bug with meta refresh
        // and downloading files.  Double check this against the
        // local test machine before uncommenting.
        $browser = $_SERVER['HTTP_USER_AGENT'];
        if (strstr($browser, 'MSIE 5.5') ||
            strstr($browser, 'MSIE 6.0')) {
            session_cache_limiter('must-revalidate');
        }
    }

     // Set the timezone for stricter compliance
    ini_set("date.timezone", "America/Denver");

    // Debugging aids
    function microtime_float() {
        list($usec, $sec) = explode(" ", microtime());
        return ((float)$usec + (float)$sec);
    }

?>
