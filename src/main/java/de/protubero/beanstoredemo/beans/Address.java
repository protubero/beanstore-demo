package de.protubero.beanstoredemo.beans;

import de.protubero.beanstore.persistence.api.KryoConfig;
import de.protubero.beanstore.persistence.kryo.PropertyBeanSerializer;

@KryoConfig(id = 304, serializer = PropertyBeanSerializer.class)
public class Address {

	private String street2;
	private String city;
	
	public Address() {
	}
	
	public Address(String street, String city) {
		this.street2 = street;
		this.city = city;
	}

	public String getStreet2() {
		return street2;
	}

	public String getCity() {
		return city;
	}
}
