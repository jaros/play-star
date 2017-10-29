package controllers

import javax.inject.Inject

import play.api.libs.ws.{WSClient, WSRequest}

class CurrencyService @Inject() (ws: WSClient) {

  val latest: WSRequest = ws.url("http://api.fixer.io/latest")

}
