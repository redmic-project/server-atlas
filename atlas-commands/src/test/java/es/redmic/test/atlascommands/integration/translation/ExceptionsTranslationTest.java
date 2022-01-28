package es.redmic.test.atlascommands.integration.translation;

/*-
 * #%L
 * Atlas-management
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.atlascommands.AtlasCommandsApplication;
import es.redmic.atlascommands.aggregate.ThemeInspireAggregate;
import es.redmic.atlascommands.commands.themeinspire.UpdateThemeInspireCommand;
import es.redmic.commandslib.exceptions.HistoryNotFoundException;
import es.redmic.exception.common.PatternUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AtlasCommandsApplication.class })
@ActiveProfiles("test")
@TestPropertySource(properties = { "schema.registry.port=0" })
public class ExceptionsTranslationTest {

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@Autowired
	MessageSource messageSource;

	@Mock
	ThemeInspireAggregate aggregate;

	protected static final String resourcePathSpanish = "classpath*:i18n/messages_es_ES.properties",
			resourcePathEnglish = "classpath*:i18n/messages_en_EN.properties";

	@Before
	public void init() {

		when(aggregate.process(any(UpdateThemeInspireCommand.class)))
				.thenThrow(new HistoryNotFoundException("id", "1"));
	}

	@Test
	public void getEnglishMessage_returnI18nMessageInDefaultLocale_WhenCodePropertyExist() {

		try {
			aggregate.process(new UpdateThemeInspireCommand());
		} catch (HistoryNotFoundException e) {

			String code = e.getCode().toString();

			String[] fields = (String[]) e.getFieldErrors().toArray();

			String mess = messageSource.getMessage(code, fields, new Locale("en", "EN"));

			Assert.assertEquals(getMessage(code, fields, resourcePathEnglish), mess);
		}
	}

	@Test
	public void getSpanishMessage_returnI18nMessage_WhenCodePropertyExist() {

		try {
			aggregate.process(new UpdateThemeInspireCommand());
		} catch (HistoryNotFoundException e) {

			String code = e.getCode().toString();

			String[] fields = (String[]) e.getFieldErrors().toArray();

			String mess = messageSource.getMessage(code, fields, new Locale("es", "ES"));

			Assert.assertEquals(getMessage(code, fields, resourcePathSpanish), mess);
		}
	}

	@Test
	public void getMessage_returnCode_WhenCodeNotInI18nFile() {

		String code = "CodeNotFound";

		String mess = messageSource.getMessage(code, null, new Locale("es", "ES"));

		Assert.assertEquals(code, mess);
	}

	private String getMessage(String code, String[] fields, String resourcePath) {

		String message = PatternUtils.getPattern(code, resourcePath);
		MessageFormat format = new MessageFormat(message);

		return format.format(fields);
	}

}
