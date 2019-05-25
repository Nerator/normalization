package ru.dovzhikov.normalization.model

import java.io.File

import scala.io.Source

class CSV private(val file: File,
                  val header: Boolean,
                  val sep: Char,
                  val dec: Char,
                  val enc: String) {

  def parse: Seq[CSV.Column] = {
    val source = Source.fromFile(file, enc)
    try {
      val lines = source.getLines().toSeq
      val heads = if (header) lines.head.split(sep).map(Some.apply).toSeq
                  else Seq.fill(lines.head.length)(None)
      val rows = (if (header) lines.tail else lines) map (_.split(sep).map(_.toDouble).toSeq)
      val cols = CSV.getColumns(rows)
      (heads, cols).zipped map {
        case (h,c) => CSV.Column(h, c)
      }
    } finally {
      source.close()
    }
  }

}

object CSV {

  def apply(file: File, header: Boolean, sep: Char, dec: Char, enc: String): CSV = new CSV(file, header, sep, dec, enc)
  def apply(file: File): CSV = CSV(file, header = true, ',', '.', "UTF-8")
  def apply(file: File, header: Boolean): CSV = CSV(file, header, ',', '.', "UTF-8")

  final case class CSVOptions(header: Boolean, sep: Char, dec: Char, enc: String)
  def apply(file: File, opts: CSVOptions): CSV = CSV(file, opts.header, opts.sep, opts.dec, opts.enc)

  final case class Column(header: Option[String], data: Seq[Double])

  private def getColumns(rows: Seq[Seq[Double]]): List[List[Double]] = {
    if (rows.exists(_.isEmpty)) List.empty
    else {
      val col = rows map (_.head)
      col.toList :: getColumns(rows map (_.tail))
    }
  }

}
