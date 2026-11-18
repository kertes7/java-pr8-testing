package ua.duikt.pr8;

import java.util.Locale;

/** Продовження підписки: тариф бере з PriceList, рахує за правилом Subscription, квитанцію шле через EmailSender. */
public class RenewalService {

    private final PriceList priceList;
    private final EmailSender emailSender;

    public RenewalService(PriceList priceList, EmailSender emailSender) {
        this.priceList = priceList;
        this.emailSender = emailSender;
    }

    public double renew(String email, int months) {
        double rate = priceList.monthlyRate();                         // значення з першої залежності
        double total = new Subscription(rate).price(months);           // правило з досліду 1
        emailSender.send(email, String.format(Locale.US,               // повідомлення другій залежності
                "Підписку продовжено на %d міс. Сума: %.2f грн", months, total));
        return total;
    }
}
