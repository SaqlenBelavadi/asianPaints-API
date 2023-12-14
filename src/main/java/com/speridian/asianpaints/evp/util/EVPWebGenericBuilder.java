package com.speridian.asianpaints.evp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author sony.lenka
 *
 * @param <T>
 */
public class EVPWebGenericBuilder<T> {

	private final Supplier<T> instantiator;

	private List<Consumer<T>> instanceModifier = new ArrayList<>();

	public EVPWebGenericBuilder(Supplier<T> instantiator) {
		this.instantiator = instantiator;
	}

	public static <T> EVPWebGenericBuilder<T> of(Supplier<T> instantiator) {
		return new EVPWebGenericBuilder<>(instantiator);
	}

	public <U> EVPWebGenericBuilder<T> with(BiConsumer<T, U> consumer, U value) {
		Consumer<T> co = instance -> consumer.accept(instance, value);
		instanceModifier.add(co);
		return this;
	}

	public T build() {
		T value = instantiator.get();
		instanceModifier.forEach(consumer -> consumer.accept(value));
		instanceModifier.clear();
		return value;
	}

}

