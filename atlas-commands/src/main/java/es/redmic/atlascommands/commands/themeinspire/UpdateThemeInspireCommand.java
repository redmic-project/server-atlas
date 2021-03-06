package es.redmic.atlascommands.commands.themeinspire;

import es.redmic.atlaslib.dto.themeinspire.ThemeInspireDTO;
import es.redmic.commandslib.commands.Command;

/*-
 * #%L
 * atlas commands
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public class UpdateThemeInspireCommand extends Command {

	private ThemeInspireDTO themeInspire;
	
	public UpdateThemeInspireCommand() {
	}
	
	public UpdateThemeInspireCommand(ThemeInspireDTO themeInspire) {
		this.setThemeInspire(themeInspire);
	}
	
	public ThemeInspireDTO getThemeInspire() {
		return themeInspire;
	}

	public void setThemeInspire(ThemeInspireDTO themeInspire) {
		this.themeInspire = themeInspire;
	}
}
