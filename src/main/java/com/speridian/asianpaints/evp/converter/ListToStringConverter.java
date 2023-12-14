package com.speridian.asianpaints.evp.converter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ListToStringConverter implements AttributeConverter<List<UUID>, String> {

	@Override
	public String convertToDatabaseColumn(List<UUID> attribute) {
		String columnValue = attribute.stream().map(uuid -> uuid.toString()).collect(Collectors.joining(","));

		return columnValue;
	}

	@Override
	public List<UUID> convertToEntityAttribute(String dbData) {

		List<UUID> uuidList = Arrays.asList(dbData.split(",")).stream().map(data -> UUID.fromString(data))
				.collect(Collectors.toList());
		return uuidList;
	}

}
