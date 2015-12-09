package ua.pti.myatm.exception;

/**
 * Created by valeriy on 24.11.15.
 */


public class NotEnoughMoneyInATMException extends Exception {
    public NotEnoughMoneyInATMException(String message) {
        super(message);
    }
}
