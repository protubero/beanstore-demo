package de.protubero.beanstoredemo.commands;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.protubero.beanstore.api.BeanStore;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "command")
@JsonSubTypes({
    @JsonSubTypes.Type(value = UpperCaseTodoText.class, name = "upper"),
    @JsonSubTypes.Type(value = LowerCaseTodoText.class, name = "lower") }
)
public abstract class AbstractCommand implements Consumer<BeanStore> {

}
