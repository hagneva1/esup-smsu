package org.esupportail.smsu.web.controllers;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.esupportail.smsu.business.ServiceManager;
import org.esupportail.smsu.web.beans.UIService;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/services")
@RolesAllowed("FCTN_GESTION_SERVICES_CP")
public class ServicesSmsuController {

	@Autowired private ServiceManager serviceManager;
	
	@GET
	@Produces("application/json")
	@PermitAll
	public List<UIService> getUIServices() {
		return serviceManager.getAllUIServices();
	}
	 
	@POST
	public void create(UIService uiService) {
		createOrModify(uiService, true);
	}

	@PUT
	@Path("/{id:\\d+}")
	public void modify(UIService uiService, @PathParam("id") int id) {
		uiService.id = id;
		createOrModify(uiService, false);
	}

	@DELETE
	@Path("/{id:\\d+}")
	public void delete(@PathParam("id") int id) {
		serviceManager.deleteUIService(id);
	}


	private void createOrModify(UIService uiService, boolean isAddMode) {
		Integer id = isAddMode ? null : uiService.id;
		if (!serviceManager.isKeyAvailable(uiService.key, id)) {
			throw new InvalidParameterException("SERVICE.KEY.ERROR");
		}

		if (!serviceManager.isNameAvailable(uiService.name, id)) {
			throw new InvalidParameterException("SERVICE.NAME.ERROR");
		}

			if (isAddMode) {
				serviceManager.addUIService(uiService);
			} else {
				serviceManager.updateUIService(uiService);
			}
	}

}
