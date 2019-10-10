package org.ergoplatform.nodeView.wallet.scanning

import com.google.common.primitives.Longs
import org.ergoplatform.db.LDBKVStore
import org.ergoplatform.settings.Constants

import scala.util.Try

class ExternalApplicationStorage(store: LDBKVStore) {

  def addApplication(appReq: ExternalAppRequest): Try[ExternalApplication] = {
    val id = lastUsedId + 1
    appReq.toApp(id).flatMap { app =>
      Try(store.insert(Seq(Longs.toByteArray(id) -> ExternalApplicationSerializer.toBytes(app))))
        .map(_ => app)
    }
  }

  def removeApplication(id: Long): Unit = store.remove(Seq(Longs.toByteArray(id)))

  def allApplications: Seq[ExternalApplication] =
    store.getAll.map { case (_, v) => ExternalApplicationSerializer.parseBytes(v) }

  def lastUsedId: Long = Try(Longs.fromByteArray(store.lastKey())).getOrElse(Constants.DefaultAppId)

}