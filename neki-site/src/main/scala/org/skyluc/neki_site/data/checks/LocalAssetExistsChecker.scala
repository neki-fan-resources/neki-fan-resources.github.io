package org.skyluc.neki_site.data.checks

import org.skyluc.fan_resources.data.Path
import org.skyluc.fan_resources.data.checks as fr
import org.skyluc.fan_resources.data.checks.CheckError
import org.skyluc.fan_resources.data.checks.LocalAssetExistsChecker
import org.skyluc.fan_resources.data.checks.LocalAssetExistsProcessor
import org.skyluc.neki_site.data.*

class LocalAssetExistsChecker(staticFolderPath: Path)
    extends fr.LocalAssetExistsChecker(LocalAssetExistsProcessor(staticFolderPath))

class LocalAssetExistsProcessor(staticFolderPath: Path)
    extends fr.LocalAssetExistsProcessor(staticFolderPath.resolve(org.skyluc.neki_site.html.Site.BASE_IMAGE_ASSET_PATH))
    with ProcessorMultimedia[Seq[CheckError]]
