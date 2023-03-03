import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

public class A_RunSynchronousTasks {

   public static void main(String[] args) {
        run();
    }

    public static void run() {
        var quotationTasks = UtilityClass.getCallableList();
        var begin = Instant.now();
        var bestQuotation = quotationTasks.stream()
                .map(UtilityClass::callableFetchQuotation)
                .min(Comparator.comparing(Quotation::amount))
                .orElseThrow();
        var end = Instant.now();
        var duration = Duration.between(begin, end);
        System.out.println("Best quotation [SYNC ] = " + bestQuotation + " (" + duration.toMillis() + "ms)");
    }
}