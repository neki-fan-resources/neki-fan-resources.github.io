package org.skyluc.neki_site

case class Config(
    isLocal: Boolean,
    baseUrl: String,
)

object Config {
  private val localConfig = Config(true, "http://neki-band:8080/")
  private val prodConfig = Config(false, "https://neki-fan-resources.github.io/")

  var cached: Config = null

  def current: Config = {
    if (cached == null) {
      if (System.getenv(PROPERTY_KEY_CONFIG_KEY) == "PROD") {
        cached = prodConfig
      } else {
        cached = localConfig
      }
    }
    cached
  }

  // -------
  val PROPERTY_KEY_CONFIG_KEY = "CONFIG_KEY"
}
