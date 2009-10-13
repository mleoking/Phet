<?php

    require_once("file-util.php");

    // *****************************************************************************
    // Global Configuration

    define("OS_WINDOWS", "WINNT");
    define("OS_WIN32",   "WIN32");
    define("OS_AIX",     "AIX");
    define("OS_MAC",     "Darwin");
    define("OS_LINUX",   "Linux");
    define("OS_SUN",     "SunOS");

    define("WEBSITE_PAGES_PATTERN", '*.htm*, *.php');

    define("ROOT_DIR",      file_cleanup_local_filename(dirname(dirname(__FILE__))."/"));
    define("PARENT_DIR",    file_cleanup_local_filename(dirname(ROOT_DIR))."/");
    define("TEMP_DIR",      file_cleanup_local_filename(ROOT_DIR."temp/"));
    define("OUTPUT_DIR",    file_cleanup_local_filename(TEMP_DIR."installer-output/"));
    define("DEPLOY_DIR",    "/web/htdocs/phet/phet-dist/installers/");

    function GET_OS_BOUND_REL_PATH($constantPrefix) {
        return file_cleanup_local_filename(ROOT_DIR."${constantPrefix}/".PHP_OS."/");
    }

    function GET_OS_BOUND_NAME($constantPrefix) {
        return constant("${constantPrefix}_".PHP_OS);
    }

    function CREATE_FILTER_ITEM($controlChar, $hostName, $path){
        return '"'.$controlChar.$hostName."/".$path.'"';
    }

    // *****************************************************************************
    // Locale-specific configuration.
    define("LOCALE_STRING",     "ar");
    
    // *****************************************************************************
    // PhET Website Configuration
    define("PHET_VERSION",                      "1.0");
    define("PHET_HOSTNAME",                     "phetsims.colorado.edu");
    define("PHET_ROOT_URL",                     "http://".PHET_HOSTNAME."/");
    define("PHET_TRANSLATED_WEBSITE_URL",       PHET_ROOT_URL.LOCALE_STRING."/");
    define("PHET_SIMS_SUBDIR",                  "sims/");
    // Definition of the filter, which specifies what to include/exclude from
    // the rip.  This one defines a filter that is used when doing a rip that
    // is meant to capture the entire web site.
    //define("PHET_RIPPER_FILTER",                '"-*wickettest*"'.' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*.jnlp').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*_all.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*_ar.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*_en.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*.jpg').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*.html').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*.swf').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*.png').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'activities/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'publications/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'installer/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*_en.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/*/*_ar.jar'));
    define("PHET_RIPPER_FILTER",                '"-*wickettest*"' );
    // Filter definition for a "lite" rip, meaning one that rips less than
    // the full web site.  This is generally swapped in for the full rip
    // filters when doing testing that requires a lot of iterations, since 
    // this will generally be much quicker than a full rip.
    define("PHET_LITE_RIPPER_FILTER",            '"-*wickettest*"'.' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/faraday/*.jnlp').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/faraday/*_all.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/faraday/*.jpg').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/arithmetic/*.html').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/arithmetic/*.swf').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/arithmetic/*.png').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/arithmetic/*.jpg').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'sims/arithmetic/*_en.jar').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'activities/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'publications/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'installer/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'workshops/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'css/*').' '.CREATE_FILTER_ITEM('+', PHET_HOSTNAME, 'images/*'));

    define("PHET_WEBSITE_ROOT_PARTIAL_PATTERN", '[^"]+colorado\.edu');
    define("PHET_WEBSITE_ROOT_PATTERN",         '/'.PHET_WEBSITE_ROOT_PARTIAL_PATTERN.'/');

    // *****************************************************************************
    // CD-ROM Configuration
    define("AUTORUN_FILENAME",                     'autorun.inf');
    define("AUTORUN_ICON_NAME",                 'phet-icon.ico');
    define("AUTORUN_ICON_SRC",                     file_cleanup_local_filename(PARENT_DIR."Installer-Resources/Install-Path/".AUTORUN_ICON_NAME));
    define("AUTORUN_ICON_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_ICON_NAME));
    define("AUTORUN_FILE_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_FILENAME));

    define("CDROM_FILE_NAME",                   "PhET-Installer_cdrom.zip");
    define("CDROM_FILE_DEST",                   OUTPUT_DIR.CDROM_FILE_NAME);

    // *****************************************************************************
    // Website Ripper Configuration

    define("RIPPED_WEBSITE_ROOT", file_cleanup_local_filename(TEMP_DIR."website/"));
    define("RIPPED_WEBSITE_SIMS_PARENT_DIR",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT.PHET_HOSTNAME.'/'));
    define("RIPPED_WEBSITE_INSTALLER_DIR",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT.PHET_HOSTNAME.'/installer/'));
    define("RIPPED_TRANSLATED_WEBSITE_ROOT",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT.PHET_HOSTNAME.'/'.LOCALE_STRING.'/'));

    // The ripper executable itself:

    // The ripper executables per OS:
    define("RIPPER_EXE_Linux",   "httrack");
    define("RIPPER_EXE_WINNT",   "httrack.exe");
    define("RIPPER_EXE_Darwin",  "httrack");

    // The location of the httrack executable.
    define("RIPPER_DIR",  file_cleanup_local_filename("/usr/local/httrack/bin/"));
    define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE"));

    // User agent to indicate when ripping.  This is used to make the web site
    // react somewhat differently (generally filtering out some links) when it
    // is being ripped for the installers.
    define("RIPPER_USER_AGENT",  '"httrack-web-mirror-ar"');

    // Command-line args of the ripper:
    define("RIPPER_ARGS", '"'.PHET_ROOT_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER." -F ".RIPPER_USER_AGENT.' -j %q0 -%e0 -r10');

    // Command-line args for a quicker, lighter, less complete rip of the web
    // site.  This exists primarily for testing purposes.
    define("RIPPER_ARGS_SUBSET", '"'.PHET_TRANSLATED_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_LITE_RIPPER_FILTER." -F ".RIPPER_USER_AGENT.' -j %q0 -%e0 -r10');

    // File used for preventing simultaneous builds.
    define("LOCK_FILE_STEM_NAME", "installer-builder");

    // *****************************************************************************
    // BitRock Configuration

    define("BITROCK_PLATFORM_EXEC_SUFFIX_OSX",         ".app");
    define("BITROCK_PLATFORM_EXEC_SUFFIX_WINDOWS",     ".exe");
    define("BITROCK_PLATFORM_EXEC_SUFFIX_LINUX",     ".bin");

    define("BITROCK_PRODUCT_SHORTNAME", "PhET");
    define("BITROCK_PLATFORM_OSX",      "osx");
    define("BITROCK_PLATFORM_WINDOWS",  "windows");
    define("BITROCK_PLATFORM_LINUX",    "linux");

    define("BITROCK_PRODUCT_VERSION",   PHET_VERSION);
    define("BITROCK_CODEBASE_MACRO",  '@@CODEBASE@@');

    // KSU Installer Note: Some of the bitrock files - such as the executable
    // itself - are maintained in the main installer directory, and some of the
    // KSU-specific files are maintained in a separate location.  Hence, some
    // of the contants below point to one place and some point to another.
    define("BITROCK_DIR",               file_cleanup_local_filename(PARENT_DIR."BitRock/"));
    define("BITROCK_KSU_DIR",           file_cleanup_local_filename(ROOT_DIR."BitRock/"));
    define("BITROCK_BUILDFILE_DIR",     file_cleanup_local_filename(BITROCK_KSU_DIR."projects/"));
    // Project file for building the "web mirror installer", meaning the
    // installer that allows for installation on a server that can then serve
    // the mirror's contents to the web.
    define("BITROCK_WEB_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."ksu-web-mirror-installer-buildfile.xml"));
    define("BITROCK_LOCAL_MIRROR_BUILDFILE", file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."ksu-local-mirror-installer-buildfile.xml"));
    define("BITROCK_EXE_DIR",           file_cleanup_local_filename(BITROCK_DIR));
    define("BITROCK_EXE_Linux",         "bitrock.sh");
    define("BITROCK_EXE_WINNT",         "bitrock.bat");
    define("BITROCK_EXE_Darwin",        "bitrock.sh");
    define("BITROCK_EXE",               GET_OS_BOUND_NAME("BITROCK_EXE"));

    define("BITROCK_DIST_DIR",          file_cleanup_local_filename(BITROCK_KSU_DIR."output/"));

    define("BITROCK_DIST_PREFIX",       BITROCK_PRODUCT_SHORTNAME."-Installer_");

    // File name of the KSU web mirror installer.  Decided to avoid using all
    // the little bits and pieces of names and just put the whole thing here.
    // Hopefully, this is easier to maintain.
    define("BITROCK_WEB_MIRROR_INSTALLER_NAME",  "PhET-Installer-ksu_windows.exe");

    define("BITROCK_DISTNAME_WINNT",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_WINDOWS.BITROCK_PLATFORM_EXEC_SUFFIX_WINDOWS);
    define("BITROCK_DISTNAME_Linux",      BITROCK_DIST_PREFIX.BITROCK_PLATFORM_LINUX.BITROCK_PLATFORM_EXEC_SUFFIX_LINUX);
    define("BITROCK_DISTNAME_Darwin",     BITROCK_DIST_PREFIX.BITROCK_PLATFORM_OSX.BITROCK_PLATFORM_EXEC_SUFFIX_OSX);

    define("BITROCK_DIST_SRC_WINNT",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_SRC_Linux",      file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_SRC_Darwin",     file_cleanup_local_filename(BITROCK_DIST_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_DIST_DEST_WINNT",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_WINNT));
    define("BITROCK_DIST_DEST_Linux",      file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Linux));
    define("BITROCK_DIST_DEST_Darwin",     file_cleanup_local_filename(OUTPUT_DIR.BITROCK_DISTNAME_Darwin));

    define("BITROCK_PRE_ARGS",            " build ");

    define("BITROCK_OUTPUT_DIR",          file_cleanup_local_filename(ROOT_DIR."BitRock/output/"));

    $g_bitrock_dists = array(
        BITROCK_PLATFORM_WINDOWS => BITROCK_DIST_SRC_WINNT,
        BITROCK_PLATFORM_LINUX      => BITROCK_DIST_SRC_Linux,
        BITROCK_PLATFORM_OSX      => BITROCK_DIST_SRC_Darwin
    );

    // *****************************************************************************
    // Installation Configuration

    define("DISTRIBUTION_TAG", "ksu-custom-distribution");
    define("MARKER_FILE_NAME", "phet-installation.properties");
    define("CREATION_TIMESTAMP_FILE_NAME", "installer-creation-timestamp.txt");
    define("VERSION_INFO_FILE_NAME", "version.html");
    define("LINUX_INSTALLER_FILE_NAME", "PhET-Installer_linux.bin");
    define("WINDOWS_INSTALLER_FILE_NAME", "PhET-Installer_windows.exe");
    define("OSX_INSTALLER_FILE_NAME", "PhET-Installer_osx.zip");
    define("CD_ROM_INSTALLER_FILE_NAME", "PhET-Installer_cdrom.zip");

    // *****************************************************************************
    // JAR file signing config information.
    define("CONFIG_FILE", "signing-config.ini");
?>
