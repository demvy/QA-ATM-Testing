package ua.pti.myatm;

import javax.xml.bind.ValidationEvent;
import java.io.IOException;
import ua.pti.myatm.exception.NoCardInsertedException;
import ua.pti.myatm.exception.NotEnoughMoneyInATMException;
import ua.pti.myatm.exception.NotEnoughMoneyInAccountException;

public class ATM {
    private double moneyInATM;
    private Card card;
    //Можно задавать количество денег в банкомате
    ATM(double moneyInATM) throws IllegalArgumentException{
        if(moneyInATM <= 0) throw new IllegalArgumentException("money in ATM can't be less than a zero!");
        else {
            this.moneyInATM = moneyInATM;
        }
    }

    // Возвращает каоличестов денег в банкомате
    public double getMoneyInATM() {
        return moneyInATM;
    }
    public void setCard(Card card) throws NoCardInsertedException, NullPointerException{
            if (card != null) this.card = card;
            else throw new NullPointerException("Card is not entered!");
        //    return true;
        //}
        //else
        //    throw new NoCardInsertedException("Card is not inserted");
    }
    public  Card getCard() throws NoCardInsertedException{
        if(this.card != null) {
            return this.card;
        }
        else
            throw new NoCardInsertedException("Card is not inserted");
    }
    //С вызова данного метода начинается работа с картой
    //Метод принимает карту и пин-код, проверяет пин-код карты и не заблокирована ли она
    //Если неправильный пин-код или карточка заблокирована, возвращаем false. При этом, вызов всех последующих методов у ATM с данной картой должен генерировать исключение NoCardInserted
    public boolean validateCard(Card card, int pinCode) {
        if (card.isBlocked() || !card.checkPin(pinCode)) {
           return false;
        }
        else{
            this.card = card;
            return true;
        }
    }


    //Возвращает сколько денег есть на счету
    public double checkBalance() throws NoCardInsertedException{

        if (this.card.isBlocked()) throw new NoCardInsertedException("Your card is blocked, can't check balance");
        Account account = this.card.getAccount();
        return account.getBalance();
    }
    
    //Метод для снятия указанной суммы
    //Метод возвращает сумму, которая у клиента осталась на счету после снятия
    //Кроме проверки счета, метод так же должен проверять достаточно ли денег в самом банкомате
    //Если недостаточно денег на счете, то должно генерироваться исключение NotEnoughMoneyInAccount 
    //Если недостаточно денег в банкомате, то должно генерироваться исключение NotEnoughMoneyInATM 
    //При успешном снятии денег, указанная сумма должна списываться со счета, и в банкомате должно уменьшаться количество денег
    public double getCash(double amount) throws NotEnoughMoneyInATMException,NotEnoughMoneyInAccountException,NoCardInsertedException{
        if(amount<0)  throw new IllegalArgumentException("Wrong value!!!!Check amount you want to take off!");
        //setCard(this.card);
        Account account = this.card.getAccount();
        if (checkBalance() < amount) throw new NotEnoughMoneyInAccountException("You have not enough money at account");
        if (getMoneyInATM() < amount) throw new NotEnoughMoneyInATMException("There is not enough money in ATM");
        this.moneyInATM -= account.withdrow(amount);
        return account.getBalance();
    }

}