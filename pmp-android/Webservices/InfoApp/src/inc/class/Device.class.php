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

namespace infoapp;

use InvalidArgumentException;
use infoapp\eventmanager\AwakeEventManager;
use infoapp\eventmanager\BatteryEventManager;
use infoapp\eventmanager\CellularConnectionEventManager;
use infoapp\eventmanager\ConnectionEventManager;
use infoapp\eventmanager\ScreenEventManager;
use infoapp\eventmanager\ProfileEventManager;
use infoapp\properties\BatteryProperties;
use infoapp\properties\ConnectionProperties;
use infoapp\properties\DeviceProperties;
use infoapp\properties\ProfileProperties;

if (!defined("INCLUDE")) {
    exit;
}

/**
 * This class handles the access to all classes used to manage the stored information
 *
 * @author Patrick Strobel
 * @version 4.5.0
 * @package infoapp
 */
class Device {

    private $deviceId;

    /** @var Device */
    private static $instance = null;

    private $awakeMgr = null;
    private $batteryMgr = null;
    private $cellularConnectionMgr = null;
    private $connectionMgr = null;
    private $screenMgr = null;
    private $profileMgr = null;

    private $batteryProp = null;
    private $connectionProp = null;
    private $deviceProp = null;
    private $profileProp = null;

    private function __construct($deviceId) {
        $this->deviceId = $deviceId;
    }

    /**
     * Returns the singelton-instance of this class
     * @param String $deviceId The device's ID thats represented by this object
     * @return Device The instance
     * @throws InvalidArgumentException Thrown, if this method has already created
     *              an instance having another device ID than given as argument or
     *              if the given ID is no valid MD5-Hash
     */
    public static function getInstance($deviceId) {

        if (!General::isValidDeviceId($deviceId)) {
            throw new InvalidArgumentException("The given device ID is no valid MD5-Hash");
        }

        if (self::$instance == null) {
            self::$instance = new Device($deviceId);
        }

        if (self::$instance->deviceId != $deviceId) {
            throw new InvalidArgumentException("The given device ID does not match the ID of the stored instance");
        }
        return self::$instance;
    }

    public static function eventsExists($deviceId) {
        if (!General::isValidDeviceId($deviceId)) {
            return false;
        }
        $db = Database::getInstance();

        $db->query("SELECT * FROM " . DB_PREFIX . "_last_event_ids WHERE `device` = x'" . $deviceId . "'");

        return $db->getAffectedRows() > 0;
    }

    /**
     * Gets the manager for awake events
     * @return AwakeEventManager The manager
     */
    public function getAwakeEventManager() {
        if ($this->awakeMgr == null) {
            $this->awakeMgr = new AwakeEventManager($this->deviceId);
        }
        return $this->awakeMgr;
    }

    /**
     * Gets the manager for battery events
     * @return BatteryEventManager The manager
     */
    public function getBatteryEventManager() {
        if ($this->batteryMgr == null) {
            $this->batteryMgr = new BatteryEventManager($this->deviceId);
        }
        return $this->batteryMgr;
    }

    /**
     * Gets the manager for cellular connection events
     * @return ConnectionEventManager The manager
     */
    public function getCellularConnectionEventManager() {
        if ($this->cellularConnectionMgr == null) {
            $this->cellularConnectionMgr = new CellularConnectionEventManager($this->deviceId);
        }
        return $this->cellularConnectionMgr;
    }

    /**
     * Gets the manager for bluetooth and wifi connection events
     * @return ConnectionEventManager The manager
     */
    public function getConnectionEventManager() {
        if ($this->connectionMgr == null) {
            $this->connectionMgr = new ConnectionEventManager($this->deviceId);
        }
        return $this->connectionMgr;
    }

    /**
     * Gets the manager for screen events
     * @return ScreenEventManager The manager
     */
    public function getScreenEventManager() {
        if ($this->screenMgr == null) {
            $this->screenMgr = new ScreenEventManager($this->deviceId);
        }
        return $this->screenMgr;
    }

    /**
     * Gets the manager for profile events
     * @return ProfileEventManager The manager
     */
    public function getProfileEventManager() {
        if ($this->profileMgr == null) {
            $this->profileMgr = new ProfileEventManager($this->deviceId);
        }
        return $this->profileMgr;
    }

    /**
     * Gets the connection property manager
     * @return BatteryProperties The manager
     */
    public function getBatteryProperties() {
        if ($this->batteryProp == null) {
            $this->batteryProp = BatteryProperties::load($this->deviceId);
        }
        return $this->batteryProp;
    }

    /**
     * Gets the connection property manager
     * @return ConnectionProperties The manager
     */
    public function getConnectionProperties() {
        if ($this->connectionProp == null) {
            $this->connectionProp = ConnectionProperties::load($this->deviceId);
        }
        return $this->connectionProp;
    }

    /**
     * Gets the device property manager
     * @return DeviceProperties The manager
     */
    public function getDeviceProperties() {
        if ($this->deviceProp == null) {
            $this->deviceProp = DeviceProperties::load($this->deviceId);
        }
        return $this->deviceProp;
    }

    /**
     * Gets the profile property manager
     * @return ProfileProperties The manager
     */
    public function getProfileProperties() {
        if ($this->profileProp == null) {
            $this->profileProp = ProfileProperties::load($this->deviceId);
        }
        return $this->profileProp;
    }


}

?>
