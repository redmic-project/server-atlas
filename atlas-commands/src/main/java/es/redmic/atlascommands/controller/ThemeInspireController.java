package es.redmic.atlascommands.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.commandslib.controller.CommandController;
import es.redmic.commandslib.service.CommandServiceItfc;

@Controller
@RequestMapping(value = "${controller.mapping.themeinspire}")
public class ThemeInspireController extends CommandController<ThemeInspireDTO> {

	@Autowired
	public ThemeInspireController(CommandServiceItfc<ThemeInspireDTO> service) {
		super(service);
	}
}
