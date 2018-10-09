package io.peiniau.randomletter;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker
public class RandomLetterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RandomLetterApplication.class, args);
    }

}

@RestController
class RandomLetterController {

    private static final Logger log = LoggerFactory.getLogger(RandomLetterController.class);

    private static final Character[] ALPHA = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private final RandomIntClient randomInt;

    @Autowired
    public RandomLetterController(RandomIntClient randomInt) {
        this.randomInt = randomInt;
    }

    @GetMapping("/random")
    @ResponseBody
    public LetterValueResponse random() {
        Integer random = randomInt.random(26).getValue();
        LetterValueResponse letterValueResponse = new LetterValueResponse(ALPHA[random]);
        log.info("Letter: {}", letterValueResponse);
        return letterValueResponse;
    }
}

@FeignClient(value = "randomint", fallback = RandomIntClientFallback.class)
interface RandomIntClient {

    @RequestMapping(method = RequestMethod.GET, value = "/random?bound={bound}")
    IntValueResponse random(@PathVariable("bound") Integer bound);

}

@Component
class RandomIntClientFallback implements RandomIntClient {

    @Override
    public IntValueResponse random(Integer bound) {
        IntValueResponse response = new IntValueResponse();
        response.setValue(0);
        return response;
    }

}

@Data
class IntValueResponse {

    private Integer value;

}

@Data
@AllArgsConstructor
class LetterValueResponse {

    private Character value;

}