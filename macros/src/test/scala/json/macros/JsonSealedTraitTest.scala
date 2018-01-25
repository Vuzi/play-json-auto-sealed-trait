package json.macros

import macros.json._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
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

}

class JsonSealedTraitTest extends WordSpec with ScalaFutures with MustMatchers {

  val testA  = TestA(42)
  val testB  = TestB("hello")
  val testC  = TestC
  val testD  = Test.TestD("hey")
  val testE  = Test.TestE
  val testF  = Test.TestF
  val testI  = Test.InnerTest.TestInner
  val test2A = Test2A(42)
  val test3A = Test3A("42")

  "JsonSealedTrait" when {

    "used with 'writes'" should {

      "generate sealed trait's writer" in {

        implicit val writer = JsonSealedTrait.writes[Test]

        // Compilation is the real test here

        Json.toJson(testA)
        Json.toJson(testB)
        Json.toJson(testC)
        Json.toJson(testD)
        Json.toJson(testE)
        Json.toJson(testF)
        Json.toJson(testI)
        Json.toJson(test2A)
        Json.toJson(test3A)

      }

      "generate sealed trait's writer with custom type field" in {

        implicit val writer = JsonSealedTrait.writes[Test]("custom_type")

        // Compilation is the real test here

        Json.toJson(testA)
        Json.toJson(testB)
        Json.toJson(testC)
        Json.toJson(testD)
        Json.toJson(testE)
        Json.toJson(testF)
        Json.toJson(testI)
        Json.toJson(test2A)
        Json.toJson(test3A)

      }

    }

    "used with 'reads'" should {

      "generate sealed trait's reader" in {

        implicit val writer = JsonSealedTrait.writes[Test]
        implicit val reader = JsonSealedTrait.reads[Test]

        Json.fromJson[Test](Json.toJson(testA)) mustBe JsSuccess(testA)
        Json.fromJson[Test](Json.toJson(testB)) mustBe JsSuccess(testB)
        Json.fromJson[Test](Json.toJson(testC)) mustBe JsSuccess(testC)
        Json.fromJson[Test](Json.toJson(testD)) mustBe JsSuccess(testD)
        Json.fromJson[Test](Json.toJson(testE)) mustBe JsSuccess(testE)
        Json.fromJson[Test](Json.toJson(testF)) mustBe JsSuccess(testF)
        Json.fromJson[Test](Json.toJson(testI)) mustBe JsSuccess(testI)
        Json.fromJson[Test](Json.toJson(test2A)) mustBe JsSuccess(test2A)
        Json.fromJson[Test](Json.toJson(test3A)) mustBe JsSuccess(test3A)

      }

      "generate sealed trait's reader with custom type field" in {

        implicit val writer = JsonSealedTrait.writes[Test]("custom_type")
        implicit val reader = JsonSealedTrait.reads[Test]("custom_type")

        Json.fromJson[Test](Json.toJson(testA)) mustBe JsSuccess(testA)
        Json.fromJson[Test](Json.toJson(testB)) mustBe JsSuccess(testB)
        Json.fromJson[Test](Json.toJson(testC)) mustBe JsSuccess(testC)
        Json.fromJson[Test](Json.toJson(testD)) mustBe JsSuccess(testD)
        Json.fromJson[Test](Json.toJson(testE)) mustBe JsSuccess(testE)
        Json.fromJson[Test](Json.toJson(testF)) mustBe JsSuccess(testF)
        Json.fromJson[Test](Json.toJson(testI)) mustBe JsSuccess(testI)
        Json.fromJson[Test](Json.toJson(test2A)) mustBe JsSuccess(test2A)
        Json.fromJson[Test](Json.toJson(test3A)) mustBe JsSuccess(test3A)

      }

    }

    "used with 'format'" should {

      "generate sealed trait's format" in {

        implicit val format = JsonSealedTrait.format[Test]

        Json.fromJson[Test](Json.toJson(testA)) mustBe JsSuccess(testA)
        Json.fromJson[Test](Json.toJson(testB)) mustBe JsSuccess(testB)
        Json.fromJson[Test](Json.toJson(testC)) mustBe JsSuccess(testC)
        Json.fromJson[Test](Json.toJson(testD)) mustBe JsSuccess(testD)
        Json.fromJson[Test](Json.toJson(testE)) mustBe JsSuccess(testE)
        Json.fromJson[Test](Json.toJson(testF)) mustBe JsSuccess(testF)
        Json.fromJson[Test](Json.toJson(testI)) mustBe JsSuccess(testI)
        Json.fromJson[Test](Json.toJson(test2A)) mustBe JsSuccess(test2A)
        Json.fromJson[Test](Json.toJson(test3A)) mustBe JsSuccess(test3A)

      }

      "generate sealed trait's format with custom type field" in {

        implicit val format = JsonSealedTrait.format[Test]("custom_type")

        Json.fromJson[Test](Json.toJson(testA)) mustBe JsSuccess(testA)
        Json.fromJson[Test](Json.toJson(testB)) mustBe JsSuccess(testB)
        Json.fromJson[Test](Json.toJson(testC)) mustBe JsSuccess(testC)
        Json.fromJson[Test](Json.toJson(testD)) mustBe JsSuccess(testD)
        Json.fromJson[Test](Json.toJson(testE)) mustBe JsSuccess(testE)
        Json.fromJson[Test](Json.toJson(testF)) mustBe JsSuccess(testF)
        Json.fromJson[Test](Json.toJson(testI)) mustBe JsSuccess(testI)
        Json.fromJson[Test](Json.toJson(test2A)) mustBe JsSuccess(test2A)
        Json.fromJson[Test](Json.toJson(test3A)) mustBe JsSuccess(test3A)

      }

    }

  }

}
