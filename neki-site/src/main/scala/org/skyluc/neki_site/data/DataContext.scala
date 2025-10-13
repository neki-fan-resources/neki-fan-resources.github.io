package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr

trait NekiDataContext extends fr.DataContext {

  override def attributeDefinitions: fr.AttributesDefinitionContext = NekiAttributesDefinitionContext
}

object NekiAttributesDefinitionContext extends fr.DefaultAttributesDefinitionContext {}
