

package com.intuit.tank.nonlinear.simulation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public class RampRateSimulation {

    private double startRampRate = 0;
    private double endRampRate = 10;
    private double d = 10;
    private double k = 1.2;


    private JFreeChart rampRateChart;
    private JFreeChart concurrentUsersChart;

    private double yValueRampRateAtD;

    private double yValueConcurrentUsersAtD;

    private static final double DOT_RADIUS = 0.1;

    private XYSeriesCollection dataset = new XYSeriesCollection();
    private XYSeries series = new XYSeries("Ramp Rate vs Time");


    private XYSeriesCollection concurrentUsersDataset = new XYSeriesCollection();
    private XYSeries concurrentUsersSeries = new XYSeries("Concurrent Users");

    private ChartPanel rampRatePanel;
    private ChartPanel concurrentUsersPanel;

    public RampRateSimulation() {
        JFrame frame = new JFrame("Nonlinear Ramp Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        JTextField startField = new JTextField("0");
        JTextField endField = new JTextField("10");
        JTextField dField = new JTextField("10");
        JTextField kField = new JTextField("1.2");
        JButton updateButton = new JButton("Update Graph");


        inputPanel.add(new JLabel("Start Ramp Rate:"));
        inputPanel.add(startField);
        inputPanel.add(new JLabel("End Ramp Rate:"));
        inputPanel.add(endField);
        inputPanel.add(new JLabel("Ramp Duration:"));
        inputPanel.add(dField);
        inputPanel.add(new JLabel("Steepness (k):"));
        inputPanel.add(kField);
        inputPanel.add(updateButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        dataset.addSeries(series);
        concurrentUsersDataset.addSeries(concurrentUsersSeries);

        rampRateChart = ChartFactory.createXYLineChart(
                "Ramp Rate vs Time",
                "Time (seconds)",
                "Ramp Rate (users/sec)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        concurrentUsersChart = ChartFactory.createXYLineChart(
                "Concurrent Users vs Time",
                "Time (seconds)",
                "Concurrent Users",
                concurrentUsersDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        rampRatePanel = new ChartPanel(rampRateChart);
        concurrentUsersPanel = new ChartPanel(concurrentUsersChart);

        frame.add(rampRatePanel, BorderLayout.CENTER);
        frame.add(concurrentUsersPanel, BorderLayout.SOUTH);


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        startRampRate = Double.parseDouble(startField.getText());
                        endRampRate = Double.parseDouble(endField.getText());
                        d = Double.parseDouble(dField.getText());
                        k = Double.parseDouble(kField.getText());
                        updateGraph();
                        return null;
                    }

                    @Override
                    protected void done() {
                        rampRatePanel.repaint();
                        concurrentUsersPanel.repaint();
                    }
                };
            worker.execute();
            }
        });

        XYPlot rampRatePlot = rampRateChart.getXYPlot();
        XYPlot concurrentUsersPlot = concurrentUsersChart.getXYPlot();

        rampRatePlot.setDomainCrosshairVisible(true);
        rampRatePlot.setRangeCrosshairVisible(true);

        concurrentUsersPlot.setDomainCrosshairVisible(true);
        concurrentUsersPlot.setRangeCrosshairVisible(true);

        rampRatePlot.setDomainCrosshairStroke(new BasicStroke(0.5f));
        rampRatePlot.setRangeCrosshairStroke(new BasicStroke(0.5f));
        rampRatePlot.setDomainCrosshairPaint(Color.BLACK);
        rampRatePlot.setRangeCrosshairPaint(Color.BLACK);

        concurrentUsersPlot.setDomainCrosshairStroke(new BasicStroke(0.5f));
        concurrentUsersPlot.setRangeCrosshairStroke(new BasicStroke(0.5f));
        concurrentUsersPlot.setDomainCrosshairPaint(Color.BLACK);
        concurrentUsersPlot.setRangeCrosshairPaint(Color.BLACK);

        addMouseCrossHair(rampRatePanel, rampRateChart, series);
        addMouseCrossHair(concurrentUsersPanel, concurrentUsersChart, concurrentUsersSeries);

        frame.pack();
        frame.setVisible(true);
    }


    private void addMouseCrossHair(ChartPanel panel, JFreeChart chart, XYSeries series) {
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                if(series.getItemCount() == 0){
                    return;
                }

                Point2D p = panel.translateScreenToJava2D(e.getPoint());
                XYPlot plot = (XYPlot) chart.getPlot();
                ChartRenderingInfo info = panel.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();

                double mouseXValue = plot.getDomainAxis().java2DToValue(p.getX(), dataArea, plot.getDomainAxisEdge());

                double nearestXValue = series.getX(0).doubleValue();
                for(int i = 1; i < series.getItemCount(); i++){
                    double currentX = series.getX(i).doubleValue();
                    if(Math.abs(currentX - mouseXValue) < Math.abs(nearestXValue - mouseXValue)){
                        nearestXValue = currentX;
                    }
                }

                double y = series.getY(series.indexOf(nearestXValue)).doubleValue();

                plot.setDomainCrosshairValue(nearestXValue);
                plot.setRangeCrosshairValue(y);
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainCrosshairVisible(false);
                plot.setRangeCrosshairVisible(false);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainCrosshairVisible(true);
                plot.setRangeCrosshairVisible(true);
            }
        });
    }

    private void updateGraph() {
        series.clear();
        concurrentUsersSeries.clear();
        rampRateChart.getXYPlot().clearAnnotations();
        concurrentUsersChart.getXYPlot().clearAnnotations();
        double steadyStateDuration = d * 1.5;
        for (double t = 0; t <= steadyStateDuration; t += 0.01) {
            series.add(t, nonlinearRampRate(t));
            concurrentUsersSeries.add(t, calculateConcurrentUsers(t));
        }



    }

    private double calculateConcurrentUsers(double t) {
        double sum = 0;
        double dt = 0.01;
        for (double ti = 0; ti <= t; ti += dt) {
            sum += nonlinearRampRate(ti) * dt;
        }
        return sum;
    }

    private double nonlinearRampRate(double t) {
        if (t <= d) {
             return ((endRampRate - startRampRate) * (1 / (1 + Math.exp(-k * (t - d / 2)))) + startRampRate);
        } else {
            return endRampRate;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RampRateSimulation());
    }
}
