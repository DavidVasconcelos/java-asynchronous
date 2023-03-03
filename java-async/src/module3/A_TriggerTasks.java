package module3;

import model.Quotation;
import util.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;

public class A_TriggerTasks {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        var quotationTasks = UtilityClass.getSupplierList();
        var quotations = new ConcurrentLinkedDeque<Quotation>();
        var begin = Instant.now();
        quotationTasks.stream()
                .map(CompletableFuture::supplyAsync)
                .toList() //futures
                .stream()
                .map(future -> future.thenAccept(quotations::add)) //quotations
                .toList()
                .stream()
                .map(CompletableFuture::join) //join
                .toList();
        var bestQuotation = quotations
                .stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();
        var end = Instant.now();
        var duration = Duration.between(begin, end);
        System.out.println("Best quotation [ASYNC ] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
    }
}
