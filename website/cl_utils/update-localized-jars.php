#!/usr/local/php/bin/php
<?php

// Note: To run this locally, your interpreter may be different
// than the one specified on the first line.  If that is the case,
// run your local version of the command line with this file as
// an argument.
// Example, on my machine PHP is in /usr/bin, which is in my path:
// $ php update-localized-jars.php
// If it wasn't in my path, it would be:
// $ /usr/bin/php update-localized-jars.php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."admin/sim-utils.php");

// Turn error reporting back on
error_reporting(E_ALL);

// Create our own exception
class LocalizeExceptioneption extends Exception {}


/**
 * Print only if verbose requested.
 *
 * @param string $msg message to print
 */
function verbose_print($msg) {
    global $verbose;
    if ($verbose) {
        print $msg;
    }
}

/**
 * Append text to a textfile in the specified JAR.  Does it this way:
 * 1.  Extract the text file from the JAR (if it exists)
 * 2.  Append the specified text to the file
 * 3.  Update the jar
 * 4.  Remove the text file
 * 
 * Note:  This is done in the current directory, do not run this command
 * if there is already a text file in this dir with the same name that
 * you don't want to overwrite.
 *
 * @param string $jar_name filename of the JAR to update
 * @param string $file_name name if the text file within the JAR
 * @param string $text the text to add (EOL is NOT added)
 */
function jar_append_to_textfile($jar_name, $file_name, $text) {
    // Extract the options.properties from the JAR
    verbose_print("   extracting original '{$file_name}' (if it exists)...\n");
    $command = JAR_EXECUTABLE." xf {$jar_name} {$file_name}";
    $ret = system($command, $ret_val);
    if ($ret_val !== 0) {
        throw new LocalizeExceptioneption("system command '{$command}' failed, return value '{$ret_val}, text '{$ret}'");
    }

    // Append the text to the file
    verbose_print("   appending text to '{$file_name}'...\n");
    $fp = fopen($file_name, 'a');
    if ($fp === false) {
        throw new LocalizeExceptioneption("cannot create '{$file_name}'");
    }

    $ret = fwrite($fp, $text);
    if ($ret === false) {
        throw new LocalizeExceptioneption("write failed on '{$file_name}'");
    }

    $ret = fclose($fp);
    if ($ret === false) {
        throw new LocalizeExceptioneption("cannot close '{$file_name}'");
    }

    // Update the {$file_name} in the localized JAR file
    verbose_print("   updating {$file_name} in the JAR file '{$jar_name}...'\n");
    $command = JAR_EXECUTABLE." uf {$jar_name} -C . {$file_name}";
    $ret = system($command, $ret_val);
    if ($ret_val !== 0) {
        throw new LocalizeExceptioneption("system command '{$command}' failed, return value '{$ret_val}, text '{$ret}'");
    }

    // Remove the file
    $ret = unlink($file_name);
    if (!$ret) {
        throw new LocalizeExceptioneption("cannot unlink '{$file_name}'");
    }
}

/**
 * Localize the jars in the "project name/dir name" for "flavor name/sim name"
 *
 * @param string $dir_name directory name the sim is in
 * @param string $flavor_name sim name within the directory
 * @param string $sims_dir location of the 'sims/' directory
 */
function localize_jars($dir_name, $flavor_name, $sims_dir) {
    try {
        // Get the directory the sim is in
        $root_dir = $sims_dir.DIRECTORY_SEPARATOR.$dir_name;

        // Get a temporary directory
        $tmp_dir = create_temp_dir();
        if (!$tmp_dir) {
            throw new LocalizeExceptioneption("cannot create a temporary directory");
        }

        verbose_print("Temporary directory: {$tmp_dir}\n");
        verbose_print("Sims directory: {$sims_dir}\n");
        verbose_print("Project name: {$dir_name}\n");
        verbose_print("Sim name: {$flavor_name}\n");

        // Set the current directory to the temporary directory
        $result = chdir($tmp_dir);
        if (!$result) {
            throw new LocalizeExceptioneption("cannot change to the temporary directory '{$tmp_dir}'");
        }

        // Make sure the base JAR exsits
        //Need to use dir-name_all.jar to avoid flavor-name dir-name collisions.
        //Also, must use dir_name here instead of flavor-name, since that is the jar produced by the deploy process
        $base_jar = $root_dir.DIRECTORY_SEPARATOR.$dir_name.'_all.jar';
        if (!file_exists($base_jar)) {
            throw new LocalizeExceptioneption("cannot find base JAR file '{$base_jar}'");
        }

        // Get a list of all the files in the directory
        $files = scandir($root_dir);
        if (empty($files)) {
            throw new LocalizeExceptioneption("no files found in sim dir {$root_dir}");
        }

        verbose_print("Scanning project '{$dir_name}'...\n");

        // Get all localized JARs
        $localized_jar_pattern = "{$flavor_name}_(..).jar";
        $original_localized_jars = array();
        foreach ($files as $file) {
            $regs = array();
            $match = ereg($localized_jar_pattern, $file, $regs);

            if (is_int($match)) {
                $original_localized_jars[$file] = true;
            }
        }

        // Look for files of the form 'flavor_name_XX.jnlp' where XX is the language code
        $jnlp_pattern = "{$flavor_name}_(..).jnlp$";
        $matches = 0;
        $languages = array('en');
        foreach ($files as $file) {
            $regs = array();
            $match = ereg($jnlp_pattern, $file, $regs);

            if (is_int($match)) {
                // A match has been found, process it
                ++$matches;

                // Add this to the languages to process
                $languages[] = $regs[1];
            }
        }

        if ($matches == 0) {
            verbose_print("   No localized versions found\n");
        }
        else {
            foreach ($languages as $lang_code) {
                // Create the filename for the new jar
                if ($lang_code == 'en') {
                    // Special case for English, no language code for the JAR
                    $localized_jar = "{$flavor_name}.jar";
                }
                else {
                    $localized_jar = "{$flavor_name}_{$lang_code}.jar";
                }

                verbose_print("Processing ".basename($base_jar)." => {$localized_jar}...\n");

                // Copy the original JAR to the new jar
                verbose_print("   copying original JAR file...\n");
                $ret = copy($base_jar, $localized_jar);
                if (!$ret) {
                    throw new LocalizeExceptioneption("cannot copy '{$base_jar}' to '{$localized_jar}' in temp dir '{$tmp_dir}'");
                }

                // Add a locale to the options.properties file
                jar_append_to_textfile($localized_jar, 'options.properties', "locale={$lang_code}\n");

                // Add a sim flavor to the 'main-flavor.properties'
                jar_append_to_textfile($localized_jar, 'main-flavor.properties', "main.flavor={$flavor_name}\n");

                // Put the new localized JAR in the sim directory
                verbose_print("   copying new jar file to simulation directory...\n");
                $ret = copy($localized_jar, $root_dir.DIRECTORY_SEPARATOR.$localized_jar);
                if (!$ret) {
                    throw new LocalizeExceptioneption("cannot copy '{$localized_jar}' to '{$root_dir}' in temp dir '{$tmp_dir}'");
                }

                //
                // Clean up

                // Remove the localized jar
                $ret = unlink($localized_jar);
                if ($ret === false) {
                    throw new LocalizeExceptioneption("cannot deleted modified JAR '{$localized_jar}' in '$tmp_dir'");
                }

                // Remove this localized JAR from being deleted
                unset($original_localized_jars[$localized_jar ]);

                verbose_print("   Success!\n");
            }
        }

        verbose_print("   Removing old localized sims that have no JNLP equivalent\n");
        // Clean up old localized jars
        foreach($original_localized_jars as $original_localized_jar => $ignored) {
            $ret = unlink($root_dir.DIRECTORY_SEPARATOR.$original_localized_jar);
            if ($ret === false) {
                throw new LocalizeExceptioneption("cannot deleted old localived JAR with no JNLP equivalent '{$original_localized_jar}'");
            }
        }

    }
    catch (LocalizeExceptioneption $e) {
        fwrite(STDERR, "Error: ".$e->getMessage()."\n");
        $exit_code = 4;
    }

    // Cleanup hope for and assume success
    $files = scandir($tmp_dir);
    foreach ($files as $file) {
        if ($file == '.' || $file == '..') continue;
        unlink($file);
    }
    rmdir($tmp_dir);
}


//
// Setup the variables that could be altered from the command line

$running_on_tigercat = true;
$verbose = false;


//
// Parse the command line

// Check for the options
$args = $argv;
$program_name = array_shift($args);
while (isset($args[0]) && (substr($args[0], 0, 1) == '-')) {
    switch ($args[0]) {
        case '-v':
        case '--verbose':
            $verbose = true;
            break;

        case '-l':
        case '--local':
            $running_on_tigercat = false;
            break;
    }

    $out = array_shift($args);
    if ($out == '--') {
        break;
    }
}

// Set up some defines based on which machine we're using
if ($running_on_tigercat) {
    define('JAR_EXECUTABLE', '/web/chroot/phet/usr/local/java/bin/jar');
    define('CLEAR_CACHE_URL', 'http://phet.colorado.edu/admin/cache-clear.php?cache=sims');
    // This join(...) thing sets up the dir in a non OS specific way
    define('DEFAULT_SIMS_DIR', DIRECTORY_SEPARATOR.join(DIRECTORY_SEPARATOR, array('web', 'htdocs', 'phet', 'sims')));
}
else {
    // Dano's local Mac dev environment
    define('JAR_EXECUTABLE', '/usr/bin/jar');
    define('CLEAR_CACHE_URL', 'http://localhost/PhET/website/admin/cache-clear.php?cache=sims');
    // This join(...) thing sets up the dir in a non OS specific way
    define('DEFAULT_SIMS_DIR', DIRECTORY_SEPARATOR.join(DIRECTORY_SEPARATOR, array('Users', 'danielmckagan', 'Workspaces', 'PhET', 'sims')));
}

//
// Set up default variables
$sims_dir = DEFAULT_SIMS_DIR;
$sim_list = array();
$exit_code = 0;

// Check to that we have enough arguments
if ((count($args) < 2) || (count($args) > 3)) {
    print <<<EOT
    usage: {$program_name} [options] project_name sim_name [sims_dir]

    For a given project and sim name, refresh the localized jars.

    Languages available are determined from the JNLP files, which
    have the form:
        sim-name_XX.jnlp
    where XX is the language code (e.g. en, es, de).  There should
    be 1 JNLP per language per sim.
    Old JAR files are removed and replaced by new ones.  These new
    JAR files are a modified copy of the base jar, where the
    'option.properties' file is created or appended the line (without
    the precceding spaces):
        locale=XX
    and the file will be named:
        sim-name_XX.jar
    where XX is the language code (e.g. en, es, de).  There should
    be 1 JAR per sim to start with, and 1 per language per sim when
    finished.

    Options:
        -v / --verbose  print extra information
        -l / --local    run locally (not tigercat)
    Arguments:
        project_name    name of the project, ex: quantum-wave-interference
        sim_name        name of the simulation, ex: davisson-germer
        root_sims_dir   [optional] where the sims dir can be found,
                          default: {$sims_dir}

    Special Notes:
    1.  Giving a project_name and sim_name of 'all' will process every
        sim, rather than a specific sim.  This info is taken from the
        database, NOT by scanning the sims directory.  If the sim is
        not in the database it will not be processed.

EOT;
    exit(-1);
}

// Get the arguments
if (($args[0] == 'all') && ($args[1] == 'all')) {
    $sims = sim_get_all_sims();
    foreach ($sims as $sim) {
        if (!$sim['sim_is_real']) continue;

        if ($sim['sim_type'] == SIM_TYPE_JAVA) {
            $sim_list[$sim['sim_dirname']] = $sim['sim_flavorname'];
        }
    }
}
else {
    $sim_list[$args[0]] = $args[1];
}
if (isset($args[2])) {
    $sims_dir = realpath($args[2]);
}

// Localize the sim(s)
// (Note: project name is referred to as dir_name and
// sim name is referred to as flavor_name due to legacy)
foreach($sim_list as $dir_name => $flavor_name) {
    $ret = localize_jars($dir_name, $flavor_name, $sims_dir);
    verbose_print("------------------------------------------------------\n");
}

// Clear the cache
$out = file_get_contents(CLEAR_CACHE_URL);

?>
