import org.firmata4j.*;
import org.firmata4j.firmata.*;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PumpTest {
    private static final String PORT_NAME = "COM3";
    private static final int PUMP_RELAY_PIN = 2;
    private static FirmataDevice arduino;

    @BeforeAll
    static void setup() throws Exception {
        arduino = new FirmataDevice(PORT_NAME);
        arduino.start();
        arduino.ensureInitializationIsDone();
        arduino.getPin(PUMP_RELAY_PIN).setMode(Pin.Mode.OUTPUT);
    }

    @Test
    @DisplayName("Is Pump On?")
    void testPumpOn() throws Exception {
        arduino.getPin(PUMP_RELAY_PIN).setValue(1);
        assertEquals(1, arduino.getPin(PUMP_RELAY_PIN).getValue(), "Pump should be ON");
    }

    @Test
    @DisplayName("Is Pump Off?")
    void testPumpOff() throws Exception {
        arduino.getPin(PUMP_RELAY_PIN).setValue(0);
        assertEquals(0, arduino.getPin(PUMP_RELAY_PIN).getValue(), "Pump should be OFF");
    }

    @AfterAll
    static void teardown() throws Exception {
        arduino.stop();
    }
}
