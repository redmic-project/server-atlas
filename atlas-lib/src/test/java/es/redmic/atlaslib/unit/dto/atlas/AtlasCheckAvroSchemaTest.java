package es.redmic.atlaslib.unit.dto.atlas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.redmic.atlaslib.dto.atlas.LayerDTO;
import es.redmic.atlaslib.unit.utils.AtlasAvroBaseTest;
import es.redmic.atlaslib.unit.utils.LayerDataUtil;

public class AtlasCheckAvroSchemaTest extends AtlasAvroBaseTest {

	@Test
	public void serializeAndDeserialize_IsSuccessful_IfSchemaAndDataAreCorrect() {

		LayerDTO dto = LayerDataUtil.getLayer();

		Object result = serializerAndDeserializer(dto);

		assertTrue("El objeto obtenido debe ser una instancia de LayerDTO", LayerDTO.class.isInstance(result));

		assertEquals(result, dto);
	}
}
