package org.skyluc.neki_site.data

import org.skyluc.fan_resources.data as fr
import org.skyluc.fan_resources.descriptor.ChoiceCategoryRepository

object ExtendedDatum {

  val categoryRepository = {
    val datumList = fr.Datum.choiceCategoryList
    ChoiceCategoryRepository(
      Seq(
        // datum ids
        fr.Id.choiceCategoryList,
        fr.ElementId.choiceCategoryList,
        fr.MultimediaId.choiceCategoryList,
        fr.ImageId.choiceCategoryList,
        fr.WebItemId.choiceCategoryList,
        fr.PostId.choiceCategoryList,
        // datums
        datumList.copy(descriptors = datumList.descriptors :+ Site.descriptor),
        fr.Element.choiceCategoryList,
        fr.Multimedia.choiceCategoryList,
        fr.Image.choiceCategoryList,
        fr.WebItem.choiceCategoryList,
        fr.Post.choiceCategoryList,
        fr.Media.choiceCategoryList,
        // other
        fr.DisplayBlock.choiceCategoryList,
        fr.DisplaySection.choiceCategoryList,
        fr.DisplaySubsection.choiceCategoryList,
        fr.LineSection.choiceCategoryList,
      ),
      fr.DataDescriptors.categoryRepository.elementFields,
      fr.FieldDescriptors.fieldRepository,
    )
  }
}
