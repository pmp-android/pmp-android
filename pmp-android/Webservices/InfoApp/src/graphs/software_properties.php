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

use infoapp\Database;
use infoapp\googlecharttools\Cell;
use infoapp\googlecharttools\Column;
use infoapp\googlecharttools\DataTable;
use infoapp\googlecharttools\GChartPhpBridge;
use infoapp\googlecharttools\Row;
use infoapp\properties\DeviceProperties;

define("INCLUDE", true);
require("./../inc/graphs_framework.inc.php");

$stat = DeviceProperties::getStatistic();
$countColumn = new Column("number", "c", "Count");

// Manufacturer distribution
$manufacturerData = new DataTable();
$manufacturerData->addColumn(new Column("string", "m", "Manufacturer"));
$manufacturerData->addColumn($countColumn);

foreach ($stat->getManufacturerDist() as $man => $count) {
    $row = new Row();
    $row->addCell(new Cell($man));
    $row->addCell(new Cell($count));
    $manufacturerData->addRow($row);
}

// Model and UI distribution
$selectedManufacturer = "Not selected";
$modelData = new DataTable();
$modelData->addColumn(new Column("string", "m", "Model"));
$modelData->addColumn($countColumn);

$uiData = new DataTable();
$uiData->addColumn(new Column("string", "u", "UI"));
$uiData->addColumn($countColumn);

if (isset($_GET["manufacturer"])) {
    $selectedManufacturer = $_GET["manufacturer"];
    $modelDist = $stat->getModelDist();
    $uiDist = $stat->getUiDist();

    if (key_exists($selectedManufacturer, $modelDist)) {
        $rawData = $modelDist[$_GET["manufacturer"]];

        foreach ($rawData as $model => $count) {
            $row = new Row();
            $row->addCell(new Cell($model));
            $row->addCell(new Cell($count));
            $modelData->addRow($row);
        }
    }

    if (key_exists($selectedManufacturer, $uiDist)) {
        $rawData = $uiDist[$_GET["manufacturer"]];

        foreach ($rawData as $ui => $count) {
            $row = new Row();
            $row->addCell(new Cell($ui));
            $row->addCell(new Cell($count));
            $uiData->addRow($row);
        }
    }
}

// API distribution
$apiData = new DataTable();
$apiData->addColumn(new Column("string", "a", "API"));
$apiData->addColumn($countColumn);

foreach ($stat->getApiDist() as $api => $count) {
    $row = new Row();
    $row->addCell(new Cell($api));
    $row->addCell(new Cell($count));
    $apiData->addRow($row);
}

// Kernel distribution
$kernelData = new DataTable();
$kernelData->addColumn(new Column("string", "k", "Kernel"));
$kernelData->addColumn($countColumn);

foreach ($stat->getKernelDist() as $kernel => $count) {
    $row = new Row();
    $row->addCell(new Cell($kernel));
    $row->addCell(new Cell($count));
    $kernelData->addRow($row);
}




$tmplt["pageTitle"] = "Software";
$tmplt["jsFunctDrawChart"] = "drawManufacturer();
        drawModel();
        drawUi();
        drawApi();
        drawKernel();";

if ($svgCharts) {
    // Draw SVG-Charts
    // ---------------
    $tmplt["jsDrawFunctions"] = "
    var manufacturerChart;
    var manufacturerData;
    function drawManufacturer() {
        manufacturerData = new google.visualization.DataTable(" . $manufacturerData->getJsonObject() . ");


        var options = {
            title: 'Manufacturer distribution (Click to select)',
            'width':800,
            'height':400
        };


        manufacturerChart = new google.visualization.PieChart(document.getElementById('manufacturer'));
        google.visualization.events.addListener(manufacturerChart, 'select', selectHandler);
        manufacturerChart.draw(manufacturerData, options);
    }
    function selectHandler(e) {
        if (manufacturerChart.getSelection().length >= 1) {
            var item = manufacturerChart.getSelection()[0];
            var manufacturer = manufacturerData.getFormattedValue(item.row, 0);
            window.location.href=document.URL + '&manufacturer=' + manufacturer;
        }
    }

    function drawModel() {
        var data = new google.visualization.DataTable(" . $modelData->getJsonObject() . ");


        var options = {
            title: 'Model distribution (Manufacturer: " . $selectedManufacturer . ")',
            'width':800,
            'height':400
        };


        var chart = new google.visualization.PieChart(document.getElementById('model'));
        chart.draw(data, options);
    }

    function drawUi() {
        var data = new google.visualization.DataTable(" . $uiData->getJsonObject() . ");


        var options = {
            title: 'UI distribution (Manufacturer: " . $selectedManufacturer . ")',
            'width':800,
            'height':400
        };


        var chart = new google.visualization.PieChart(document.getElementById('ui'));
        chart.draw(data, options);
    }

    function drawApi() {
        var data = new google.visualization.DataTable(" . $apiData->getJsonObject() . ");


        var options = {
            title: 'API distribution',
            'width':800,
            'height':400
        };


        var chart = new google.visualization.PieChart(document.getElementById('api'));
        chart.draw(data, options);
    }

    function drawKernel() {
        var data = new google.visualization.DataTable(" . $kernelData->getJsonObject() . ");


        var options = {
            title: 'Kernel distribution',
            'width':800,
            'height':400
        };


        var chart = new google.visualization.PieChart(document.getElementById('kernel'));
        chart.draw(data, options);
    }";
}

$tmplt["content"] = "
            <h1>Software Statistics</h1>";
if ($svgCharts) {
    // Draw SVG-Charts
    // ---------------
    $tmplt["content"] .= "
            <p>Select a manufacturer to view the model- and UI-chart.</p>
            <div id=\"manufacturer\"></div>
            <div id=\"model\"></div>
            <div id=\"ui\"></div>
            <div id=\"api\"></div>
            <div id=\"kernel\"></div>";
} else {
    // Draw static/PNG-charts
    // ----------------------

    $manufacturerChart = new gchart\gPieChart($chart->getPieChartWidth() - 100, $chart->getPieChartHeight() - 100);
    $manufacturerChart->setTitle("Manufacturer distribution");
    $bridge = new GChartPhpBridge($manufacturerData);
    $bridge->pushData($manufacturerChart, GChartPhpBridge::LEGEND);

    $modelChart = new gchart\gPieChart($chart->getPieChartWidth() - 100, $chart->getPieChartHeight() - 100);
    $modelChart->setTitle("Model distribution (Manufacturer: " . $selectedManufacturer . ")");
    $bridge = new GChartPhpBridge($modelData);
    $bridge->pushData($modelChart, GChartPhpBridge::LEGEND);

    $uiChart = new gchart\gPieChart($chart->getPieChartWidth() - 100, $chart->getPieChartHeight() - 100);
    $uiChart->setTitle("UI distribution (Manufacturer: " . $selectedManufacturer . ")");
    $bridge = new GChartPhpBridge($uiData);
    $bridge->pushData($uiChart, GChartPhpBridge::LEGEND);

    $apiChart = new gchart\gPieChart($chart->getPieChartWidth() - 100, $chart->getPieChartHeight() - 100);
    $apiChart->setTitle("API distribution");
    $bridge = new GChartPhpBridge($apiData);
    $bridge->pushData($apiChart, GChartPhpBridge::LEGEND);

    $kernelChart = new gchart\gPieChart($chart->getPieChartWidth() - 100, $chart->getPieChartHeight() - 100);
    $kernelChart->setTitle("Kernel distribution");
    $bridge = new GChartPhpBridge($kernelData);
    $bridge->pushData($kernelChart, GChartPhpBridge::LEGEND);

    // Build a string that contains a link to manufacturer specific charts
    $currentUrl = $_SERVER["PHP_SELF"] . "?" . $tmplt["dateGetParams"] . "&" .
            $tmplt["annotationGetParam"] . "&" . $tmplt["deviceGetParam"] . "&" .
            $tmplt["viewGetParam"];
    $manufacturerLinks = "";
    $first = true;
    foreach ($stat->getManufacturerDist() as $man => $value) {
        if ($first) {
            $first = false;
        } else {
            $manufacturerLinks .= ", ";
        }
        $manufacturerLinks .= "<a href=\"" . $currentUrl . "&manufacturer=" . $man . "\">" . $man . "</a>";
    }

    $tmplt["content"] .= "
            <p>Select a manufacturer to view the model- and UI-chart: " . $manufacturerLinks . "</p>
            <p><img src=\"" . $manufacturerChart->getUrl() . "\" /></p>
            <p><img src=\"" . $modelChart->getUrl() . "\" /></p>
            <p><img src=\"" . $uiChart->getUrl() . "\" /></p>
            <p><img src=\"" . $apiChart->getUrl() . "\" /></p>
            <p><img src=\"" . $kernelChart->getUrl() . "\" /></p>";
}

include ("template.php");

Database::getInstance()->disconnect();
?>

