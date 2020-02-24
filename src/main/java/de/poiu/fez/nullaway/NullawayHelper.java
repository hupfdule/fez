/*
 * Copyright (C) 2020 Marco Herrn
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
package de.poiu.fez.nullaway;


/**
 * Helper methods to be used with NullAway.
 *
 * @author Marco Herrn
 */
public class NullawayHelper {

  /**
   * Cast a nullable value to a non-nullable one as described in
   * https://github.com/uber/NullAway/wiki/Suppressing-Warnings#downcasting.
   *
   * This methods actually just throw a NullPointerException if the given value is null.
   *
   * @param <T> the type of the nullable value
   * @param x the nullable value
   * @return the nullable value as a non-nullable value
   */
  public static <T> T castToNonNull(@Nullable final T x) {
    if (x == null) {
      throw new NullPointerException("Unexpected null value");
    }
    return x;
  }
}
