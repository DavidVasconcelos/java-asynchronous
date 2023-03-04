package module4;

import model.Quotation;
import util.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class A_ReadingSeveralTasks {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        var quotationTasks = UtilityClass.getQuotationSupplierList();
        var begin = Instant.now();
        var quotationCFS = quotationTasks
                .stream()
                .map(CompletableFuture::supplyAsync)
                .toList();
        var bestQuotation = CompletableFuture
                .allOf(quotationCFS.toArray(CompletableFuture[]::new))
                .thenApply(v -> // always null be careful
                        quotationCFS
                                .stream()
                                .map(CompletableFuture::join)
                                .min(Comparator.comparing(Quotation::amount))
                                .orElseThrow()
                ).join();
        var end = Instant.now();
        var duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC ] = " + bestQuotation + " (" + duration.toMillis() + "ms)");

    }
}
