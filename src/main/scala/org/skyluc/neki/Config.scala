package org.skyluc.neki

case class Config(
    isLocal: Boolean,
    baseUrl: String,
)

object Config {

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

  private val localConfig = Config(true, "http://neki-band:8080/")
  private val prodConfig = Config(false, "https://neki-fan-resources.github.io/")

  // -------
  val PROPERTY_KEY_CONFIG_KEY = "CONFIG_KEY"
}
