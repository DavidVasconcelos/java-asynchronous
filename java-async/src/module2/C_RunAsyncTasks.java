package module2;

import model.Quotation;
import util.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class C_RunAsyncTasks {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        var quotationTasks = UtilityClass.getQuotationSupplierList(false);
        var begin = Instant.now();
        var bestQuotation = quotationTasks.stream()
                .map(CompletableFuture::supplyAsync)
                .toList() // futures
                .stream()
                .map(UtilityClass::futureFetchQuotation)
                .toList() // quotations
                .stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();
        var end = Instant.now();
        var duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC ] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
    }
}