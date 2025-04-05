package app.security.rest;

import app.security.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static io.javalin.apibuilder.ApiBuilder.*;


public class SecurityRoutes
{
    // declare controllers here
    private static SecurityController securityController = new SecurityController();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static Logger logger = LoggerFactory.getLogger(SecurityRoutes.class);

    public static EndpointGroup getRoutes(EntityManagerFactory emf)
    {
        // instantiate controllers with emf here

        return () ->
        {
            path("auth", () ->
            {
                post("register", securityController.register(), Role.ANYONE);
                post("login", securityController.login(), Role.ANYONE);
            });
            path("secured", () ->
            {
                get("demo", ctx ->
                {
                    Set<RouteRole> assignedRoles = ctx.routeRoles();
                    System.out.println("Assigned roles inside route: " + assignedRoles);
                    ctx.json(objectMapper.createObjectNode().put("msg", "Success"));
                }, Role.USER);
            });
        };
    }
}