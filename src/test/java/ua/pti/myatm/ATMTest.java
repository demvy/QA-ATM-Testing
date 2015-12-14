/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.pti.myatm;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.InOrder;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import ua.pti.myatm.exception.NoCardInsertedException;
import ua.pti.myatm.exception.NotEnoughMoneyInATMException;
import ua.pti.myatm.exception.NotEnoughMoneyInAccountException;

/**
 *
 * @author andrii
 */
public class ATMTest {

    @Test
    public void testGetMoneyInATM(){
        int minimum=0;
        int maximum=100000;
        int random = minimum + (int)(Math.random()*maximum);
        ATM instance = new ATM(random);
        assertEquals(random, instance.getMoneyInATM(),0);
    }

    @Test//(expected = NoCardInsertedException.class)
    public void testValidateCardIfCardIsBlockedOrNotCorrectPIN(){
        int pinCode = 1234;
        Card card = mock(Card.class);
        ATM instance = new ATM(1000);
        when(card.isBlocked() || !card.checkPin(pinCode)).thenReturn(false);
        assertFalse(instance.validateCard(card, pinCode));
        verify(card,atLeast(1)).isBlocked();
        verify(card,atMost(3)).checkPin(pinCode);
       }

    @Test
    public void testValidateCardIfCardIsNotBlockedAndPasswordCorrect(){
        int pinCode = 1234;
        Card card = mock(Card.class);
        ATM instance = new ATM(1000);
        when(!card.isBlocked() && card.checkPin(pinCode)).thenReturn(true);
        assertTrue(card.checkPin(pinCode));
        assertTrue(!card.isBlocked());
        assertTrue(instance.validateCard(card, pinCode));
        verify(card,atMost(3)).checkPin(pinCode);
    }

   @Test
    public void testCheckBalanceIfValidationSuccessful() throws NoCardInsertedException{

       Card card = mock(Card.class);
       Account account = mock(Account.class);
       ATM instance = new ATM(1000);
       instance.setCard(card);
       when(card.getAccount()).thenReturn(account);
       when(card.getAccount().getBalance()).thenReturn(1000.0);
       double expected_result = 1000.0;
       when(instance.checkBalance()).thenReturn(1000.0);
       assertEquals(instance.checkBalance(), expected_result, 0.0);
       verify(card, atLeast(1)).getAccount();
       verify(account, atLeast(1)).getBalance();
   }

    @Test(expected = NoCardInsertedException.class)
    public void testCheckBalanceIFCardIsBlocked() throws NoCardInsertedException{
        Card card = mock(Card.class);
        ATM instance = new ATM(100);
        instance.setCard(card);
        doThrow(NoCardInsertedException.class).when(card).isBlocked();
        assertTrue(card.isBlocked());

    }

    @Test(expected = NullPointerException.class)
    public void testSetCardIfCardIsNull() throws NoCardInsertedException, NullPointerException{
        //Card card = mock(Card.class);
        ATM instance = new ATM(100);
        instance.setCard(null);
    }

    @Test
    public void testSetCardIfCardInside() throws NoCardInsertedException,NullPointerException{
        Card card = mock(Card.class);
        ATM instance = new ATM(1000);
        instance.setCard(card);
        assertEquals(card, instance.getCard());
    }

    @Test
    public void testGetCardIfCardInside() throws NoCardInsertedException{
        Card card = mock(Card.class);
        ATM instance = new ATM(1000);
        instance.setCard(card);
        assertNotNull(instance.getCard());
    }

    @Test(expected = NoCardInsertedException.class)
    public void testGetCardIfCardOutside() throws NoCardInsertedException{
        ATM instance = new ATM(1000);
        assertNull(instance.getCard());
    }

    @Test(expected = NotEnoughMoneyInAccountException.class)
    public void testGetCashIfAccountMoneyNotEnough() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException, IllegalArgumentException {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        double amount = 1000.0;
        when(card.getAccount()).thenReturn(account);
        when(account.getBalance()).thenReturn(400.0);
        doThrow(NotEnoughMoneyInAccountException.class).when(account).getBalance();
        assertEquals(-600.0, account.getBalance() - amount, 0.0);
    }

    @Test(expected = NotEnoughMoneyInATMException.class)
    public void testGetCashIfATMMoneyNotEnough() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException, IllegalArgumentException {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(100.0);
        instance.setCard(card);
        int pinCode = 1234;
        instance.validateCard(card, pinCode);
        double amount = 200.0;
        double accMoney =1200.0;
        when(card.getAccount()).thenReturn(account);
        when(account.getBalance()).thenReturn(accMoney);
        when(card.getAccount().withdrow(amount)).thenReturn(amount);
        when(account.getBalance()).thenReturn(accMoney - amount);
        assertEquals(account.getBalance(),instance.getCash(amount), 0.0);
    }

    @Test(expected = NoCardInsertedException.class)
    public void testGetCashIfCardNotInserted() throws NoCardInsertedException,NotEnoughMoneyInAccountException, NotEnoughMoneyInATMException, IllegalArgumentException{
        Card card = mock(Card.class);
        ATM instance = new ATM(100.0);
        int pinCode = 1234;
        instance.setCard(card);
        when(card.isBlocked()).thenReturn(true);
        when(card.checkPin(pinCode)).thenReturn(false);
        instance.checkBalance();
    }

    @Test
    public void testGetCashRightAmountWitdrowsFromATM() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException,IllegalArgumentException {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(1000.0);
        instance.setCard(card);
        when(card.getAccount()).thenReturn(account);
        when(account.getBalance()).thenReturn(1000.0);
        double amount = 200.0;
        double before = instance.getMoneyInATM();
        when(account.withdrow(amount)).thenReturn(amount);
        instance.getCash(amount);
        double after= instance.getMoneyInATM();
        //assertEquals(account.getBalance(), instance.checkBalance(), 0.0);
        //assertEquals(account.getBalance(), instance.getMoneyInATM(), 0.0);
        //assertEquals(account.getBalance(), instance.getCash(amount), 0.0);
        assertEquals(before-amount, after, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCashIfAmountBelowZero() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException, IllegalArgumentException{
        double amount = -100.0;
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(1000.0);
        instance.getCash(amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testATMIfMoneyBelowZero() throws IllegalArgumentException{
        double moneyInATM = -100.0;
        ATM instance = new ATM(moneyInATM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testATMIfMoneyEqualsToZero() throws IllegalArgumentException{
        double moneyInATM = 0.0;
        ATM instance = new ATM(moneyInATM);
    }

    @Test
    public void testATMIfMoneyAboveZero(){
        double moneyInATM = 100.0;
        ATM instance = new ATM(moneyInATM);
        assertEquals(100.0, instance.getMoneyInATM(),0.0);
    }

    @Test//(expected = NullPointerException.class)
    public void testCheckBalanceIsBlockedIsCalled() throws NoCardInsertedException{
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(1000.0);
        instance.setCard(card);
        when(card.getAccount()).thenReturn(account);
        instance.checkBalance();
        assertEquals(account.getBalance(),instance.checkBalance(),0.0);
        verify(card,atLeastOnce()).isBlocked();
    }

    @Test
    public void testGetCashOrder() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException,IllegalArgumentException {
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(1000.0);
        double amount = 100.0;
        instance.setCard(card);
        when(card.getAccount()).thenReturn(account);
        when(account.getBalance()).thenReturn(1000.0);
        instance.getCash(amount);
        InOrder order = inOrder(account);
        order.verify(account, times(1)).withdrow(amount);
        order.verify(account, atLeastOnce()).getBalance();
    }

    @Test
    public void testWithdrowRightParametr() throws NoCardInsertedException,NotEnoughMoneyInAccountException,NotEnoughMoneyInATMException,IllegalArgumentException{
        Card card = mock(Card.class);
        Account account = mock(Account.class);
        ATM instance = new ATM(1000.0);
        double amount = 100.0;

        when(card.getAccount()).thenReturn(account);
        when(account.getBalance()).thenReturn(1000.0);
        instance.setCard(card);
        instance.getCash(amount);
        verify(account, times(1)).withdrow(amount);
    }


}
