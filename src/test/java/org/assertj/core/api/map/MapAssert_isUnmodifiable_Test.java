package org.assertj.core.api.map;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.error.ShouldBeUnmodifiable;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.error.ShouldNotBeNull.shouldNotBeNull;
import static org.assertj.core.util.AssertionsUtil.expectAssertionError;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MapAssert_isUnmodifiable_Test {

  @Test
  void should_fail_if_actual_is_null() {
    // GIVEN
    Map<?, ?> actual = null;
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> assertThat(actual).isUnmodifiable());
    // THEN
    then(assertionError).hasMessage(shouldNotBeNull().create());
  }

  @ParameterizedTest
  @MethodSource("unmodifiableMaps")
  void should_pass(Map<?, ?> actual) {
    //WHEN/THEN
    assertThatNoException().as(actual.getClass().getName())
                           .isThrownBy(() -> assertThat(actual).isUnmodifiable());
  }

  private static Stream<Map<?,?>> unmodifiableMaps() {
    return Stream.of(
      Collections.unmodifiableMap(new HashMap<>()),
      ImmutableMap.of(),
      ImmutableMap.copyOf(Maps.newHashMap("key", "value")),
      UnmodifiableMap.unmodifiableMap(new HashMap<>()), //This does not override the Map.compute, computeIfAbsent, computeIfPresent, merge, putIfAbsent and other java 8 methods
      UnmodifiableMap.unmodifiableMap(Maps.newHashMap("key", "value"))
    );
  }

  @ParameterizedTest
  @MethodSource("modifiableMaps")
  void should_fail_if_actual_can_be_modified(Map<?, ?> actual, ErrorMessageFactory errorMessageFactory) {
    //WHEN
    AssertionError assertionError = expectAssertionError(() -> assertThat(actual).isUnmodifiable());
    //THEN
    then(assertionError).as(actual.getClass().getName())
                        .hasMessage(errorMessageFactory.create());
  }

  private static Stream<Arguments> modifiableMaps() {
    return Stream.of(
      arguments(new HashMap<>(), ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.compute(null, (key, value) -> value)")),
      arguments(Maps.newHashMap("key", "value"),
                ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.compute(null, (key, value) -> value)")),
      arguments(new Hashtable<>(), ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.compute(null, (key, value) -> value)")),
      arguments(new LinkedHashMap<>(), ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.compute(null, (key, value) -> value)")),
      arguments(new TreeMap<>(), ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.compute(null, (key, value) -> value)")),
      arguments(new EnumMap<>(TestEnum.class), ShouldBeUnmodifiable.shouldBeUnmodifiable("Map.clear()"))
    );
  }

  private enum TestEnum {
    TEST1,
    TEST2;
  }
}

