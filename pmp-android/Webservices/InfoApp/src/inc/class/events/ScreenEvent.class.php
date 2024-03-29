<?php

/*
 * Copyright 2012 pmp-android development team
 * Project: InfoApp-Webservice
 * Project-Site: http://code.google.com/p/pmp-android/
 *
 * ---------------------------------------------------------------------
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace infoapp\events;

use InvalidArgumentException;
use infoapp\Database;
use infoapp\General;

if (!defined("INCLUDE")) {
    exit;
}

/**
 * A screen event stores information about the state of the device's display
 * at a given timestamp
 *
 * @author Patrick Strobel
 * @version 4.0.0
 * @package infoapp
 * @subpackage events
 */

class ScreenEvent extends Event {

    /** @var boolean */
    private $display;

    /**
     * Creates a new screen event
     * @param int $id           The event's ID
     * @param long $timestamp   Point in time when this event occurred
     * @param boolean $display  Indicates if the device's display is turned on (true) or turned off (false)
     * @throws InvalidArgumentException
     */
    public function __construct($id, $timestamp, $display) {

        parent::__construct($id, $timestamp);

        if (!is_bool($display)) {
            throw new InvalidArgumentException("\"display\" is no boolean");
        }

        $this->display = $display;
    }

    /**
     * Get the device's display status
     * @return boolean  True, if display is on
     */
    public function isDisplayOn() {
        return $this->display;
    }
}
?>
