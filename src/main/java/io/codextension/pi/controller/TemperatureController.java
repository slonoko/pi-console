package io.codextension.pi.controller;

import io.codextension.pi.component.DhtReader;
import io.codextension.pi.model.Dht;
import io.codextension.pi.repository.DhtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by elie on 26.02.2017.
 */
@RestController
@RequestMapping("/temperature")
public class TemperatureController {

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, true));
    }

    @Autowired
    private DhtRepository dhtRepository;

    @RequestMapping("/current")
    public Dht getCurrent() {
        dhtRepository.findAll();
            DhtReader reader = new DhtReader();
            return reader.getValue();
    }

    @RequestMapping("/range") // ?from=15.09.2012-10:12&to=15.09.2017-10:12
    public List<Dht> getRange(@RequestParam(name = "from", required = true)  Date fromDate, @RequestParam(name = "to", required = true) Date toDate) {
        return dhtRepository.findByMeasuredDateBetween(fromDate, toDate);
    }

}