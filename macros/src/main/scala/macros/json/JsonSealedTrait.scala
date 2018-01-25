package macros.json

import scala.annotation.tailrec
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

import play.api.libs.json._

/**
  * Macro used to generate automatic readers and/or writers for a given sealed trait `T`. Every found implementation of
  * the sealed trait (direct or indirect) will use play-json automatic reader/writer generation (i.e. `Json.reads`
  * and/or `Json.writes`). The macros will handle case classes, objects, case objects, classes implementing a sealed
  * trait implementing `T` and every level of nested objects.
  *
  * For example with a given sealed trait `Test`:
  * {{{
  *
  *  sealed trait Test
  *
  *  case class TestA(value: Int) extends Test
  *  case class TestB(value: String) extends Test
  *  object TestC extends Test
  *  sealed trait Test2 extends Test
  *  case class Test2A(a: Int) extends Test2
  *  sealed trait Test3 extends Test2
  *  case class Test3A(a: String) extends Test3
  *
  *  object Test {
  *    case class TestD(value: String) extends Test
  *    object TestE extends Test
  *    case object TestF extends Test
  *
  *    object InnerTest {
  *      case object TestInner extends Test
  *    }
  *
  *     implicit val format: Format[Test] = format[Test]
  *
  *  }
  *
  *  object Hello extends App {
  *
  *    val testA = TestA(42)
  *    val testB = TestB("hello")
  *    val testC = TestC
  *    val testD = TestD("hey")
  *    val testE = TestE
  *    val testF = TestF
  *    val testI = TestInner
  *    val test2A = Test2A(42)
  *    val test3A = Test3A("42")
  *
  *    assert(Json.fromJson[Test](Json.toJson(testA)) == JsSuccess(testA))
  *    assert(Json.fromJson[Test](Json.toJson(testB)) == JsSuccess(testB))
  *    assert(Json.fromJson[Test](Json.toJson(testC)) == JsSuccess(testC))
  *    assert(Json.fromJson[Test](Json.toJson(testD)) == JsSuccess(testD))
  *    assert(Json.fromJson[Test](Json.toJson(testE)) == JsSuccess(testE))
  *    assert(Json.fromJson[Test](Json.toJson(testF)) == JsSuccess(testF))
  *    assert(Json.fromJson[Test](Json.toJson(testI)) == JsSuccess(testI))
  *    assert(Json.fromJson[Test](Json.toJson(test2A)) == JsSuccess(test2A))
  *    assert(Json.fromJson[Test](Json.toJson(test3A)) == JsSuccess(test3A))
  *
  *  }
  * }}}
  *
  */
object JsonSealedTrait {

  /**
    * Creates a `Reads[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Their respective readers will be automatically generated using the `Json.reads` macro.
    *
    * The provided JSON should have the name of the class in the `__type` field, or the deserialization will fail.
    *
    * @see [[play.api.libs.json.Json#reads() Json.reads]]
    */
  def reads[A]: Reads[A] = macro reads_impl_default[A]

  /**
    * Creates a `Reads[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Same as `JsonSealedTrait.reads[A]` but with a customisable type field.
    *
    * @param typeField Custom name of the JSON field containing the name.
    * @see [[macros.json.JsonSealedTrait#reads() JsonSealedTrait.reads]]
    * @see [[play.api.libs.json.Json#reads() Json.reads]]
    */
  def reads[A](typeField: String): Reads[A] = macro reads_impl[A]

  /**
    * Creates a `OWrites[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Their respective writes will be automatically generated using the `Json.writes` macro.
    *
    * The created JSON will have the name of the class in the `__type` field.
    *
    * @see [[play.api.libs.json.Json#writes() Json.writes]]
    */
  def writes[A]: OWrites[A] = macro writes_impl_default[A]

  /**
    * Creates a `OWrites[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Same as `JsonSealedTrait.writes[A]` but with a customisable type field.
    *
    * @param typeField Custom name of the JSON field containing the name.
    * @see [[macros.json.JsonSealedTrait#writes() JsonSealedTrait.writes]]
    * @see [[play.api.libs.json.Json#writes() Json.writes]]
    */
  def writes[A](typeField: String): OWrites[A] = macro writes_impl[A]

  /**
    * Creates a `OFormat[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Their respective writes will be automatically generated using the `Json.format` macro.
    *
    * The created JSON will have the name of the class in the `__type` field ; and the deserialized JSON should have
    * the name of the class in the `__type` field, or the deserialization will fail.
    *
    * @see [[play.api.libs.json.Json#format() Json.format]]
    */
  def format[A]: OFormat[A] = macro format_impl_default[A]

  /**
    * Creates a `OFormat[A]` by resolving, at compile-time, the classes or objects implementing the sealed family `A`.
    * Same as `JsonSealedTrait.format[A]` but with a customisable type field.
    *
    * @param typeField Custom name of the JSON field containing the name.
    * @see [[macros.json.JsonSealedTrait#format() JsonSealedTrait.format]]
    * @see [[play.api.libs.json.Json#format() Json.format]]
    */
  def format[A](typeField: String): OFormat[A] = macro format_impl[A]

  /**
    * @see [[macros.json.JsonSealedTrait#format_impl(scala.reflect.macros.blackbox.Context, scala.reflect.api.Exprs.Expr, scala.reflect.api.TypeTags.WeakTypeTag) JsonSealedTrait.format_impl]]
    */
  def format_impl_default[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[OFormat[A]] = {
    import c.universe._
    format_impl[A](c)(c.Expr[String](Literal(Constant("__type"))))
  }

  /**
    * @see [[macros.json.JsonSealedTrait#reads_impl(scala.reflect.macros.blackbox.Context, scala.reflect.api.Exprs.Expr, scala.reflect.api.TypeTags.WeakTypeTag) JsonSealedTrait.reads_impl]]
    */
  def reads_impl_default[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[Reads[A]] = {
    import c.universe._
    reads_impl[A](c)(c.Expr[String](Literal(Constant("__type"))))
  }

  /**
    * @see [[macros.json.JsonSealedTrait#writes_impl(scala.reflect.macros.blackbox.Context, scala.reflect.api.Exprs.Expr, scala.reflect.api.TypeTags.WeakTypeTag) JsonSealedTrait.writes_impl]]
    */
  def writes_impl_default[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[OWrites[A]] = {
    import c.universe._
    writes_impl[A](c)(c.Expr[String](Literal(Constant("__type"))))
  }

  /**
    * Macro used to generate the implicit format for a given sealed trait `A`. The generated code will reuse the reader
    * and writer macros
    *
    * @see [[JsonSealedTrait#reads_impl JsonSealedTrait.reads_impl]]
    * @see [[JsonSealedTrait#writes_impl JsonSealedTrait.writes_impl]]
    *
    */
  def format_impl[A: c.WeakTypeTag](c: blackbox.Context)(typeField: c.Expr[String]): c.Expr[OFormat[A]] = {
    import c.universe._

    val libs = q"_root_.play.api.libs"
    val json = q"$libs.json"

    val tree = q"$json.OFormat(${reads_impl[A](c)(typeField)}, ${writes_impl[A](c)(typeField)})"

    c.Expr[OFormat[A]](tree)
  }

  /**
    * Macro used to generate the implicit reader for a given sealed trait `A`. The generated code will be similar to...
    * {{{
    *
    *  case obj @ JsObject(_) => obj.value.get("__type") match {
    *    case Some(t) => t.validate[String].flatMap { dis =>
    *      dis match {
    *         case "ClassA" =>  // Case class
    *           val reader = Json.reads[ClassA]
    *           reader.reads(obj)
    *         case "ObjectB" =>  // Object
    *        JsSuccess(ObjectB)
    *      }
    *    }
    *    case _ => JsError(JsPath \ "__type", "error.missing.path")
    *  }
    *
    * }}}
    *
    * ...with a matching case clause for every implementation of `A` found.
    *
    */
  def reads_impl[A: c.WeakTypeTag](c: blackbox.Context)(typeField: c.Expr[String]): c.Expr[Reads[A]] = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol

    val libs   = q"_root_.play.api.libs"
    val json   = q"$libs.json"
    val JsPath = q"$json.JsPath"

    requireSealedTrait(c)(symbol)

    val subTypes = allSubTypes(c)(symbol)

    val tree = {
      val cases = Match(
        q"dis",
        subTypes.toList.map { t =>
          if (isCaseClass(c)(t)) {
            // We match on the name of the class, which should be present in the parsed element
            // under the `typeField` field
            cq"""
            ${t.name.decodedName.toString} =>
              val reader = $json.Json.reads[$t]
              reader.reads(obj)
            """
          } else {
            // We match on the name of the object, which should be present in the parsed element
            // under the `typeField` field. Since we are looking for a companion object, we don't need a reader
            cq"""
            ${t.name.decodedName.toString} =>
              $json.JsSuccess(${t.asClass.module})
            """
          }
        } :+ cq"""_ => $json.JsError("error.invalid")"""
      )

      q"""(_: $json.JsValue) match {
        case obj @ $json.JsObject(_) => obj.value.get($typeField) match {
           case Some(t) => t.validate[String].flatMap { dis => $cases }
           case _       => $json.JsError($JsPath \ $typeField, "error.missing.path")
        }
        case _ => $json.JsError("error.expected.jsobject")
      }"""
    }

    c.Expr[Reads[A]](tree)
  }

  /**
    * Macro used to generate the implicit writer for a given sealed trait `A`. The generated code will be similar to...
    * {{{
    *
    *  o match {
    *    case a: ClassA => // Case class
    *      val writer = Json.writes[ClassA]
    *      writer.writes(a) + ("__type" -> JsString("ClassA"))
    *    case _: ObjectB => // Object
    *      JsObject(Seq("__type" -> JsString("ObjectB"))
    *    case _ => throw new Exception(...) // Should not happen
    *  }
    *
    * }}}
    *
    * ...with a matching case clause for every implementation of `A` found.
    *
    */
  def writes_impl[A: c.WeakTypeTag](c: blackbox.Context)(typeField: c.Expr[String]): c.Expr[OWrites[A]] = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol

    val libs = q"_root_.play.api.libs"
    val json = q"$libs.json"

    // The write can only be derived if the target is a sealed trait
    requireSealedTrait(c)(symbol)

    val subTypes = allSubTypes(c)(symbol)

    val tree = {
      val cases = Match(
        q"o",
        subTypes.toList.map { t =>
          if (isCaseClass(c)(t)) {
            // For each sub-type we create a case clause in a match clause. This clause will also
            // define a derived writer using the play json `Json.writes` macro
            cq"""x: $t =>
              val writer = $json.Json.writes[$t]
              writer.writes(x) + ($typeField -> $json.JsString(${t.name.decodedName.toString}))"""
          } else {
            // If the element is a static object, we only serialize its type
            cq"""_: $t =>
              $json.JsObject(Seq($typeField -> $json.JsString(${t.name.decodedName.toString})))"""
          }
        } :+ cq"""_ => throw new Exception("Could not write abstract type") """
      )

      q"""{ o: $symbol =>
        $cases
      }"""
    }

    c.Expr[OWrites[A]](tree)
  }


  /** Will check that the provided symbol is a sealed trait, or fail the macro */
  private def requireSealedTrait(c: blackbox.Context)(symbol: c.Symbol): Unit = {
    if (!symbol.isClass) {
      c.abort(
        c.enclosingPosition,
        "Can only enumerate values of a sealed trait or class."
      )
    } else if (!symbol.asClass.isSealed) {
      c.abort(
        c.enclosingPosition,
        "Can only enumerate values of a sealed trait or class."
      )
    }
  }

  /** Will check that the provided class is a case class, or fail the macro */
  private def isCaseClass(c: blackbox.Context)(symbol: c.Symbol): Boolean = {
    symbol.isClass && symbol.asClass.isCaseClass && symbol.asClass.module == c.universe.NoSymbol
  }

  /** Get all the known sub types of a provided type (indirect or direct) */
  private def allSubTypes(c: blackbox.Context)(symbol: c.Symbol): Set[c.Symbol] = {

    @tailrec
    def allSubClasses(path: Traversable[c.Symbol], subClasses: Set[c.Symbol]): Set[c.Symbol] = path.headOption match {
      case Some(subSymbol) if subSymbol.isAbstract =>
        // Search subtypes
        allSubClasses(path.tail ++ subSymbol.asClass.knownDirectSubclasses, subClasses)
      case Some(subSymbol) =>
        allSubClasses(path.tail, subClasses ++ Set(subSymbol))
      case None =>
        subClasses
    }

    allSubClasses(symbol.asClass.knownDirectSubclasses, Set.empty)
  }

}
