package dev.rth
package zioplayground

final class ExampleSuite extends TestSuite:
  test("hello world") {
    1 `shouldBe` 1
  }
