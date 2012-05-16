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

class GChartPhpBridge {

    const LEGEND = 0;
    const Y_COORDS = 1;

    private $data;

    public function __construct(GDataTable $data) {
        $this->data = $data;
    }

    /**
     * Pushs the data stored in the GDataTable into a gGraph.
     * @param gchart\gChart $gChart Graph in which the data should be pushed
     * @param enum $firstColumnRole The data in the first column of the GDataTable
     *                  is used for different purposes, depending on the chart-type it
     *                  is used for (in Pie-Chart, for example, the first column
     *                  represents the secments' label, whereas in line-charts,
     *                  this column is used for the y-axis. If this parameter
     *                  is set to <code>LEGEND</CODE>, the data in the first column
     *                  will be "pushed" into the legend (through <code>$gChart->setLegend</code>),
     *                  if set to <code>Y_COORDS</code>, it will be "pushed" into
     *                  a data row (through <code>$gChart->addDataSet()</code>
     *
     * @param int $scale If <code>$firstColumnRole</code> is set to <code>Y_COORDS</code>,
     *                  this parameter will be used to define the rightes value
     *                  that's drawn on the y-axis. That is, the values
     *                  on the y-axis will run from 0 to $scale
     * @return \gchart\gChart
     */
    public function pushData(gchart\gChart $gChart, $firstColumnRole, $scale = 24) {
        if ($this->data->getRowsCount() == 0) {
            return;
        }

        $columns = $this->data->getColumns();
        $legend = array();
        // Data is stored in a 2D-Array where the first key is the column and
        // the second key is the row
        $data2D = array();

        foreach ($this->data->getRows() as $rowNum => $row) {
            // Use a seperate counter as some columns will be ignored
            // (and there would be empty columns in the array if we would use $rawCellNum)
            $cellNum = 0;
            foreach ($row->getCells() as $rawCellNum => $cell) {
                // Ignore cells that have a role/property (like annotations) since we cannot
                // represent them in static graphs
                if ($columns[$cellNum]->getProperty() == null) {
                    $data2D[$cellNum][$rowNum] = $cell->getValue();
                    $legend[$cellNum] = $columns[$rawCellNum]->getLabel();
                    $cellNum++;
                }
            }
        }


        switch ($firstColumnRole) {
            case self::LEGEND:
                $gChart->addDataSet($data2D[1]);
                $gChart->setLegend($data2D[0]);
                break;
            case self::Y_COORDS:
                // Normalize x-axis (first column) so that all x-values will go from 0 to 100
                $xMin = $data2D[0][0];
                $xMax = $data2D[0][count($data2D[0]) - 1];
                $xDelta = $xMax - $xMin;
                $xScaleFactor = $xDelta / 100;

                // Happens if, for example, only one row exists
                if ($xScaleFactor == 0) {
                    $xScaleFactor = 1;
                }

                for ($i = 0; $i < count($data2D[0]); $i++) {
                    $data2D[0][$i] = (int) (($data2D[0][$i] - $xMin) / $xScaleFactor);
                }

                // Normalize y-axis (first column) so that all y-values will go from 0 to 100
                $yMin = 100;
                $yMax = -100;

                for ($i = 1; $i < count($data2D); $i++) {
                    $yMin = min(min($data2D[$i]), $yMin);
                    $yMax = max(max($data2D[$i]), $yMax);
                }
                $yDelta = $yMax - $yMin;
                $yScaleFactor = $yDelta / 100;

                // Happens if, for example, only one row exists
                if ($yScaleFactor == 0) {
                    $yScaleFactor = 1;
                }

                // Add some empty space between lowest point and x-axis
                //$yMin -= 10 * $yScaleFactor;

                // Write data into chart
                for ($i = 1; $i < count($data2D); $i++) {
                    $gChart->addDataSet($data2D[0]);

                    foreach ($data2D[$i] as $key => $value) {
                        $data2D[$i][$key] = (int) (($value - $yMin) / $yScaleFactor);
                    }
                    $gChart->addDataSet($data2D[$i]);
                }


                // Set axis range and legend
                //$gChart->addAxisRange(0, 0, $scale);
                //$gChart->addAxisRange(1, $yMin,$yMax);
                $gChart->setLegend(array_slice($legend, 1));
                break;
        }
        return $gChart;
    }

}

?>
