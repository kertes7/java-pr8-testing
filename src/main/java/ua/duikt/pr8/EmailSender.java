package ua.duikt.pr8;

/** Залежність №2 сервісу: відправник листів (квитанція). Реалізації тут немає — у тесті це мок. */
public interface EmailSender {

    void send(String to, String message);
}
