package store;

import model.Account;
import model.Ticket;

import java.util.List;

public interface Store {

    void saveAccount(Account account);
    void saveTicket(Ticket ticket);
    Account findAccByPhone(String phone);
    List<Integer> findAllTickets();

}
