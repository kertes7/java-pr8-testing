package ua.duikt.pr8;

/** Залежність №1 сервісу: прайс-лист (джерело місячного тарифу). Реалізації тут немає — у тесті це стаб. */
public interface PriceList {

    double monthlyRate();
}
