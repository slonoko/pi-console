package io.codextension.pi.component;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import io.codextension.pi.model.Dht;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * Created by elie on 26.02.2017.
 */
@Component
@Scope("singleton")
public class DhtReader {
    private static final int MAXTIMINGS = 85;
    private static final int PIN_NB = 18;
    private static long lastCallTimestamp = 0;
    private int[] dht11_dat = {0, 0, 0, 0, 0};
    private Dht previousValue;

    @PostConstruct
    public void ini() {
        GpioUtil.export(PIN_NB, GpioUtil.DIRECTION_OUT);
    }

    @PreDestroy
    public void destroy() {
        GpioUtil.unexport(PIN_NB);
    }

    public synchronized Dht getValue() {
        if (lastCallTimestamp != 0 && (new Date().getTime() - lastCallTimestamp <= 2000)) {
            return previousValue;
        }

        lastCallTimestamp = new Date().getTime();

        int laststate = Gpio.HIGH;
        int j = 0;
        dht11_dat[0] = dht11_dat[1] = dht11_dat[2] = dht11_dat[3] = dht11_dat[4] = 0;

        Gpio.pinMode(PIN_NB, Gpio.OUTPUT);
        Gpio.digitalWrite(PIN_NB, Gpio.LOW);
        Gpio.delay(18);

        Gpio.digitalWrite(PIN_NB, Gpio.HIGH);
        Gpio.pinMode(PIN_NB, Gpio.INPUT);

        for (int i = 0; i < MAXTIMINGS; i++) {
            int counter = 0;
            while (Gpio.digitalRead(PIN_NB) == laststate) {
                counter++;
                Gpio.delayMicroseconds(1);
                if (counter == 255) {
                    break;
                }
            }

            laststate = Gpio.digitalRead(PIN_NB);

            if (counter == 255) {
                break;
            }

      /* ignore first 3 transitions */
            if ((i >= 4) && (i % 2 == 0)) {
         /* shove each bit into the storage bytes */
                dht11_dat[j / 8] <<= 1;
                if (counter > 16) {
                    dht11_dat[j / 8] |= 1;
                }
                j++;
            }
        }


        // check we read 40 bits (8bit x 5 ) + verify checksum in the last
        // byte
        if ((j >= 40) && checkParity()) {
            float h = (float) ((dht11_dat[0] << 8) + dht11_dat[1]) / 10;
            if (h > 100) {
                h = dht11_dat[0];   // for DHT11
            }
            float c = (float) (((dht11_dat[2] & 0x7F) << 8) + dht11_dat[3]) / 10;
            if (c > 125) {
                c = dht11_dat[2];   // for DHT11
            }
            if ((dht11_dat[2] & 0x80) != 0) {
                c = -c;
            }
            previousValue = new Dht(c, h);
        }

        return previousValue;
    }

    private boolean checkParity() {
        return (dht11_dat[4] == ((dht11_dat[0] + dht11_dat[1] + dht11_dat[2] + dht11_dat[3]) & 0xFF));
    }

    public Dht getPreviousValue() {
        return previousValue;
    }
}

