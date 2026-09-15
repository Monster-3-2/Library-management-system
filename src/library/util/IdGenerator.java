package library.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdGenerator — produces unique, sequential, human-readable IDs.
 *
 * Uses AtomicInteger for thread safety.
 * Counters start at values that avoid collision with sample data IDs.
 *
 * @author  Student
 * @version 2.0
 */
public class IdGenerator {

    private static final AtomicInteger bookCounter        = new AtomicInteger(1000);
    private static final AtomicInteger memberCounter      = new AtomicInteger(2000);
    private static final AtomicInteger transactionCounter = new AtomicInteger(3000);

    public static String generateBookId()        { return "BK-"  + bookCounter.getAndIncrement(); }
    public static String generateMemberId()      { return "MB-"  + memberCounter.getAndIncrement(); }
    public static String generateTransactionId() { return "TXN-" + transactionCounter.getAndIncrement(); }
}
