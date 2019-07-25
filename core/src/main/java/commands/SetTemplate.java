package commands;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.stringtemplate.v4.ST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

public class SetTemplate implements Command{
    private Logger logger = LogManager.getLogger(SetTemplate.class);
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        try {
            String template = request.getParameter("template").equals("Message to friend")? "template1.st":"template2.st";
            String text = createTemplate(template);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            String json = objectMapper.writeValueAsString(text);
            logger.info("Template was setting");
            response.getWriter().write(json);
        } catch (IOException e) {
            throw new CommandException("ERROR setting template "+e.getMessage());
        }

    }
    private String createTemplate(String resourceFileName) throws IOException {
        String templateString = readFromFile(resourceFileName);
        ST template = new ST(templateString);
        Properties properties = new Properties();
        properties.load(SetTemplate.class.getResourceAsStream("/email.properties"));
        String admin = properties.getProperty("ADMIN-NAME");
        template.add("admin", admin);
        return template.render();
    }
    private String readFromFile(String resourceFileName) throws IOException {
        try (BufferedReader bReader = new BufferedReader(
                new InputStreamReader(SetTemplate.class.getResourceAsStream("/" + resourceFileName)))){
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

}

