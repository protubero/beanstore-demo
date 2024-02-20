package de.protubero.beanstoredemo.beans;

import com.esotericsoftware.kryo.kryo5.serializers.DefaultSerializers.EnumSerializer;

import de.protubero.beanstoredemo.framework.KryoConfig;

@KryoConfig(id=303, serializer=EnumSerializer.class)
public enum Priority {
	Tomorrow,
	Today,
	Upcoming,
	Someday
}
