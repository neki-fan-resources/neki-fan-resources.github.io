package org.skyluc.neki.yaml

import org.virtuslab.yaml.YamlCodec

case class Typing(`type`: String) derives YamlCodec

case class Iding(id: String) derives YamlCodec
