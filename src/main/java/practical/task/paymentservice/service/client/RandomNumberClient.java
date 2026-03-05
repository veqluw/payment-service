package practical.task.paymentservice.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class RandomNumberClient {

    private final WebClient webClient;

    @Autowired
    public RandomNumberClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://www.randomnumberapi.com/api/v1.0/random").build();
    }

    public List<Integer> getRandomNumber() {
        return webClient.get()
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Integer>>() {})
                .block();
    }
}