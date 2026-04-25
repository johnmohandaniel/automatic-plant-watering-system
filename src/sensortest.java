import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class SoilSensorUnitTest {

    private static final String PORT_NAME = "COM3"; // Update if needed
    private static final int SOIL_SENSOR_PIN = 15;
    private static FirmataDevice arduino;
    private static Pin moistureSensor;

    @BeforeAll
    static void setUp() throws Exception {
        arduino = new FirmataDevice(PORT_NAME);
        arduino.start();
        arduino.ensureInitializationIsDone();

        // Set analog mode
        moistureSensor = arduino.getPin(SOIL_SENSOR_PIN);
        moistureSensor.setMode(Pin.Mode.ANALOG);
        Thread.sleep(500); // sensor takes value every half a second
    }

    @Test
    @DisplayName("Test soil sensor reads a valid range")
    void testMoistureSensorReading() throws Exception {
        int value = (int) moistureSensor.getValue();
        System.out.println("Moisture Reading: " + value);


        assertTrue(value >= 0 && value <= 1023, "Moisture sensor value should be between 0 and 1023");
    }    // Analog values are between 0 and 1023

    @AfterAll
    static void tearDown() throws Exception {
        arduino.stop();
    }
}

