import org.jfree.chart.*;
import org.jfree.data.time.*;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.I2CDevice;

import javax.swing.*;
import java.util.*;
// all imports

public class PlantWatering {
    private static final String PORT_NAME = "COM3";
    private static final int SOIL_SENSOR_PIN = 15;           // pin A0
    private static final int PUMP_RELAY_PIN = 2;               // pin D2
    private static final int LED_PIN = 4;
    private static final int STOP_BUTTON_PIN = 7;
    private static ArrayList<Integer> moistureData = new ArrayList<>(); // Stores sensor readings
    private static boolean running = true;
// All pins declared as final variables

    public static void main(String[] args) {
        try {
            FirmataDevice arduino = new FirmataDevice(PORT_NAME);
            arduino.start();
            arduino.ensureInitializationIsDone();
            // initialization of arduino

            arduino.getPin(PUMP_RELAY_PIN).setMode(Pin.Mode.OUTPUT);
            arduino.getPin(LED_PIN).setMode(Pin.Mode.OUTPUT);
            arduino.getPin(STOP_BUTTON_PIN).setMode(Pin.Mode.INPUT);

            I2CDevice i2cObject = arduino.getI2CDevice((byte) 0x3C);
            SSD1306 oled = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
            oled.init();


            //Setup the graph
            TimeSeries series = new TimeSeries("Soil Moisture");
            TimeSeriesCollection dataset = new TimeSeriesCollection(series);
            JFreeChart chart = ChartFactory.createTimeSeriesChart("Soil Moisture vs Time", "Time", "Moisture Level", dataset, true, true, false);
            ChartPanel panel = new ChartPanel(chart);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);

            while (true) {

                int stopButtonState = (int) arduino.getPin(STOP_BUTTON_PIN).getValue();
                if (stopButtonState == 1) { // STOP button is pressed
                    running = false;
                    break;
                }

                Pin moistureSensor = arduino.getPin(SOIL_SENSOR_PIN);
                moistureSensor.setMode(Pin.Mode.ANALOG);
                int moistureValue = (int) moistureSensor.getValue();
                moistureData.add(moistureValue); // Store data for analysis
                System.out.println("Soil Moisture: " + moistureValue);


                // Control water pump based on moisture level
                boolean pumpOn = false;
                if (moistureValue > 600) {
                    arduino.getPin(PUMP_RELAY_PIN).setValue(1);
                    arduino.getPin(LED_PIN).setValue(1);
                    pumpOn = true;
                    System.out.println("Soil is dry!!! Pump ONNNNN.");

                } else if (moistureValue > 570 && moistureValue < 600) {
                    arduino.getPin(PUMP_RELAY_PIN).setValue(1);
                    arduino.getPin(LED_PIN).setValue(1);
                    pumpOn = true;
                    System.out.println("Soil is damp! Pump ONNNN");

                } else {
                    arduino.getPin(PUMP_RELAY_PIN).setValue(0);
                    arduino.getPin(LED_PIN).setValue(0);
                    pumpOn = false;
                    System.out.println("Soil is wet. Pump OFFFFFFF");

                }
                // Update OLED display
                oled.getCanvas().clear();
                oled.getCanvas().setTextsize(2);
                oled.getCanvas().setCursor(0, 0);
                oled.getCanvas().write("Moisture: " + moistureValue);
                oled.getCanvas().setCursor(0, 20);
                oled.getCanvas().write("Pump: " + (pumpOn ? "ON" : "OFF"));
                oled.display();

                // Update the graph
                series.addOrUpdate(new Second(), moistureValue);

                Thread.sleep(2000);
            }
            System.out.println("Clearing OLED DISPLAY");
            oled.getCanvas().clear();
            oled.display(); // Turn off OLED display
            arduino.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}