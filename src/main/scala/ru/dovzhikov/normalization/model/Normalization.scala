package ru.dovzhikov.normalization.model

object Normalization {

  def meanAndStdev(l: Seq[Double]): (Double, Double) = {
    val len = l.length
    val mean = l.sum / len
    val std = math.sqrt(l.map(v => math.pow(v - mean, 2.0)).sum / (len - 1))
    (mean, std)
  }

  def minmax(l: Seq[Double]): Seq[Double] = {
    val mn = l.min
    val mx = l.max
    l map (v => (v - mn) / (mx - mn))
  }

  def standardScore(l: Seq[Double]): Seq[Double] = {
    val (m, sd) = meanAndStdev(l)
    l map (v => (v - m) / sd)
  }

  def sigmoid(l: Seq[Double]): Seq[Double] = {
    standardScore(l) map (v => 1 / (1 + math.exp(-v)))
  }

  case class Method(f: Seq[Double] => Seq[Double], name: String) {
    override def toString: String = name
  }

  val methods: Seq[Method] = Seq(
    Method(identity, "Без нормирования"),
    Method(minmax, "Минимакс"),
    Method(standardScore, "Стандартизация"),
    Method(sigmoid, "Сигмоида (логистическая функция)")
  )

}
