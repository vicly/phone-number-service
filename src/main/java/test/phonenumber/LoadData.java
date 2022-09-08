package test.phonenumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import test.phonenumber.domain.Customer;
import test.phonenumber.domain.CustomerRepository;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.domain.PhoneNumberRepository;

@Configuration
public class LoadData {
    private static final Logger log = LoggerFactory.getLogger(LoadData.class);

    @Bean
    CommandLineRunner initDatabase(CustomerRepository customerRepository, PhoneNumberRepository phoneNumberRepository) {
        return args -> {
            Customer ada = customerRepository.save(new Customer("Ada"));
            Customer tim = customerRepository.save(new Customer("Tim"));
            Customer vic = customerRepository.save(new Customer("Vic"));
            log.info("Load data - " + ada);
            log.info("Load data - " + tim);
            log.info("Load data - " + vic);

            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950361", ada.getId())));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950362", ada.getId())));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950363", tim.getId())));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950364")));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950365")));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950366")));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950367")));
            log.info("Load data - " + phoneNumberRepository.save(new PhoneNumber("+61423950368")));
        };
    }
}
