package ru.dovzhikov.normalization.model

object Normalization {

  def meanAndStdev(l: List[Double]): (Double, Double) = {
    val len = l.length
    val mean = l.sum / len
    val std = math.sqrt(l.map(v => math.pow(v - mean, 2.0)).sum / (len - 1))
    (mean, std)
  }

  def minmax(l: List[Double]): List[Double] = {
    val mn = l.min
    val mx = l.max
    l map (v => (v - mn) / (mx - mn))
  }

  def standardScore(l: List[Double]): List[Double] = {
    val (m, sd) = meanAndStdev(l)
    l map (v => (v - m) / sd)
  }

  def sigmoid(l: List[Double]): List[Double] = {
    standardScore(l) map (v => 1 / (1 + math.exp(v)))
  }

  case class Method(f: List[Double] => List[Double], name: String) {
    override def toString: String = name
  }

  val methods: List[Method] = List(
    Method(minmax, "MinMax"),
    Method(standardScore, "SS"),
    Method(sigmoid, "Sigmoid")
  )

}
