package org.example.temperature.command;
 
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.example.temperature.manager.administration.service.TemperatureManagerAdministration ;
 
import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
 
//Define this class as an implementation of a component :
@Component
//Create an instance of the component
@Instantiate(name = "temperature.administration.command")
//Use the handler command and declare the command as a command provider. The
//namespace is used to prevent name collision.
@CommandProvider(namespace = "temperature")
public class TemperatureCommandImpl {
 
    // Declare a dependency to a TemperatureAdministration service
    @Requires
    private TemperatureManagerAdministration m_administrationService;
 
	/** Component Lifecycle Method */
    @Invalidate
	public void stop() {
		System.out.println("Temperature Command is stoping ...");
	}

	/** Component Lifecycle Method */
	@Validate
	public void start() {
		System.out.println("Temperature Command is starting ...");
	}
    /**
     * Command implementation to express that the temperature is too high in the given room
     *
     * @param room the given room
     */
 
    // Each command should start with a @Command annotation
    @Command
    public void tempTooHigh(String room) {
        m_administrationService.temperatureIsTooHigh(room);
    }
 
    @Command
    public void tempTooLow(String room){
        m_administrationService.temperatureIsTooLow(room);
    }
 
}