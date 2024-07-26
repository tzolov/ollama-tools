package com.example.ollama;

import java.util.function.Function;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import com.example.ollama.OllamaApplication.MockWeatherService.WeatherRequest;
import com.example.ollama.OllamaApplication.MockWeatherService.WeatherResponse;

@SpringBootApplication
public class OllamaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OllamaApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ChatClient.Builder chatClientBuilder) {
		return args -> {
			var chatClient = chatClientBuilder.build();

			var response = chatClient
				.prompt()
				.user("What is the weather in Amsterdam and Paris?")
				.functions("weatherFunction") // reference by bean name.
				.call()
				.content();

			System.out.println(response);
		};
	}

	@Bean
	@Description("Get the weather in location")
	public Function<WeatherRequest, WeatherResponse> weatherFunction() {
		return new MockWeatherService();
	}

	public static class MockWeatherService
			implements Function<MockWeatherService.WeatherRequest, MockWeatherService.WeatherResponse> {

		public record WeatherRequest(String location, String unit) {
		}

		public record WeatherResponse(double temp, String unit) {
		}

		@Override
		public WeatherResponse apply(WeatherRequest request) {
			double temperature = request.location().contains("Amsterdam") ? 20 : 25;
			return new WeatherResponse(temperature, request.unit);
		}

	}

}
