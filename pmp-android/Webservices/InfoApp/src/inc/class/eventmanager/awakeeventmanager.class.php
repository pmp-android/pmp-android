<?php

/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
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

if (!defined("INCLUDE")) {
    exit;
}

/**
 * Gives access to awake events
 * @author Patrick Strobel
 * @version 1.0.0
 */
class AwakeEventManager extends EventManager {

    protected function isEventTypeValid($event) {
        return $event instanceof AwakeEvent;
    }

    /**
     *
     * @param AwakeEvent[] $events
     */
    protected function writeBack($events) {
        $db = Database::getInstance();

        // Add events
        $lastId = 0;
        foreach ($events as $event) {
            $db->query("INSERT INTO `" . DB_PREFIX . "_awake` (
                            `device`,
                            `event_id`,
                            `timestamp`,
                            `awake`
                        ) VALUES (
                            x'" . $this->deviceId . "',
                            " . $event->getId() . ",
                            " . $event->getTimestamp() . ",
                            " . (int) $event->isAwake() . "
                        )");
            $lastId = $event->getId();
        }

        $this->updateOrInsertLastIdEntry("awake", $lastId);
    }

    public function getLastId() {
        return $this->queryLastId("awake");
    }

}

?>
