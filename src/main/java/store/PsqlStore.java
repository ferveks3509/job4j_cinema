package store;

import model.Account;
import model.Ticket;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PsqlStore implements Store {

    private final static Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties properties = new Properties();

        try (BufferedReader br = new BufferedReader(new FileReader("resources/db.properties"))) {
            properties.load(br);
        } catch (Exception e) {
            LOG.error("IllegalStateException", e);
        }
        try {
            Class.forName(properties.getProperty("jdbc.driver"));
        } catch (Exception e) {
            LOG.error("IllegalStateException", e);
        }
        pool.setDriverClassName(properties.getProperty("jdbc.driver"));
        pool.setUrl(properties.getProperty("jdbc.url"));
        pool.setUsername(properties.getProperty("jdbc.username"));
        pool.setPassword(properties.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public void saveAccount(Account account) {
        if (account.getId() == 0) {
            createAccount(account);
        } else {
            updateAccount(account);
        }
    }
    private Account createAccount(Account account) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("insert into account(username, phone) values (?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getName());
            ps.setString(2, account.getPhone());
            ps.execute();
            try(ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    account.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            LOG.error("Error in method create Account", e);
        }
        return account;
    }
    private void updateAccount (Account account) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("update accont set name = ?, phone = ? where id = ?")) {
            ps.setString(1, account.getName());
            ps.setString(2, account.getPhone());
            ps.setInt(3, account.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Error in update method", e);
        }
    }

    @Override
    public void saveTicket(Ticket ticket) {
       if (ticket.getId() == 0) {
           createTicket(ticket);
       } else {
           updateTicket(ticket);
       }
    }

    private Ticket createTicket(Ticket ticket) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("insert into ticket(row, cell, accoint_id) values (?,?,?)",
            PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ticket.getRow());
            ps.setInt(2, ticket.getCell());
            ps.setInt(3, ticket.getAccount_id());
            ps.execute();
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt("id"));
                }
            }
        } catch (Exception e) {
            LOG.error("Error in create Ticket method", e);
        }
        return ticket;
    }
    private void updateTicket(Ticket ticket) {
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("update ticket set row = ?, cell = ? account_id = ? where id = ?")) {
            ps.setInt(1, ticket.getRow());
            ps.setInt(2, ticket.getCell());
            ps.setInt(3, ticket.getAccount_id());
            ps.setInt(4, ticket.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Error in update Ticket method", e);
        }
    }

    @Override
    public Account findAccByPhone(String phone) {
        Account account = null;
        try (Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("select * from account where phone = (?)")) {
            ps.setString(1, phone);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    account = new Account(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (Exception e) {
            LOG.error("Error in find account by phone method", e);
        }
        return account;
    }

    @Override
    public List<Integer> findAllTickets() {
        List<Integer> tickets = new ArrayList<>();
        try(Connection cn = pool.getConnection();
        PreparedStatement ps = cn.prepareStatement("select * from ticket")) {
            try (ResultSet rs = ps.getResultSet()){
                while (rs.next()) {
                    tickets.add(rs.getInt("row") * 10 + rs.getInt("cell"));
                }
            }
        } catch (Exception e) {
            LOG.error("Error in find all tickets method", e);
        }
        return tickets;
    }
}
