import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class UtilityClass {
    public static Random getRandom() {
        return new Random();
    }

    public static int getServerSleepRandom() {
        return getRandom().nextInt(80, 120);
    }

    public static List<Callable<Quotation>> getCallableList() {
        Callable<Quotation> fetchQuotationA =
                () -> {
                    Thread.sleep(getServerSleepRandom());
                    return new Quotation("Server A", getRandom().nextInt(40, 60));
                };
        Callable<Quotation> fetchQuotationB =
                () -> {
                    Thread.sleep(getServerSleepRandom());
                    return new Quotation("Server B", getRandom().nextInt(30, 70));
                };
        Callable<Quotation> fetchQuotationC =
                () -> {
                    Thread.sleep(getServerSleepRandom());
                    return new Quotation("Server A", getRandom().nextInt(40, 80));
                };
        return List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
    }

    public static List<Supplier<Quotation>> getSupplierList() {
        Supplier<Quotation> fetchQuotationA =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server A", getRandom().nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationB =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server B", getRandom().nextInt(30, 70));
                };
        Supplier<Quotation> fetchQuotationC =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Quotation("Server A", getRandom().nextInt(40, 80));
                };
        return List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
    }

    public static Quotation callableFetchQuotation(Callable<Quotation> task) {
        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Quotation futureFetchQuotation(Future<Quotation> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
