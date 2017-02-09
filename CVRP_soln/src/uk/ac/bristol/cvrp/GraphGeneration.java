package uk.ac.bristol.cvrp;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/** 
* @author  Frank Chen
* capechy@hotmail.com
*/
public class GraphGeneration extends ApplicationFrame {

	private static final long serialVersionUID = 6884518135387671537L;

	public GraphGeneration(String title, List<Double> stepsBestLength) {
		super(title);
		final XYDataset dataset = createDataset(stepsBestLength);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(900, 500));
        setContentPane(chartPanel);
		
	}

	public XYDataset createDataset(List<Double> stepsBestLength) {
        
        final XYSeries bestLengths = new XYSeries("bestLengths");
        
        for(int i = 0; i < stepsBestLength.size(); i++) {
        	bestLengths.add(i + 1, stepsBestLength.get(i));
        }


        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(bestLengths);
                
        return dataset;
        
    }
	
	 private JFreeChart createChart(final XYDataset dataset) {
	        
	        // create the chart...
	        final JFreeChart chart = ChartFactory.createXYLineChart(
	            "Best tour length",      // chart title
	            "X",                      // x axis label
	            "Y",                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );

	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
	        chart.setBackgroundPaint(Color.white);

	        // get a reference to the plot for further customisation...
	        final XYPlot plot = chart.getXYPlot();
	        plot.setBackgroundPaint(Color.lightGray);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        
	        final XYSplineRenderer renderer = new XYSplineRenderer();
	        renderer.setSeriesLinesVisible(1, true);
	        renderer.setSeriesShapesVisible(1, false);
	        plot.setRenderer(renderer);

	        // change the auto tick unit selection to integer units only...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        // OPTIONAL CUSTOMISATION COMPLETED.
	                
	        return chart;
	        
	    }
}
 