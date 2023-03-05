package module6;

import model.Quotation;
import model.TravelPage;
import model.Weather;
import util.UtilityClass;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class E_ComposingTasks {

    public static void main(String[] args) {
        run();
    }

    @SuppressWarnings("unchecked")
    public static void run() {
        var weatherTasks = UtilityClass.getWeatherSupplierList(true);
        var quotationTasks = UtilityClass.getQuotationSupplierList(true);
        CompletableFuture<Weather> anyWeather = Arrays.stream(weatherTasks
                        .stream()
                        .map(weatherTask -> CompletableFuture.supplyAsync(weatherTask)
                                .exceptionally(e -> {
                                    System.out.printf("e = %s", e);
                                    return new Weather("Unknown", "Unknown");
                                })
                        )
                        .toArray(CompletableFuture[]::new))
                .reduce(CompletableFuture::anyOf)
                .orElseThrow()
                .thenApply(o -> o);
        var quotationCFS = quotationTasks
                .stream()
                .map(quotationTask -> CompletableFuture.supplyAsync(quotationTask)
                        .handle((quotation, exception) -> {
                            if (exception == null) {
                                return quotation;
                            } else {
                                System.out.printf("exception %s", exception);
                                return new Quotation("Unknown", 10000000);
                            }
                        })
                )
                .toList();
        var bestQuotationCF = CompletableFuture
                .allOf(quotationCFS.toArray(CompletableFuture[]::new))
                .thenApply(v -> // always null be careful
                        quotationCFS
                                .stream()
                                .map(CompletableFuture::join)
                                .min(Comparator.comparing(Quotation::amount))
                                .orElseThrow()
                );

        bestQuotationCF
                .thenCompose(quotation -> anyWeather
                        .thenApply(weather -> new TravelPage(quotation, weather)))
                .thenAccept(System.out::println)
                .join();
    }
}
