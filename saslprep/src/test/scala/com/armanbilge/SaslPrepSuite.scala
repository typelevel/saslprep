/*
 * Copyright 2021 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2008-2018 MongoDB, Inc.
 * Copyright 2017 Tom Bentley
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel

import munit.FunSuite

class SaslPrepSuite extends FunSuite {
  import SaslPrep._

  test("rfc4013 examples") {
    // Taken from https://tools.ietf.org/html/rfc4013#section-3
    assertEquals("IX", saslPrepStored("I\u00ADX"))
    assertEquals("user", saslPrepStored("user"))
    assertEquals("user=", saslPrepStored("user="))
    assertEquals("USER", saslPrepStored("USER"))
    assertEquals("a", saslPrepStored("\u00AA"))
    assertEquals("IX", saslPrepStored("\u2168"))
    interceptMessage[IllegalArgumentException]("Prohibited character at position 0")(
      saslPrepStored("\u0007"))
    interceptMessage[IllegalArgumentException](
      "First character is RandALCat, but last character is not")(saslPrepStored("\u0627\u0031"))
  }

  test("mapped to space") {
    assertEquals("A B", saslPrepStored("A\u00A0B"))
  }

  test("bidi2") {
    // RandALCat character first *and* last is OK
    assertEquals("\u0627\u0031\u0627", saslPrepStored("\u0627\u0031\u0627"))
    // Both RandALCat character and LCat is not allowed
    interceptMessage[IllegalArgumentException](
      "Contains both RandALCat characters and LCat characters")(
      saslPrepStored("\u0627\u0041\u0627"))
  }

  test("unassigned") {
    val unassignedCodepoint = (Character.MIN_CODE_POINT to Character.MAX_CODE_POINT)
      .reverse
      .find(codepoint => !Character.isDefined(codepoint) && !SaslPrep.prohibited(codepoint))
      .get
    val withUnassignedChar = "abc" + new String(Character.toChars(unassignedCodepoint))
    assertEquals(withUnassignedChar, saslPrepQuery(withUnassignedChar))
    interceptMessage[IllegalArgumentException]("Character at position 3 is unassigned")(
      saslPrepStored(withUnassignedChar))
  }
}
