package ir.sadad.co.checkversionapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Calendar;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class CheckVersionApiApplication {

    public static void main(String[] args) {
        log.warn("Jdk DST offset is: {}", Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DST_OFFSET));
        SpringApplication.run(CheckVersionApiApplication.class, args);
    }

}
