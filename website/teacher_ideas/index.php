<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Teacher Ideas &amp; Activities</h1>
            
            <p>
                Welcome to the Teacher Ideas &amp; Activities page. This page is your gateway to teacher-submitted contributions, designed to be used in conjunction with the <a href="../simulations/index.php">PhET simulations</a>.
            </p>
            
            <h2><a href="../teacher_ideas/browse.php">Browse</a></h2>
            
            <p>
                Start by <a href="browse.php">browsing</a> existing contributions. These contributions include homework assignments, lectures, activities, concept questions and more, and enable you to get the most out of your PhET experience. If you already have a simulation in mind, head over to the <a href="../simulations/index.php">PhET simulations</a> to browse the contributions designed for that simulation.
            </p>
            
            <h2><a href="../teacher_ideas/contribute.php">Contribute</a></h2>
            
            <p>
                 If you have developed some material you'd like to share with others, please consider <a href="contribute.php">contributing it to PhET</a>. You can read our guidelines for contributions by <a href="contribution-guidelines.doc">clicking here</a>.
            </p>
        <?php
    }

    print_site_page('print_content', 3);

?>