/*
 * Copyright (C) 2018 Marco Herrn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.poiu.fez;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Helper methods additional to the require...-methods in {@link java.util.Objects}.
 * <p>
 * The purpose of this class is to provide convenience methods for testing method parameters for
 * certain requirements and fail fast if the requirements are not fulfilled.
 *
 * @author Marco Herrn
 */
public class Require {

  /////////////////////////////////////////////////////////////////////////////
  //
  // Attributes

  /////////////////////////////////////////////////////////////////////////////
  //
  // Constructors

  /////////////////////////////////////////////////////////////////////////////
  //
  // Methods

  /**
   * Checks that the given string is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(String bar) {
   *     this.bar = Require.nonEmpty(bar);
   * }
   * </pre></blockquote>
   *
   * @param s the object reference to check for emptiness
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static String nonEmpty(final String s) {
    if (s.isEmpty()) {
      throw new IllegalArgumentException("Empty String is not allowed");
    }
    return s;
  }


  /**
   * Checks that the given string is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(String bar, String baz) {
   *     this.bar = Require.nonEmpty(bar, "bar must not be empty");
   *     this.baz = Require.nonEmpty(baz, "baz must not be empty");
   * }
   * </pre></blockquote>
   *
   * @param s the object reference to check for emptiness
   * @param message detail message to be used in the event that an {@code
   *                IllegalArgumentException} is thrown
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static String nonEmpty(final String s, final String message) {
    if (s.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return s;
  }


  /**
   * Checks that the given string is not all whitespace or empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(String bar) {
   *     this.bar = Require.nonWhitespace(bar);
   * }
   * </pre></blockquote>
   *
   * @param s the object reference to check for containing only whitespace
   * @return {@code s} if not only whitespace
   * @throws IllegalArgumentException if {@code s} is only whitespace
   */
  public static String nonWhitespace(final String s) {
    if (s.trim().isEmpty()) {
      throw new IllegalArgumentException("Empty String or String of only whitespace is not allowed");
    }
    return s;
  }


  /**
   * Checks that the given string is not all whitespace or empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(String bar) {
   *     this.bar = Require.nonWhitespace(bar, "bar must not be all whitespace or empty");
   *     this.baz = Require.nonWhitespace(baz, "baz must not be all whitespace or empty");
   * }
   * </pre></blockquote>
   *
   * @param s the object reference to check for containing only whitespace
   * @param message detail message to be used in the event that an {@code
   *                IllegalArgumentException} is thrown
   * @return {@code s} if not only whitespace
   * @throws IllegalArgumentException if {@code s} is only whitespace
   */
  public static String nonWhitespace(final String s, final String message) {
    if (s.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return s;
  }


  /**
   * Checks that the given collection is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(Collection&lt;String&gt; bar) {
   *     this.bar = Require.nonEmpty(bar);
   * }
   * </pre></blockquote>
   *
   * @param <T> the type of the collections elements
   * @param s the object reference to check for emptiness
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static <T> Collection<T> nonEmpty(final Collection<T> s) {
    if (s.isEmpty()) {
      throw new IllegalArgumentException("Empty Collection is not allowed");
    }
    return s;
  }


  /**
   * Checks that the given collection is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(Collection&lt;String&gt; bar, Collection&lt;Integer&gt; baz) {
   *     this.bar = Require.nonEmpty(bar, "bar must not be empty");
   *     this.baz = Require.nonEmpty(baz, "baz must not be empty");
   * }
   * </pre></blockquote>
   *
   * @param <T> the type of the collections elements
   * @param s the object reference to check for emptiness
   * @param message detail message to be used in the event that an {@code
   *                IllegalArgumentException} is thrown
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static <T> Collection<T> nonEmpty(final Collection<T> s, final String message) {
    if (s.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return s;
  }


  /**
   * Checks that the given array is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(final char[] bar) {
   *     this.bar = Require.nonEmpty(bar);
   * }
   * </pre></blockquote>
   *
   * @param s the array to check for emptiness
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static char[] nonEmpty(final char[] s) {
    if (s == null || s.length == 0) {
      throw new IllegalArgumentException("Empty Collection is not allowed");
    }
    return s;
  }


  /**
   * Checks that the given collection is not empty. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(Collection&lt;String&gt; bar, Collection&lt;Integer&gt; baz) {
   *     this.bar = Require.nononEmpty(bar, "bar must not be empty");
   *     this.baz = Require.nonEmpty(baz, "baz must not be empty");
   * }
   * </pre></blockquote>
   *
   * @param s the object reference to check for emptiness
   * @param message detail message to be used in the event that an {@code
   *                 IllegalArgumentException} is thrown
   * @return {@code s} if not empty
   * @throws IllegalArgumentException if {@code s} is empty
   */
  public static char[] nonEmpty(final char[] s, final String message) {
    if (s == null || s.length == 0) {
      throw new IllegalArgumentException(message);
    }
    return s;
  }


  /**
   * Checks that the given expression is true. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(final String foo, final Collection&lt;String&gt; bar) {
   *     Require.isTrue(bar.contains(foo));
   * }
   * </pre></blockquote>
   *
   * @param expression the result of the expression to check
   * @return the given <code>expression</code>
   * @throws IllegalArgumentException if {@code expression} is false
   */
  public static boolean isTrue(final boolean expression) {
    if (!expression) {
      throw new IllegalArgumentException("Expression must be true");
    }
    return expression;
  }


  /**
   * Checks that the given expression is true. This
   * method is designed primarily for doing parameter validation in methods
   * and constructors, as demonstrated below:
   * <blockquote><pre>
   * public Foo(final String foo, final Collection&lt;String&gt; bar) {
   *     Require.isTrue(bar.contains(foo));
   * }
   * </pre></blockquote>
   *
   * @param expression the result of the expression to check
   * @param message detail message to be used in the event that an {@code
   *                 IllegalArgumentException} is thrown
   * @return the given <code>expression</code>
   * @throws IllegalArgumentException if {@code expression} is false
   */
  public static boolean isTrue(final boolean expression, final String message) {
    if (!expression) {
      throw new IllegalArgumentException(message);
    }
    return expression;
  }


  /* * * The methods below just call the corresponding methods in java.util.Objects. * * */


  /**
   *
   * @see java.util.Objects#requireNonNull(java.lang.Object)
   */
  public static  <T> T  nonNull(final T obj) {
    return Objects.requireNonNull(obj);
  }


  /**
   *
   * @see java.util.Objects#requireNonNull(java.lang.Object, java.lang.String)
   */
  public static  <T> T  nonNull(final T obj, final String message) {
    return Objects.requireNonNull(obj, message);
  }


  /**
   *
   * @see java.util.Objects#requireNonNull(java.lang.Object, java.util.function.Supplier)
   */
  public static  <T> T  nonNull(final T obj, final Supplier<String> messageSupplier) {
    return Objects.requireNonNull(obj, messageSupplier);
  }
}
