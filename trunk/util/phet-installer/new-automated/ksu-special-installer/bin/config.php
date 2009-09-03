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

    // *****************************************************************************
    // PhET Website Configuration
    define("PHET_VERSION",                      "1.0");
    define("PHET_ROOT_URL",                     "http://phet-server.colorado.edu:8080/zh_CN/");
    define("PHET_WEBSITE_URL",                  PHET_ROOT_URL);
    define("PHET_SIMS_SUBDIR",                  "sims/");
    // Definition of the filter, which specifies what to exclude from the rip.
    //define("PHET_RIPPER_FILTER",                '"-*wickettest*"');
    // The definition below for the PHET_RIPPER_FILTER can be used to create a
    // very small rip of the website that excludes sims and a lot of other
    // things.  This is useful for testing, since it rips much more quickly
    // that the "real" rip.  To use it, uncomment it, and comment out any
    // other definitions of the same constant.
    define("PHET_RIPPER_FILTER",                '"-*/get-phet/*" "-*/workshops/*" "-*/simulations/*" "-*wickettest*"');
    define("PHET_WEBSITE_ROOT_PARTIAL_PATTERN", '[^"]+colorado\.edu');
    define("PHET_WEBSITE_ROOT_PATTERN",         '/'.PHET_WEBSITE_ROOT_PARTIAL_PATTERN.'/');

    // *****************************************************************************
    // CD-ROM Configuration
    define("AUTORUN_FILENAME",                     'autorun.inf');
    define("AUTORUN_ICON_NAME",                 'phet-icon.ico');
    define("AUTORUN_ICON_SRC",                     file_cleanup_local_filename(ROOT_DIR."Installer-Resources/Install-Path/".AUTORUN_ICON_NAME));
    define("AUTORUN_ICON_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_ICON_NAME));
    define("AUTORUN_FILE_DEST",                 file_cleanup_local_filename(OUTPUT_DIR.AUTORUN_FILENAME));

    define("CDROM_FILE_NAME",                   "PhET-Installer_cdrom.zip");
    define("CDROM_FILE_DEST",                   OUTPUT_DIR.CDROM_FILE_NAME);

    // *****************************************************************************
    // Website Ripper Configuration

    // The name of the HTTrack directory:
    define("RIPPER_DIR_NAME",    "HTTrack");

    define("RIPPED_WEBSITE_ROOT", file_cleanup_local_filename(TEMP_DIR."website/"));
    define("RIPPED_WEBSITE_TOP",  file_cleanup_local_filename(RIPPED_WEBSITE_ROOT."phet-server.colorado.edu_8080/"));

    // The ripper executable itself:

    // The ripper executables per OS:
    define("RIPPER_EXE_Linux",   "httrack");
    define("RIPPER_EXE_WINNT",   "httrack.exe");
    define("RIPPER_EXE_Darwin",  "httrack");

    define("RIPPER_DIR",  file_cleanup_local_filename(PARENT_DIR.RIPPER_DIR_NAME."/".PHP_OS."/"));
    define("RIPPER_EXE",  RIPPER_DIR.GET_OS_BOUND_NAME("RIPPER_EXE"));

    // Command-line args of the ripper:
    define("RIPPER_ARGS", '"'.PHET_WEBSITE_URL.'" -O "'.RIPPED_WEBSITE_ROOT.'" '.PHET_RIPPER_FILTER.' -j %q0 -%e0');

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
    define("BITROCK_INSTALLDIR_MACRO",  '@@INSTALLDIR@@');

    // KSU Installer Note: Some of the bitrock files - such as the executable
    // itself - is maintained in the main installer directory, and some of the
    // KSU-specific files are maintained in a separate location.  Hence, some
    // of the contants below point to one place and some point to another.
    define("BITROCK_DIR",               file_cleanup_local_filename(PARENT_DIR."BitRock/"));
    define("BITROCK_KSU_DIR",           file_cleanup_local_filename(ROOT_DIR."BitRock/"));
    define("BITROCK_BUILDFILE_DIR",     file_cleanup_local_filename(BITROCK_KSU_DIR."projects/"));
    define("BITROCK_BUILDFILE",         file_cleanup_local_filename(BITROCK_BUILDFILE_DIR."phet-ksu-installer-buildfile.xml"));
    define("BITROCK_EXE_DIR",           file_cleanup_local_filename(BITROCK_DIR));
    define("BITROCK_EXE_Linux",         "bitrock.sh");
    define("BITROCK_EXE_WINNT",         "bitrock.bat");
    define("BITROCK_EXE_Darwin",        "bitrock.sh");
    define("BITROCK_EXE",               GET_OS_BOUND_NAME("BITROCK_EXE"));

    define("BITROCK_DIST_DIR",          file_cleanup_local_filename(BITROCK_KSU_DIR."output/"));

    define("BITROCK_DIST_PREFIX",       BITROCK_PRODUCT_SHORTNAME."-Installer_");

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

    define("DEPLOYMENT_TAG", "WEB_SITE");
    define("MARKER_FILE_NAME", "phet-installation.properties");
    define("CREATION_TIMESTAMP_FILE_NAME", "installer-creation-timestamp.txt");
    define("VERSION_INFO_FILE_NAME", "version.html");
    define("LINUX_INSTALLER_FILE_NAME", "PhET-Installer_linux.bin");
    define("WINDOWS_INSTALLER_FILE_NAME", "PhET-Installer_windows.exe");
    define("OSX_INSTALLER_FILE_NAME", "PhET-Installer_osx.zip");
    define("CD_ROM_INSTALLER_FILE_NAME", "PhET-Installer_cdrom.zip");
?>
