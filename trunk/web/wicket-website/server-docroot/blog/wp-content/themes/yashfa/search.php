<?php get_header( ); ?>

<div id="content-wrap">
    <div id="content">
        <div class="gap">

        <?php if ( have_posts( ) ) : ?>

            <h2 class="pagetitle"><?php _e( 'Search Results', 'yashfa' )?></h2>

            <div class="navigation">
                <div class="alignleft"><?php next_posts_link( __( '&laquo; Older Entries', 'yashfa' ) ) ?></div>
                <div class="alignright"><?php previous_posts_link( __( 'Newer Entries &raquo;', 'yashfa' ) ) ?></div>
            </div>


        <?php while ( have_posts( ) ) : the_post( ); ?>

            <div class="post" id="post-<?php the_ID( ); ?>">

                <h2><a href="<?php the_permalink( ) ?>" rel="bookmark"
                       title="Permanent Link to <?php the_title_attribute( ); ?>"><?php the_title( ); ?></a></h2>

                <div class="posties clearfix">
                    <div class="one">

                        <div class="entry">
                        <?php the_content( __( 'Read the rest of this entry &rarr;', 'yashfa' ) ); ?>
                        </div>

                        <div class="tagged">
                        <?php the_tags( __( 'Tags: ', 'yashfa' ), ', ', '<br />' ); ?>
                        </div>

                        <div class="social-sub-div">
                            <a href="http://www.facebook.com/sharer.php?u=<?php echo urlencode( get_permalink( ) ); ?>&amp;t=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Facebook">

                                <img src="/files/icons/social/16/facebook.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="https://twitter.com/share?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;text=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Twitter">
                                <img src="/files/icons/social/16/twitter.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://www.stumbleupon.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Stumble Upon">
                                <img src="/files/icons/social/16/stumbleupon.png"
                                     alt="Social Media Icon" style="border: none;"/>

                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://digg.com/submit?phase=2&amp;url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Digg">
                                <img src="/files/icons/social/16/digg.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>
                        <div class="social-sub-div">
                            <a href="http://reddit.com/submit?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Reddit">
                                <img src="/files/icons/social/16/reddit.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>

                        </div>
                        <div class="social-sub-div">
                            <a href="http://del.icio.us/post?url=<?php echo urlencode( get_permalink( ) ); ?>&amp;title=<?php echo urlencode( get_the_title( ) ); ?>"
                               style="padding: 0; margin: 0; text-decoration: none;" target="_blank"
                               title="Share this on Delicious">
                                <img src="/files/icons/social/16/delicious.png" alt="Social Media Icon"
                                     style="border: none;"/>
                            </a>
                        </div>

                        <div class="new-comments-area">
                        <?php comments_popup_link( __( '0 Comments', 'yashfa' ), __( '1 Comment', 'yashfa' ), __( '% Comments', 'yashfa' ) ); ?>
                            &nbsp;|&nbsp;
                        <?php comments_popup_link( __( 'Add a Comment', 'yashfa' ), __( 'Add a Comment', 'yashfa' ), __( 'Add a Comment', 'yashfa' ) ); ?>
                        </div>

                    </div>
                    <div class="two">
                        <div class="dater">
                            <h3 style="font-size: 150%;"><?php the_time( 'D d' ) ?></h3>
                            <h5><?php the_time( 'M Y' ) ?></h5>
                        </div>
                        <p class="metadata"><br/><?php _e( 'by', 'yashfa' ); ?> <?php  the_author( ) ?>
                            <br/><?php _e( 'posted in<br/>', 'yashfa' ); ?> <?php  the_category( ', ' ) ?> <?php edit_post_link( __( 'Edit', 'yashfa' ), '', '' ); ?>
<!--                            <br/>--><?php //comments_popup_link( __( '0 Comments', 'yashfa' ), __( '1 Comment', 'yashfa' ), __( '% Comments', 'yashfa' ) ); ?>
                        </p>
                    </div>
                </div>

            </div>
        <?php endwhile; ?>

            <div class="navigation">
                <div class="alignleft"><?php next_posts_link( __( '&laquo; Older Entries', 'yashfa' ) ) ?></div>
                <div class="alignright"><?php previous_posts_link( __( 'Newer Entries &raquo;', 'yashfa' ) ) ?></div>
            </div>

        <?php else : ?>

            <h2 class="center"><?php _e( 'No posts found. Try a different search?', 'yashfa' )?></h2>
        <?php include ( TEMPLATEPATH . '/searchform.php' ); ?>

        <?php endif; ?>

        </div>
    </div>
</div>
<?php get_sidebar( ); ?>

<?php get_footer( ); ?>
