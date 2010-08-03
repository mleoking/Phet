package edu.colorado.phet.common.motion.charts;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Layouts for MinimizableControlCharts
 *
 * @author Sam Reid
 */
public interface ControlChartLayout {
    void updateLayout(MinimizableControlChart[] charts, double width, double height);

    /**
     * This layout ensures that different components in the chart line up vertically,
     * and that all of the vertical space is used.
     */
    public class AlignedLayout implements ControlChartLayout {
        public interface DoubleGetter {
            Double getValue(MinimizableControlChart chart);
        }

        public double getSum(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = getValues(charts, doubleGetter);
            double sum = 0.0;
            for (Double value : values) {
                sum = sum + value;
            }
            return sum;
        }

        private ArrayList<Double> getValues(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = new ArrayList<Double>();
            for (MinimizableControlChart chart : charts) {
                values.add(doubleGetter.getValue(chart));
            }
            return values;
        }

        public double getMax(MinimizableControlChart[] charts, DoubleGetter doubleGetter) {
            ArrayList<Double> values = getValues(charts, doubleGetter);
            Collections.sort(values);
            return values.get(values.size() - 1);
        }

        public void updateLayout(MinimizableControlChart[] charts, double width, double height) {
            double maxControlPanelWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getControlPanel().getFullBounds().getWidth();
                }
            });
            double maxSliderWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getSliderNode().getFullBounds().getWidth();
                }
            });
            double maxZoomControlWidth = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getZoomButtonNode().getFullBounds().getWidth();
                }
            });
            double extraLayoutHeightForDomainAxisLabels = getMax(charts, new DoubleGetter() {
                public Double getValue(MinimizableControlChart chart) {
                    return chart.getDomainLabelHeight();
                }
            });
            double maxRangeAxisLabelWidth = getMax( charts, new DoubleGetter() {//TODO: need to observe when this changes and update layout
                public Double getValue( MinimizableControlChart chart ) {
                    return chart.getMaxRangeAxisLabelWidth();
                }
            } );
//            double chartRangeLabelWidth = 20;//TODO: don't hard code this.
            double chartWidth = width - maxControlPanelWidth - maxSliderWidth - maxZoomControlWidth - maxSliderWidth / 2.0 - maxRangeAxisLabelWidth;

            //Figure out how many charts are visible and how much space the minimized charts will use.
            int numVisibleCharts = 0;
            double minimizedChartSpace = 0;
            for (MinimizableControlChart chart : charts) {
                if (chart.getMaximized().getValue()) {
                    numVisibleCharts++;
                } else {
                    minimizedChartSpace += chart.getMinimizedHeight();
                }
            }

            //Determine the X coordinates of the different components
            final double controlPanelX = 0.0;
            final double sliderX = maxControlPanelWidth + controlPanelX + maxSliderWidth / 2.0;
            final double chartX = sliderX + maxSliderWidth + maxRangeAxisLabelWidth-10;//This value accounts for the fact that the range axis labels are not exactly right justified against the chart//TODO: don't hard code this
            final double zoomControlX = chartX + chartWidth;

            //Compute the vertical location and spacing
            final int paddingBetweenCharts = 8;
            final int totalPaddingBetweenCharts = paddingBetweenCharts * (charts.length - 1);
            final double maximizedChartHeight = numVisibleCharts == 0 ? 0 : 
                    (height  - minimizedChartSpace - totalPaddingBetweenCharts) / numVisibleCharts - extraLayoutHeightForDomainAxisLabels;//TODO: account for insets
            double chartY = 0.0;
            
            //Update the chart sizes and locations
            for (MinimizableControlChart chart : charts) {
                chart.setLayoutLocations(controlPanelX, sliderX, chartX, zoomControlX);
                chart.getChartNode().getViewDimension().setDimension(chartWidth, maximizedChartHeight);
                chart.setOffset(0, chartY);
                chart.setMinimizeMaximizeButtonOffset(chartX + chartWidth - chart.getMinimizeMaximizeButton().getFullBounds().getWidth() - 4, 0);

                //identify the location of the next chart
                double currentChartHeight = (chart.getMaximized().getValue() ? maximizedChartHeight + chart.getDomainLabelHeight() : chart.getMinimizedHeight());
                chartY += paddingBetweenCharts + currentChartHeight;
            }
        }
    }
}
