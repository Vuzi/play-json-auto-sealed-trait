package example

import macros.json.JsonSealedTrait.format
import example.Test.InnerTest.TestInner
import example.Test.{TestD, TestE, TestF}
import play.api.libs.json._

sealed trait Test

case class TestA(value: Int) extends Test

case class TestB(value: String) extends Test

object TestC extends Test

sealed trait Test2 extends Test

case class Test2A(a: Int) extends Test2

sealed trait Test3 extends Test2

case class Test3A(a: String) extends Test3

object Test {
  case class TestD(value: String) extends Test

  object TestE extends Test

  case object TestF extends Test

  object InnerTest {
    case object TestInner extends Test
  }

  implicit val writer: Format[Test] = format[Test]
}

object MacroTest extends App {

  val testA = TestA(42)
  val testB = TestB("hello")
  val testC = TestC
  val testD = TestD("hey")
  val testE = TestE
  val testF = TestF
  val testI = TestInner
  val test2A = Test2A(42)
  val test3A = Test3A("42")

  assert(Json.fromJson[Test](Json.toJson(testA)) == JsSuccess(testA))
  assert(Json.fromJson[Test](Json.toJson(testB)) == JsSuccess(testB))
  assert(Json.fromJson[Test](Json.toJson(testC)) == JsSuccess(testC))
  assert(Json.fromJson[Test](Json.toJson(testD)) == JsSuccess(testD))
  assert(Json.fromJson[Test](Json.toJson(testE)) == JsSuccess(testE))
  assert(Json.fromJson[Test](Json.toJson(testF)) == JsSuccess(testF))
  assert(Json.fromJson[Test](Json.toJson(testI)) == JsSuccess(testI))
  assert(Json.fromJson[Test](Json.toJson(test2A)) == JsSuccess(test2A))
  assert(Json.fromJson[Test](Json.toJson(test3A)) == JsSuccess(test3A))

}

