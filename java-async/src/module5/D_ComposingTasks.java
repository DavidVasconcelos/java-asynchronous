package module5;

import model.Quotation;
import model.TravelPage;
import model.Weather;
import util.UtilityClass;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class D_ComposingTasks {

    public static void main(String[] args) {
        run();
    }

    @SuppressWarnings("unchecked")
    public static void run() {
        var quotationExecutor = Executors.newFixedThreadPool(4, UtilityClass.quotationThreadFactory);
        var weatherExecutor = Executors.newFixedThreadPool(4, UtilityClass.weatherThreadFactory);
        var minExecutor = Executors.newFixedThreadPool(1, UtilityClass.minThreadFactory);
        var weatherTasks = UtilityClass.getWeatherSupplierList(false);
        var quotationTasks = UtilityClass.getQuotationSupplierList(false);
        CompletableFuture<Weather> anyWeather = Arrays.stream(weatherTasks
                        .stream()
                        .map(task -> CompletableFuture.supplyAsync(task, weatherExecutor))
                        .toArray(CompletableFuture[]::new))
                .reduce(CompletableFuture::anyOf)
                .orElseThrow()
                .thenApply(o -> o);
        var quotationCFS = quotationTasks
                .stream()
                .map(task -> CompletableFuture.supplyAsync(task, quotationExecutor))
                .toList();
        var bestQuotationCF = CompletableFuture
                .allOf(quotationCFS.toArray(CompletableFuture[]::new))
                .thenApplyAsync(v -> { // always null be careful
                            System.out.println("AllOf then apply " + Thread.currentThread());
                            return quotationCFS
                                    .stream()
                                    .map(CompletableFuture::join)
                                    .min(Comparator.comparing(Quotation::amount))
                                    .orElseThrow();
                        },
                        minExecutor); // executor parameter only in methods with async suffix, otherwise it will be
                                      // executed in the CF's previous thread

        bestQuotationCF
                .thenCompose(quotation -> anyWeather
                        .thenApply(weather -> new TravelPage(quotation, weather)))
                .thenAccept(System.out::println)
                .join();

        quotationExecutor.shutdown();
        weatherExecutor.shutdown();
        minExecutor.shutdown();
    }
}
