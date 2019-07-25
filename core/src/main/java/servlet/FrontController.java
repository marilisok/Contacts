package servlet;

import commands.Command;
import commands.CommandFactory;
import util.CheckBirthdayUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class FrontController extends HttpServlet {
    private Logger logger = LogManager.getLogger(FrontController.class);
    @Override
    public void init(){
        try {

            CheckBirthdayUtil.getInstance().startService();
        } catch (Exception e) {
            logger.error("Error while init", e);
        }

    }

    @Override
    public void destroy() {
        try {
            logger.info("Init servlet");
            CheckBirthdayUtil.getInstance().stopService();
        } catch (Exception e) {
            logger.error("Error while destroy servlet", e);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);

    }
    private void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        try {

            response.setCharacterEncoding("UTF-8");
            request.setCharacterEncoding("UTF-8");
            CommandFactory client = new CommandFactory();
            Command command = client.defineCommand(request);
            command.execute(request,response);
        } catch (Exception e) {
            logger.error("Error while process request", e);
        }
    }
}
