package es.redmic.test.atlasview.integration.translation;

/*-
 * #%L
 * Atlas-query-endpoint
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

import static org.mockito.ArgumentMatchers.anyString;
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

import es.redmic.atlasview.AtlasViewApplication;
import es.redmic.atlasview.repository.themeinspire.ThemeInspireESRepository;
import es.redmic.exception.common.PatternUtils;
import es.redmic.exception.data.ItemNotFoundException;

@SpringBootTest(classes = { AtlasViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "schema.registry.port=0" })
@ActiveProfiles("test")
public class ExceptionsTranslationTest {

	@Autowired
	MessageSource messageSource;

	@Mock
	ThemeInspireESRepository repository;

	protected static final String resourcePathSpanish = "classpath*:i18n/messages_es_ES.properties",
			resourcePathEnglish = "classpath*:i18n/messages_en_EN.properties";

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@Before
	public void init() {

		when(repository.findById(anyString())).thenThrow(new ItemNotFoundException("id", "1"));
	}

	@Test
	public void getEnglishMessage_returnI18nMessageInDefaultLocale_WhenCodePropertyExist() {

		try {
			repository.findById("1");
		} catch (ItemNotFoundException e) {

			String code = e.getCode().toString();

			String[] fields = (String[]) e.getFieldErrors().toArray();

			String mess = messageSource.getMessage(code, fields, new Locale("en", "EN"));

			Assert.assertEquals(getMessage(code, fields, resourcePathEnglish), mess);
		}
	}

	@Test
	public void getSpanishMessage_returnI18nMessage_WhenCodePropertyExist() {

		try {
			repository.findById("1");
		} catch (ItemNotFoundException e) {

			String code = e.getCode().toString();

			String[] fields = (String[]) e.getFieldErrors().toArray();

			String mess = messageSource.getMessage(code, fields, new Locale("es", "ES"));

			Assert.assertEquals(getMessage(code, fields, resourcePathSpanish), mess);
		}
	}

	@Test
	public void getRussianLanguageMessage_returnCode_WhenI18nFileNotExist() {

		try {
			repository.findById("1");
		} catch (ItemNotFoundException e) {

			String code = e.getCode().toString();

			String[] fields = (String[]) e.getFieldErrors().toArray();

			String mess = messageSource.getMessage(code, fields, new Locale("ru", "RU"));

			Assert.assertEquals(code, mess);
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
