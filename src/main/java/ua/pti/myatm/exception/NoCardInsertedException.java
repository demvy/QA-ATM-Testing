package ua.pti.myatm.exception;

/**
 * Created by valeriy on 24.11.15.
 */


public class NoCardInsertedException extends Exception {
    public NoCardInsertedException(String message) {
        super(message);
    }
}
