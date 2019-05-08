package es.redmic.atlaslib.dto.atlas;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaIgnore;

import es.redmic.brokerlib.avro.common.CommonDTO;

public abstract class LayerBaseDTO extends CommonDTO {

	@JsonSchemaIgnore
	@Size(min = 1, max = 500)
	@NotNull
	private String name;

	public LayerBaseDTO() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerBaseDTO other = (LayerBaseDTO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
