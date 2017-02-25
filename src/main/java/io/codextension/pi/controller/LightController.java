package io.codextension.pi.controller;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;

/**
 * Created by eelkhour on 24.02.2017.
 */
@RestController
public class LightController {
    GpioController gpio;

    @PostMapping
    public void init() {
        gpio = GpioFactory.getInstance();
    }

    @PreDestroy
    public void destroy() {
        gpio.shutdown();
    }

    @RequestMapping(value = "/lights/{state}", method = RequestMethod.GET)
    public String switchLights(@PathVariable(value = "off", required = true, name = "state") String state, Model model) {
        GpioPinDigitalOutput myLed_1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "My LED 1");
        GpioPinDigitalOutput myLed_2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "My LED 2");

        if (state.equalsIgnoreCase("off")) {
            myLed_1.low();
            myLed_2.low();
        } else if (state.equalsIgnoreCase("on")) {
            myLed_1.high();
            myLed_2.high();
        }

        return "Setting light state to: " + state;
    }


}
