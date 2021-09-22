package servlet;

import com.google.gson.Gson;
import model.Account;
import model.Ticket;
import store.PsqlStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class HallServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Integer> list = PsqlStore.instOf().findAllTickets();
        String json = new Gson().toJson(list);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        out.println(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        PsqlStore.instOf().saveAccount(new Account(
                0,
                req.getParameter("name"),
                req.getParameter("phone")
        ));
        Account account = PsqlStore.instOf().findAccByPhone(req.getParameter("phone"));
        int accountId = account.getId();
        PsqlStore.instOf().saveTicket(new Ticket(
                0,
                Integer.parseInt(req.getParameter("row")),
                Integer.parseInt(req.getParameter("cell")),
                accountId
        ));
    }


}
