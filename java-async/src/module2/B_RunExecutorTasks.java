package module2;

import model.Quotation;
import util.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class B_RunExecutorTasks {

    public static void main(String[] args) {
        run();
    }

    public static void run() {
        var quotationTasks = UtilityClass.getCallableList();
        var executor = Executors.newFixedThreadPool(4);
        var begin = Instant.now();
        var bestQuotation = quotationTasks.stream()
                .map(executor::submit)
                .toList() //futures
                .stream()
                .map(UtilityClass::futureFetchQuotation)
                .toList() //quotations
                .stream()
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();
        var end = Instant.now();
        var duration = Duration.between(begin, end);
        System.out.println("Best quotation [Parallel ] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
        executor.shutdown();
    }
}