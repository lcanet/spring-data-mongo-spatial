package org.tekila.datamongo.spatial;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

/**
 * Holder of geometryconverters
 * 
 * @author lc
 *
 */
public final class GeometryConverters {

	private GeometryConverters() {
		
	}
	
	/**
	 * Return a list of all converters needed to handle geometric data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final List<Converter> geometryConverters() {
		return Arrays.asList((Converter)GeometryReadConverter.INSTANCE, GeometryWriteConverter.INSTANCE);
	}
	
}
