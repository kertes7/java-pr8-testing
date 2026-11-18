package ua.duikt.pr8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Дослід 1 і 2: три тести за схемою Given-When-Then та параметризовані тести на межах. */
class SubscriptionTest {

    private static final double EPS = 0.001;       // похибка для порівняння дробових чисел

    // ----- Дослід 1: три тести -----

    @Test
    @DisplayName("Звичайний випадок: 3 місяці без знижки = 3 * 199")
    void regularCase() {
        // Given
        Subscription subscription = new Subscription();
        // When
        double price = subscription.price(3);
        // Then
        assertEquals(597.0, price, EPS);
    }

    @Test
    @DisplayName("Некоректний аргумент: 0 місяців -> IllegalArgumentException")
    void invalidArgument() {
        // Given
        Subscription subscription = new Subscription();
        // When + Then
        assertThrows(IllegalArgumentException.class, () -> subscription.price(0));
    }

    @Test
    @DisplayName("Знижка 10 % від 6 місяців: 6 * 199 * 0.9 = 1074.6")
    void discountFromSixMonths() {
        // Given
        Subscription subscription = new Subscription();
        // When
        double price = subscription.price(6);
        // Then
        assertEquals(1074.6, price, EPS);
    }

    // ----- Дослід 2: параметризовані тести на межових значеннях (0, 1, 5, 6, 12, 13) -----

    @ParameterizedTest(name = "{0} міс. -> {1} грн")
    @CsvSource({
            "1, 199.0",        // нижня межа діапазону
            "5, 995.0",        // останнє значення без знижки
            "6, 1074.6",       // перше значення зі знижкою
            "12, 2149.2"       // верхня межа діапазону
    })
    void priceOnBoundaries(int months, double expected) {
        assertEquals(expected, new Subscription().price(months), EPS);
    }

    @ParameterizedTest(name = "{0} міс. -> виняток")
    @ValueSource(ints = {0, 13})            // значення поза допустимим діапазоном
    void outOfRangeThrows(int months) {
        assertThrows(IllegalArgumentException.class, () -> new Subscription().price(months));
    }
}
