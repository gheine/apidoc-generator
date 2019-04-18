package scala.models.play.files

import io.apibuilder.generator.v0.models.InvocationForm

import scala.models.{Config, DateTimeTypeConfig, DateTypeConfig, Play2Models}

object ModelsJson {

  private def implicits(ssd: scala.generator.ScalaService) = Seq(Play2Models.timeImplicits) ++
    (ssd.config.dateTimeType match {
      case DateTimeTypeConfig.JodaDateTime => Seq(Play2Models.jodaDateTimeImplicits)
      case _ => Nil
    }) ++
    (ssd.config.dateType match {
      case DateTypeConfig.JodaLocalDate => Seq(Play2Models.jodaLocalDateImplicits)
      case _ => Nil
    })

  def contents(form: InvocationForm): String = {
    val scalaService = scala.generator.ScalaService(form.service, Config(form.attributes, Config.PlayGen2DefaultConfig))
    val imports = scala.models.JsonImports(form.service)
    val gen = scala.models.Play2Json(scalaService)

    s"""
      package ${scalaService.namespaces.models}

      package object json {

        import play.api.libs.json.{__, JsString, Writes}
        import play.api.libs.functional.syntax._

        ${implicits(scalaService).mkString("\n")}

        ${imports.mkString("\n")}

        ${gen.generateEnums()}
        ${gen.generateModelsAndUnions()}

      }
    """
  }

}
