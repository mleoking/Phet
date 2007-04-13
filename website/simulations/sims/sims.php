<?php
    ini_set('display_errors', '1');

    include_once("../../admin/db.inc");
    include_once("../../admin/web-utils.php");
    include_once("../../admin/sim-utils.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Phet :: Physics Education Technology at CU Boulder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link rel="Shortcut Icon" type="image/x-icon" href="favicon.ico" />
<style type="text/css">
/*<![CDATA[*/
        @import url(../../css/main.css);
        @import url(../../css/test.css);
/*]]>*/
</style>
<script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--

function SymError()
{
  return true;
}

window.onerror = SymError;

var SymRealWinOpen = window.open;

function SymWinOpen(url, name, attributes)
{
  return (new Object());
}

window.open = SymWinOpen;

//-->
//]]>
</script>
<script type="text/javascript" src="../../js/drop_down.js">
</script>
<script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--

function SymError()
{
  return true;
}

window.onerror = SymError;

var SymRealWinOpen = window.open;

function SymWinOpen(url, name, attributes)
{
  return (new Object());
}

window.open = SymWinOpen;

//-->
//]]>
</script>
</head>

<body id="top">
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <a href="http://phet.colorado.edu/web-pages/index.html"><img src="../../images/phet-logo.gif" alt="" title="" /><span class="logo-title"><img src="../../images/logo-title.jpg" alt="" title="" /></span></a>

            <div class="clear"></div>

            <div class="mainNav">
                <ul>
                    <li><a href="../../index.html" accesskey="1">Home</a></li>

                    <li class="selected"><a href="../../simulations/index.php" accesskey="2">Simulations</a></li>

                    <li><a href="../../research/index.html" accesskey="3">Research</a></li>

                    <li><a href="../../about/index.html" accesskey="4">About PhET</a></li>
                </ul>
            </div>
        </div>
    </div>

    <div id="container">
        <div id="localNav">
            <ul>
                <li><a href="../../index.html" accesskey="1">Home</a></li>

                <li class=" selected"><a href="../index.php">Simulations</a></li>

                <?php
                    print_sim_categories("../");
                ?>

                <li><a href="../../teacher_ideas/index.html">Teacher Ideas Database</a></li>

                <li><a href="../../get_phet/index.html">Get PhET</a></li>

                <li><a href="../../tech_support/index.html">Technical Support</a></li>

                <li><a href="../../research/index.html">Research</a></li>

                <li><a href="../../contribute/index.html">Contribute</a></li>

                <li><a href="../../about/index.html">About PhET</a></li>
            </ul>

            <h4><br />
            Principle Sponsors</h4>

            <dl>
                <dt><a href="http://www.hewlett.org/Default.htm">The William and Flora Hewlett Foundation</a></dt>

                <dd><a href="http://www.hewlett.org/Default.htm"><img src="../../images/hewlett-logo.jpg" alt="The Hewlett Logo"/></a><br />
                <br />
                Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time.</dd>

                <dt><a href="http://www.nsf.gov/"><img class="sponsors" src="../../images/nsf-logo.gif" alt="The NSF Logo"/>National Science Foundation</a></dt>

                <dd><br />
                An independent federal agency created by Congress in 1950 to promote the progress of science.<br />
                <br />
                <a href="../sponsors/index.html"><img src="../../images/other-sponsors.gif" alt="Other Sponsors Logo"/></a></dd>
            </dl>
        </div>

        <div id="content">
            <!--<p><a class="breadcrumbs" href="../../index.html">Home</a><a class="breadcrumbs"  href="../../simulations/index.php">Main Simulations</a><a class="breadcrumbs"  href="../../simulations/index.php">Top Sims</a></p>-->


            <?php
                $simid          = $_REQUEST['simid'];
                $sql_sim        = "SELECT * FROM `simtest` WHERE `simid`= '$simid' ";
                $sql_result_sim = mysql_query($sql_sim);
    
                while ($row2 = mysql_fetch_row($sql_result_sim)) {
                    $sim_id         = $row2[0];
                    $sim_name       = format_for_html($row2[1]);
                    $rating         = $row2[2];
                    $type           = $row2[3];
                    $simsize        = $row2[4];
                    $url_sim        = $row2[5];
                    $url_thumb      = $row2[6];
                    $desc           = format_for_html($row2[7]);
                    $keywords       = $row2[8];
                    $systemreq      = $row2[9];
                    $teachingideas  = $row2[10];
                    $usertips       = $row2[11];
                    $learninggoals  = $row2[12];
                }
            ?>

            <div class="productListHeader">
                <?php
                    print "<h1>$sim_name</h1>"
                ?>
            </div>

            <p>
                <?php
                    print "<a href=\"$url_sim\">";
                    print "<img class=\"sim-large\" src=\"$url_thumb\"/>";
                    print "</a>";
                    print "$desc";                    
                ?>
            </p>

            <table id="indi-sim" cellspacing="0" summary="">
                <tr>
                    <th scope="row" abbr="" class="spec-sim">
                        <?php
                            if ($rating == "0") {
                                $simrating_image = "beta-minus-rating.gif";
                            }
                            else if ($rating == "2") {
                                $simrating_image = "beta-plus-rating.gif";
                            }
                            else if ($rating == "1") {
                                $simrating_image = "beta-rating.gif";
                            }
                            else if ($rating == "3") {
                                $simrating_image = "star-rating.gif";
                            }
                            else if ($rating == "4") {
                                $simrating_image = "alpha-rating.gif";
                            }

                            if ($type == "0") {
                                $simtype_image = "java.png";
                            }
                            else if ($type == "1") {
                                $simtype_image = "flash.png";
                            }
                            
                            $simrating = "<img src=\"../../images/sims/ratings/$simrating_image\" width=\"16\" height=\"16\" />";
                            $simtype   = "<img src=\"../../images/sims/ratings/$simtype_image\"   width=\"32\" height=\"16\" />";
                            
                            print "Rating: $simrating Type: $simtype";
                            // <img class="rating" src="../../images/check_Icon.gif" />
                        ?>
                    </th>
                </tr>
            </table><br />
            <span class="size">
                <?php
                    print "???kb";
                ?>
            </span><br />

            <p class="indisim">
                <a class="d-arrow" href="#topics"><span class="burg">Topics</span></a> 
                
                <a class="d-arrow" href="#ideas"><span class="burg">Teaching Ideas</span></a> 
                
                <a class="d-arrow" href="#software"><span class="burg">Software Requirements</span></a> 
                
                <a class="d-arrow" href="#versions"><span class="burg">Translated Versions</span></a> 
                
                <a class="d-arrow" href="#credits"><span class="burg">Credits</span></a></p><br />
            <br />

            <h1 class="indi-sim" id="topics">Simulation Information</h1>

            <h2 class="sub-title">User Tips</h2>

            <p class="indi-sim">
                <?php
                    print "$usertips";
                ?>
            </p>

            <h2 class="sub-title">Sample Learning Goals</h2>

            <p class="indi-sim">
                <?php
                    print "$learninggoals";
                ?>
            </p>

            <p><a href="#top"><img src="../../images/top.gif" /></a></p>

            <h1 class="indi-sim" id="ideas">Teaching Ideas</h1>

            <h2 class="sub-title">Ideas and Activites for this Sim</h2>

            <p class="indi-sim">
                <?php
                    print "<a href=\"$teachingideas\">Click here to see Ideas and Activities for this Simulation (PDF file).</a>";
                ?>
            </p>

            <p class="indi-sim">&nbsp;</p>

            <form>
                <textarea id="textareainput" name="textareainput" class="textarea">
                </textarea><br />
                <br />
                <input type="submit" value="Submit" class="buttonSubmit" />
            </form>

            <p><a href="#top"><img src="../../images/top.gif" /></a></p>

            <h1 class="indi-sim" id="software">Software Requirements</h1>

            <h2 class="sub-title">Software Requirements</h2>

            <p class="indi-sim">
                <b>Windows Systems</b><br/>
                Microsoft Windows 98SE/2000/XP<br/>
                
                <?php
                    if ($type == '0') { 
                        print "Sun Java 1.4.2_10 or later<br/>";
                    }
                    else if ($type == '1') {
                        print "Macromedia Flash 7 or later<br/>";
                    }
                ?>
                
                <br/><b>Macintosh Systems</b><br/>
                OS 10.3.9 or later<br/>
                
                
                <?php
                    if ($type == '0') {
                        print "Apple Java 1.4.2_09 or later<br/>";
                    }
                    else if ($type == '1') {
                        print "Macromedia Flash 7 or later<br/>";
                    }
                ?>
                
                <br/><b>Linux Systems</b><br/>
                
                <?php
                    if ($type == '0') {
                        print "Sun Java 1.4.2_10 or later<br/>";
                    }
                    else if ($type == '1') {
                        print "Macromedia Flash 7 or later<br/>";
                    }
                ?>
            </p>

            <p><a href="#top"><img src="../../images/top.gif" /></a></p>

            <h1 class="indi-sim" id="versions">Translated Versions</h1>

            <h2 class="sub-title">Translated versions</h2>

            <p class="indi-sim">Quisque sagittis commodo velit. Nunc porttitor sagittis tortor. Mauris metus. Maecenas eu nisi id elit tincidunt malesuada.</p>

            <p><a href="#top"><img src="../../images/top.gif" /></a></p>

            <h1 class="indi-sim" id="credits">Credits</h1>

            <h2 class="sub-title">Credits</h2>

            <p class="indi-sim">Quisque sagittis commodo velit. Nunc porttitor sagittis tortor. Mauris metus. Maecenas eu nisi id elit tincidunt malesuada.</p>

            <p><a href="#top"><img src="../../images/top.gif" /></a></p>

            <p class="footer">© 2007 PhET. All rights reserved.<br />
            
        </div>
    </div>

    <p><script language="JavaScript" type="text/javascript">
//<![CDATA[
<!--
var SymRealOnLoad;
var SymRealOnUnload;

function SymOnUnload()
{
  window.open = SymWinOpen;
  if(SymRealOnUnload != null)
     SymRealOnUnload();
}

function SymOnLoad()
{
  if(SymRealOnLoad != null)
     SymRealOnLoad();
  window.open = SymRealWinOpen;
  SymRealOnUnload = window.onunload;
  window.onunload = SymOnUnload;
}

SymRealOnLoad = window.onload;
window.onload = SymOnLoad;

//-->
//]]>
</script></p>
</body>
</html>
