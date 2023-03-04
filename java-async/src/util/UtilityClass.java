package util;

import model.Quotation;
import model.Weather;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

public final class UtilityClass {
    public static Random getRandom() {
        return new Random();
    }

    public static int getServerSleepRandom() {
        return getRandom().nextInt(80, 120);
    }

    public static List<Callable<Quotation>> getQuotationCallableList() {
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

    public static List<Supplier<Quotation>> getQuotationSupplierList() {
        Supplier<Quotation> fetchQuotationA =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("QA running in " + Thread.currentThread());
                    return new Quotation("Server A", getRandom().nextInt(40, 60));
                };
        Supplier<Quotation> fetchQuotationB =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("QB running in " + Thread.currentThread());
                    return new Quotation("Server B", getRandom().nextInt(30, 70));
                };
        Supplier<Quotation> fetchQuotationC =
                () -> {
                    try {
                        Thread.sleep(getServerSleepRandom());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("QC running in " + Thread.currentThread());
                    return new Quotation("Server A", getRandom().nextInt(40, 80));
                };
        return List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
    }

    public static List<Supplier<Weather>> getWeatherSupplierList() {
        Supplier<Weather> fetchWeatherA =
                () -> {
                    try {
                        Thread.sleep(getRandom().nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("WA running in " + Thread.currentThread());
                    return new Weather("Server A", "Sunny");
                };
        Supplier<Weather> fetchWeatherB =
                () -> {
                    try {
                        Thread.sleep(getRandom().nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("WB running in " + Thread.currentThread());
                    return new Weather("Server B", "Mostly Sunny");
                };
        Supplier<Weather> fetchWeatherC =
                () -> {
                    try {
                        Thread.sleep(getRandom().nextInt(80, 120));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("WC running in " + Thread.currentThread());
                    return new Weather("Server C", "Almost Sunny");
                };
        return List.of(fetchWeatherA, fetchWeatherB, fetchWeatherC);
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

    private static int quotationThreadIndex = 0;
    public static ThreadFactory quotationThreadFactory =
            r -> new Thread(r, "Quotation-" + quotationThreadIndex++);

    private static int weatherThreadIndex = 0;
    public static ThreadFactory weatherThreadFactory = r -> new Thread(r, "Weather-" + weatherThreadIndex++);

    private static int minThreadIndex = 0;
    public static ThreadFactory minThreadFactory = r -> new Thread(r, "Min-" + minThreadIndex++);
}
