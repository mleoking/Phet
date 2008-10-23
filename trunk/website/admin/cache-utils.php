<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/sys-utils.php");

    define("WEBPAGES_CACHE",             "webpages");
    define("HOURS_TO_CACHE_WEBPAGES",     1);

    // FIXME: Caching uses the $_SESSION variable to generate a name.  We are now doing a
    // session_start and session_write_close() to keep the session open for as short a
    // time as possible.  Using this global to be set when the session is open to genearte
    // the name that will be used with the cache.  All this caching stuff should be
    // integrated more tightly into SitePage.
    $cache_page_name = null;

    // Disable all caching when run on developer's machine:
    //$g_disable_all_caching = ((isset($_SERVER['SERVER_NAME'])) && ($_SERVER['SERVER_NAME'] == 'localhost')) ? true : false;

    // FIXME: horrible implementation
    function cache_setup_page_name() {
        global $cache_page_name;

        $cache_page_name = cache_auto_get_page_name();
    }

    function cache_enabled() {
        if ((isset($_SERVER['SERVER_NAME'])) &&
            ($_SERVER['SERVER_NAME'] == 'localhost')) {
            if (isset($GLOBALS["DEBUG_FORCE_LOCAL_CACHE"]) && $GLOBALS["DEBUG_FORCE_LOCAL_CACHE"]) {
                return true;
            }
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Set the group for the file (recursive if file is a directory)
     *
     *
     * @param string $file - name of the file to change the permissions on
     */
    function create_proper_ownership($file) {
        exec('chmod 775 '.$file);

        if (is_dir($file)) {
            exec('chgrp --recursive phet '.$file);
        }
        else {
            exec('chgrp phet '.$file);
        }
    }

    function cache_get_cache_root_dir() {
       return CACHE_ROOT.CACHE_DIRNAME.DIRECTORY_SEPARATOR; 
    }

    /**
     * Get the root cache directory for the running script.
     *
     * @return string path to the root directory where the running script should be cached
     */
    function cache_get_script_root_dir() {
        // Explode the directories to the root directory where the cache will be
        $p = explode(DIRECTORY_SEPARATOR, CACHE_ROOT);

        // Explode the path (path only) of the running script
        $d = explode(DIRECTORY_SEPARATOR, dirname($_SERVER["PHP_SELF"]));

        // Get the tail directories, the same number to get to the cache root
        $s = array_slice($d, -(count($p) - 1));

        // Build the directory name
        $cache_dir = cache_get_cache_root_dir().implode(DIRECTORY_SEPARATOR, $s).DIRECTORY_SEPARATOR;

        // return the result
        return $cache_dir;
    }

    /**
     * TODO: Update this comment when I understand cache fully
     * ?? return './cache-$cache_name'
     *
     * @param string $cache_name - name of the file in cache?
     * @return string './cached-$cache_name'
     */
    function cache_get_location($cache_name) {
        // Location:
        // mixing this between old and new.
        // Old method: just place a cached-$cache_name file
        // in the current directory.
        // New method: in the PORTAL_ROOT, have a directory
        // named cache.  Inside cache, we have a mirror structure
        // of the website, but it only contains caching.
        // Example: we want to cache the file generated by
        //   phet.colorado.edu/teacher_ideas/browse.php
        // old method would put it here:
        //   phet.colorado.edu/teacher_ideas/cached-browse-pages
        // new method puts it here:
        //   phet.colorado.edu/cache/teacher_ideas/cached-browse-pages

        $dir = cache_get_script_root_dir()."cached-$cache_name";
        mkdir_recursive($dir, 0770);
        return $dir;
    }


    function cache_get_file_location($cache_name, $resource_name) {
        return cache_get_location($cache_name)."/$resource_name";
    }

    /**
     * Clear the specified cache, or the ENTIRE cache if nothing is specified
     *
     * @param $cache_name string[optional] name of cache to clear, or blank fro ALL caches
     * @return bool true if successful, false if not
     */
    function cache_clear($cache_name = null) {
        if (is_null($cache_name)) {
            $dir = cache_get_cache_root_dir();
        }
        else if (!empty($cache_name)) {
            $dir = cache_get_cache_root_dir().$cache_name.DIRECTORY_SEPARATOR;
        }
        else {
            return false;
        }

        exec("rm -rf {$dir}");
        return (!file_exists($dir));
    }

    function cache_clear_all() {
        return cache_clear();
    }

    function cache_clear_simulations() {
        // Need to clear simulations to clear the web pages, thumbnails, etc
        // Need to clear admin to get rid of the cached flash i18n JAR files
        // Need to clear teacher ideas, since that references the simulations
        return (cache_clear("simulations") && cache_clear("admin") && cache_clear("teacher_ideas"));
    }

    function cache_clear_admin() {
        return cache_clear("admin");
    }

    function cache_clear_teacher_ideas() {
        // Teacher ideas and simulations both reference each other, need to clear both
        return (cache_clear("simulations") && cache_clear("teacher_ideas"));
    }

    /**
     * Cache the given resource
     *
     * @param string $cache_name - cache name of the resource
     * @param string $resource_name - name of the resource
     * @param unknown_type $resource_contents - contents of the resource (like a web page)
     * @return unknown - same return value as flock_put_contents
     */
    function cache_put($cache_name, $resource_name, $resource_contents) {
        $cache_dir = cache_get_location($cache_name);

        if (!file_exists($cache_dir)) {
            mkdir($cache_dir);
            create_proper_ownership($cache_dir);
        }

        $resource_location = cache_get_file_location($cache_name, $resource_name);

        $return_value = flock_put_contents($resource_location, $resource_contents);


        create_proper_ownership($resource_location);

        return $return_value;
    }


    function cache_get($cache_name, $resource_name, $expiration_hours = false) {
        if (!cache_enabled()) return false;

        $resource_location = cache_get_file_location($cache_name, $resource_name);

        if (!file_exists($resource_location)) {
            return false;
        }

        if (is_numeric($expiration_hours)) {
            $time = filemtime($resource_location);

            $diff = time() - $time;

            // Refresh the cache every 24 hours:
            if ($diff > $expiration_hours * 60 * 60) {
                return false;
            }
        }

        return flock_get_contents($resource_location);
    }


    function cache_auto_get_page_name() {
        // FIXME: horrible implementation
        global $cache_page_name;
        if (!is_null($cache_page_name)) {
            return $cache_page_name;
        }

        $hash_contents = $_SERVER['REQUEST_URI'];

        foreach ($_SESSION as $key => $value) {
            $hash_contents .= "$key=>$value";
        }

        return md5($hash_contents).'.html';
    }


    function cache_has_valid_page($cache_name = WEBPAGES_CACHE, $orig_resource_name = false, $expiration_hours = HOURS_TO_CACHE_WEBPAGES) {
        if (!cache_enabled()) return;

        if (!$orig_resource_name) {
            $resource_name = cache_auto_get_page_name();
        }
        else {
            $resource_name = $orig_resource_name;
        }

        $resource_location = cache_get_file_location($cache_name, $resource_name);

        $result = file_exists($resource_location);

        if (!file_exists($resource_location)) {
            return false;
        }

        if (is_numeric($expiration_hours)) {
            $time = filemtime($resource_location);

            $diff = time() - $time;

            // Check if the page is still fresh enough
            if ($diff > $expiration_hours * 60 * 60) {
                return false;
            }
            // TODO: else, delete it!
        }

        return true;
    }


    /**
     * Starts caching the current webpage. Must be called before any content printed.
     */
    function cache_auto_start() {
        if (!cache_enabled()) return;

        $page_name = cache_auto_get_page_name();

        $cached_page = cache_get(WEBPAGES_CACHE, $page_name, HOURS_TO_CACHE_WEBPAGES);

        if ($cached_page) {
            print $cached_page;

            exit;
        }
        else {
            ob_start();
        }
    }


    /**
     * Ends caching the current webpage. Must be called after all content printed.
     */
    function cache_auto_end() {
        if (!cache_enabled()) return;

        $page_name = cache_auto_get_page_name();

        $page_contents = ob_get_contents();
        // TODO: printing scheme is slow, change to get_flush and printing the result
        //$page_contents = ob_get_flush();

        $page_contents = preg_replace('/^ +/',       '',   $page_contents);
        $page_contents = preg_replace('/[ \t]{2,}/', ' ',  $page_contents);

        if (debug_is_on()) {
            // Change the css file
            $page_contents = preg_replace('/main.css/', 'main-cached.css', $page_contents);

            // Add a timestamp
            $timestamp = date("F j, Y, g:i a");
            $page_contents = $page_contents . "<!-- {$timestamp} -->\n";
        }

        cache_put(WEBPAGES_CACHE, $page_name, $page_contents);

        ob_end_flush();
    }

?>