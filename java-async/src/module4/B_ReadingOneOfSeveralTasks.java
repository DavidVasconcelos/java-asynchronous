package module4;

import util.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class B_ReadingOneOfSeveralTasks {
    public static void main(String[] args) {
        run();
    }

    @SuppressWarnings("unchecked")
    public static void run() {
        var weatherTasks = UtilityClass.getWeatherSupplierList(false);
        var begin = Instant.now();
        Arrays.stream(weatherTasks
                        .stream()
                        .map(CompletableFuture::supplyAsync)
                        .toArray(CompletableFuture[]::new))
                .reduce(CompletableFuture::anyOf)
                .orElseThrow()
                .thenAccept(firstWeather -> {
                    var duration = Duration.between(begin, Instant.now());
                    System.out.printf("First weather data that was retrieved from the servers [ASYNC ] %s (%d ms)",
                            firstWeather, duration.toMillis());
                })
                .join();
    }
}
