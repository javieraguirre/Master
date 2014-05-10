/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servJugadores;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Javi
 */
@Path("sokobanurjcwebserviceport")
public class SokobanURJCWebServicePort {
    private servJugadores_client.SokobanURJCWebService port;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SokobanURJCWebServicePort
     */
    public SokobanURJCWebServicePort() {
        port = getPort();
    }

    /**
     * Invokes the SOAP method addition
     * @param parameter1 resource URI parameter
     * @param parameter2 resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("addition/")
    public String getAddition(@QueryParam("parameter1")
            @DefaultValue("0.0") double parameter1, @QueryParam("parameter2")
            @DefaultValue("0.0") double parameter2) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.addition(parameter1, parameter2);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     * Invokes the SOAP method hello
     * @param name resource URI parameter
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/plain")
    @Consumes("text/plain")
    @Path("hello/")
    public String getHello(@QueryParam("name") String name) {
        try {
            // Call Web Service Operation
            if (port != null) {
                java.lang.String result = port.hello(name);
                return result;
            }
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }

    /**
     *
     */
    private servJugadores_client.SokobanURJCWebService getPort() {
        try {
            // Call Web Service Operation
            servJugadores_client.SokobanURJCWebService_Service service = new servJugadores_client.SokobanURJCWebService_Service();
            servJugadores_client.SokobanURJCWebService p = service.getSokobanURJCWebServicePort();
            return p;
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }
        return null;
    }
}
