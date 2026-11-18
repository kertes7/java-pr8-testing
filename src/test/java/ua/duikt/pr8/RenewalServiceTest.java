package ua.duikt.pr8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Дослід 3: сервіс із двома залежностями.
 * priceList — СТАБ (задаємо, що повертати); emailSender — МОК (перевіряємо, що його викликали).
 */
@ExtendWith(MockitoExtension.class)
class RenewalServiceTest {

    @Mock
    PriceList priceList;        // використовується як стаб

    @Mock
    EmailSender emailSender;    // використовується як мок

    @Test
    void renewCalculatesTotalAndSendsReceipt() {
        // Given: стаб повертає тариф 199 грн
        when(priceList.monthlyRate()).thenReturn(199.0);
        RenewalService service = new RenewalService(priceList, emailSender);

        // When
        double total = service.renew("user@example.com", 6);

        // Then: результат за правилом (знижка від 6 місяців) ...
        assertEquals(1074.6, total, 0.001);
        // ... і квитанцію надіслано другій залежності
        verify(emailSender).send("user@example.com", "Підписку продовжено на 6 міс. Сума: 1074.60 грн");
    }
}
