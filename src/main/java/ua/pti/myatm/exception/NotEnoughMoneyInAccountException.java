package ua.pti.myatm.exception;

/**
 * Created by valeriy on 24.11.15.
 */


public class NotEnoughMoneyInAccountException extends Exception {
    public NotEnoughMoneyInAccountException(String message) {
        super(message);
    }
}
