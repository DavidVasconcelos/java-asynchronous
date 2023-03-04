package module4;

import model.Quotation;
import model.TravelPage;
import model.Weather;
import util.UtilityClass;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class C_ReadingSeveralTasks {

    public static void main(String[] args) {
        run();
    }

    @SuppressWarnings("unchecked")
    public static void run() {
        var weatherTasks = UtilityClass.getWeatherSupplierList();
        var quotationTasks = UtilityClass.getQuotationSupplierList();
        CompletableFuture<Weather> anyWeather = Arrays.stream(weatherTasks
                        .stream()
                        .map(CompletableFuture::supplyAsync)
                        .toArray(CompletableFuture[]::new))
                .reduce(CompletableFuture::anyOf)
                .orElseThrow()
                .thenApply(o -> o);
        var quotationCFS = quotationTasks
                .stream()
                .map(CompletableFuture::supplyAsync)
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

        //combine
        bestQuotationCF
                .thenCombine(anyWeather, TravelPage::new)
                .thenAccept(System.out::println)
                .join();

        //compose
        bestQuotationCF
                .thenCompose(quotation -> anyWeather
                        .thenApply(weather -> new TravelPage(quotation, weather)))
                .thenAccept(System.out::println)
                .join();
    }
}
