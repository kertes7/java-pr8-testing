package ua.duikt.pr8;

/**
 * Підписка стрімінгового сервісу (варіант 0).
 * Правило: від 1 до 12 місяців; 199 грн за місяць; від 6 місяців — знижка 10 %.
 * Для некоректної кількості місяців кидається IllegalArgumentException.
 */
public class Subscription {

    public static final double DEFAULT_MONTHLY_RATE = 199.0;
    public static final int MIN_MONTHS = 1;
    public static final int MAX_MONTHS = 12;
    public static final int DISCOUNT_FROM_MONTHS = 6;
    public static final double DISCOUNT = 0.10;

    private final double monthlyRate;

    public Subscription() {
        this(DEFAULT_MONTHLY_RATE);
    }

    public Subscription(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public double price(int months) {
        if (months < MIN_MONTHS || months > MAX_MONTHS) {
            throw new IllegalArgumentException("Кількість місяців має бути від " + MIN_MONTHS + " до " + MAX_MONTHS + ": " + months);
        }
        double total = months * monthlyRate;
        if (months >= DISCOUNT_FROM_MONTHS) {
            total = total * (1 - DISCOUNT);
        }
        return total;
    }
}
