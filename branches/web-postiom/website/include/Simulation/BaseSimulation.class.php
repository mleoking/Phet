<?php

require_once('include/installer-utils.php');

abstract class BaseSimulation implements SimulationInterface {
    const sim_root = SIMS_ROOT;
    const thumbnail_cache = 'thumbnails';
    const thumbnail_cache_lifespan = 24;
    const thumbnail_width = 130;
    const thumbnail_height = 97;

    // Sim data
    private $id;
    private $name;
    private $sorting_name;
    private $description;
    protected $project_name;
    protected $sim_name;
    private $guidance_recommended;
    private $rating;
    private $keywords;
    private $teachers_guide_id;
    private $main_topics;
    private $learning_goals;
    private $design_team;
    private $libraries;
    private $thanks_to;
    
    // Data from the database, used during the refactor
    private $original_data;
    private $db_data;

    // Map the database field names to local variable names
    private static $map = array(
        'id' => array('sim_id', 'int'),
        'name' => array('sim_name', 'string'),
        'project_name' => array('sim_dirname', 'string'),
        'sim_name' => array('sim_flavorname', 'string'),
        'sorting_name' => array('sim_sorting_name', 'tolower_string'),
        'guidance_recommended' => array('sim_crutch', 'bool'),
        'rating' => array('sim_rating', 'int'),
        'description' => array('sim_desc', 'string'),
        'keywords' => array('sim_keywords', 'delimited-list'),
        'teachers_guide_id' => array('sim_teachers_guide_id', 'int'),
        'main_topics' => array('sim_main_topics', 'delimited-list'),
        'learning_goals' => array('sim_sample_goals', 'delimited-list'),
        'design_team' => array('sim_design_team', 'delimited-list'),
        'libraries' => array('sim_libraries', 'delimited-list'),
        'thanks_to' => array('sim_thanks_to', 'delimited-list'),
        );

    public function __construct($db_data) {
        $this->original_data = $db_data;
        foreach (self::$map as $property => $db_field) {
            $type = $db_field[1];
            $raw_data = $db_data[$db_field[0]];
            // Convert the data to the specified type
            switch ($type) {
                case 'bool':
                    $data = (0 != intval($raw_data));
                    break;
                case 'int':
                    $data = intval($raw_data);
                    break;
                case 'string':
                    $data = $raw_data;
                    break;
                case 'tolower_string':
                    // TODO: data should come out of database already lowercased
                    $data = strtolower($raw_data);
                    break;
                case 'delimited-list':
                    // TODO: fix data in database so the trim is not neede
                    // This will involve fixing existing data, and updating
                    // admin/edit-sim.php to do this trimming
                    $trimmed_data = rtrim($raw_data, '*, ');
                    if (strstr($trimmed_data, '*')) {
                        $data = preg_split('/ *\\* */', $trimmed_data, -1, PREG_SPLIT_NO_EMPTY);
                    }
                    else {
                        $data = preg_split('/ *, */', $trimmed_data, -1, PREG_SPLIT_NO_EMPTY);
                    }
                    if ($property == 'libraries') {
                        //var_dump($raw_data, $trimmed_data, $data);
                    }
                    break;
                default:
                    throw new RuntimeException("Type '{$type}' is not supported");
            }

            $this->$property = $data;

            // Unset the data and later check for unused data
            unset($db_data[$db_field[0]]);
        }

        // Save what is left for easy checking for unused data
        $this->db_data = $db_data;
    }

    public function getId() {
        return $this->id;
    }

    public function getName() {
        return $this->name;
    }

    public function getSortingName() {
        return $this->sorting_name;
    }

    public function getSortingFirstChar() {
        return strtoupper($this->sorting_name[0]);
    }

    public function getDescription() {
        return $this->description;
    }

    public function getGuidanceRecommended() {
        return $this->guidance_recommended;
    }

    public function getRating() {
        return $this->rating;
    }

    public function getKeywords() {
        return $this->keywords;
    }

    public function hasTeachersGuide() {
        return ($this->teachers_guide_id != 0);
    }

    public function getTeachersGuideUrl() {
        if (!$this->hasTeachersGuide()) {
            return '';
        }

        return SITE_ROOT.'admin/get-teachers-guide.php?teachers_guide_id='.$this->teachers_guide_id;
    }

    public function getMainTopics() {
        return $this->main_topics;
    }

    public function getLearningGoals() {
        return $this->learning_goals;
    }

    public function getDesignTeam() {
        return $this->design_team;
    }

    public function getLibraries() {
        return $this->libraries;
    }

    public function getThanksTo() {
        return $this->thanks_to;
    }

    public function getScreenshotUrl() {
        $basename = "{$this->project_name}/{$this->sim_name}-screenshot.png";
        return self::sim_root.$basename;
    }

    public function getAnimatedScreenshotUrl() {
        return self::sim_root."{$this->project_name}/{$this->sim_name}-animated-screenshot.gif";
    }

    public function getThumbnailUrl() {
        // TODO: too much in 1 function, refactor

        $image_url = $this->getScreenshotUrl();
        $file_name_hash = md5($image_url).'.jpg';
        if (cache_has_valid_page(self::thumbnail_cache, $file_name_hash, self::thumbnail_cache_lifespan)) {
            return cache_get_file_location(self::thumbnail_cache, $file_name_hash);
        }

        // Load image -- assume image is in png format (for now):
        @$img = imagecreatefrompng($image_url);
        if ($img == false) {
            return;
        }
        
        $existing_width  = imagesx($img);
        $existing_height = imagesy($img);
        
        // Scale to thumbnail width, preserving aspect ratio:
        $new_width  = self::thumbnail_width;
        $new_height = self::thumbnail_height;
        //old method is proportional: floor($existing_height * ( thumbnail_width / $existing_width ));

        $tmp_img = imagecreatetruecolor($new_width, $new_height);
        
        imagecopyresampled($tmp_img, $img, 0, 0, 0, 0, $new_width, $new_height, $existing_width, $existing_height);

        $temp_file = tempnam('/tmp', 'sim-thumbnail');

        // Output image to cached image location:
        imagejpeg($tmp_img, $temp_file);

        cache_put(self::thumbnail_cache, $file_name_hash, file_get_contents($temp_file));

        unlink($temp_file);
        
        return cache_get_file_location(self::thumbnail_cache, $file_name_hash);
    }

    public function getPageUrl() {
        return SITE_ROOT.'simulations/sims.php?sim='.WebUtils::inst()->encodeString($this->getName());
    }

    public function getDownloadUrl($requested_locale = Locale::DEFAULT_LOCALE) {
        $locale = (Locale::inst()->isValid($requested_locale)) ? $requested_locale : Locale::DEFAULT_LOCALE;

        $file = self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jar";

        if (!file_exists($file)) {
            return '';
        }

        return SITE_ROOT."admin/get-run-offline.php?sim_id={$this->getId()}&locale={$locale}";
    }

    // I'd rather declare these explicitly as abstract, but PHP thinks
    // about these differently.  If you receive a error "Cannot
    // instantiate abstract class BaseSimulation in ...file..."  It is
    // probably because not all interface functions have been defined
    abstract protected function getTranslationGlob();

    public function getTranslations() {
        $translations = array();
        $sim_glob = $this->getTranslationGlob();
        $base_glob = $sim_glob[0];
        $base_regex = $sim_glob[1];

        $files = glob($base_glob);
        foreach ($files as $file) {
            $regs = array();
            $result = ereg($base_regex, $file, $regs);
            if ($result !== false) {
                $locale = "{$regs[2]}{$regs[3]}";
            }
            else {
                // Skip the default locale, it is not a translation
                continue;
            }

            $localeUtils = Locale::inst();
            if (!$localeUtils->isValid($locale)) {
                // Locale is not in the table, log error and skip
                // TODO: log an error
                continue;
            }
            else if ($localeUtils->isDefault($locale)) {
                // Skip the default locale, it is not a translation
                continue;
            }

            if (!isset($translations[$locale])) {
                $translations[$locale] = 1;
            }
            else {
                $translations[$locale] += 1;
            }
        }

        $end = array();
        foreach ($translations as $key => $value) {
            $end[] = $key;
        }

        $translations = $end;
        usort($translations, array(Locale::inst(), 'sortCodeByNameCmp'));
        return $translations;
    }

    public function getVersion() {
        $properties_filename = self::sim_root."{$this->project_name}/{$this->project_name}.properties";

        $revision_tags = array(
            'major', 'minor', 'dev', 'revision',
            'timestamp', 'installer_timestamp');
        $regex = 'version\.('.join('|', $revision_tags).') *= *([^ \n\r\t]+)';

        $version = array();
        foreach ($revision_tags as $tag) {
            $version[$tag] = '';
        }

        $installer_timestamp = installer_get_latest_timestamp();
        if ($installer_timestamp && !empty($installer_timestamp)) {
            $version['installer_timestamp'] = installer_get_latest_timestamp();
        }

        if (!file_exists($properties_filename)) {
            return $version;
        }

        $handle = @fopen($properties_filename, "r");

        if ($handle) {
            while (!feof($handle)) {
                $buffer = fgets($handle, 4096);
                $regs = array();
                if (ereg($regex, $buffer, $regs)) {
                    if ($regs[1] && in_array($regs[1], $revision_tags)) {
                        $version[$regs[1]] = trim($regs[2]);
                    }
                }
            }
            fclose($handle);
        }

        return $version;
    }
}

?>