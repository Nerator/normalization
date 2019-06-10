package ru.dovzhikov.normalization.model

import org.scalacheck.Gen
import org.scalatest._
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class NormalizationSpec extends FlatSpec with Matchers with ScalaCheckDrivenPropertyChecks {

  private val EPS = 1e-8

  // Генератор списков вещественных чисел для тестирования свойств
  // Списки длины от 2 до 1000, со значениями в диапазонах [-1e150, -1e-150],
  // {0.0} и [1e-150, 1e150] с ненулевой дисперсией
  private val listGen = for {
    numElems <- Gen.chooseNum(2, 1000)
    neg = Gen.chooseNum(-1e150, -1e-150)
    zero = Gen.const(0.0)
    pos = Gen.chooseNum(1e-150, 1e150)
    list <- Gen.listOfN(numElems, Gen.oneOf(pos, zero, neg))
      if list.min !== list.max +- EPS
  } yield list

  val values = List(
    0.4765201686,
    0.4605294280,
    0.4642153644,
    0.4606346127,
    0.4572916463,
    0.4563134491,
    0.4517266463,
    0.4368786344,
    0.4451630367,
    0.4610855447,
    0.4400023448,
    0.4450459000,
    0.4547204833,
    0.4575295299,
    0.4474408518,
    0.4555937432,
    0.4641753673,
    0.4527843197,
    0.4648059116,
    0.4516485914,
    0.4702209006,
    0.4511725226,
    0.4534821532,
    0.4517309009,
    0.4530252038,
    0.4662913796,
    0.4482275763,
    0.4437257439,
    0.4508725854,
    0.4590678679,
    0.4515116014,
    0.4600219452,
    0.4608939957,
    0.4552172878,
    0.4458871283,
    0.4395376622,
    0.4615977325,
    0.3991398847,
    0.4345492267,
    0.4041056026,
    0.4421213712,
    0.4618093217,
    0.3960818189,
    0.4261070562
  )

  "meanAndVariance" should "work correctly" in {
    val (mean, variance) = Normalization.meanAndVariance(values)
    mean shouldEqual 0.449784182806818 +- EPS
    variance shouldEqual 0.000282790874245 +- EPS // Sample LOCalc: VAR
    //variance shouldEqual 0.000276363808921 +- EPS // Population LOCalc: VAR.P
  }

  "minmax" should "work correctly" in {
    val nvalues = Normalization.minmax(values)
    val expected = List(
      1.0,
      0.8012050144,
      0.8470281377,
      0.8025126583,
      0.7609532969,
      0.7487924662,
      0.6917698792,
      0.5071811604,
      0.610171865,
      0.8081185908,
      0.5460147567,
      0.6087156351,
      0.7289889044,
      0.7639106381,
      0.6384893918,
      0.739845168,
      0.8465308985,
      0.7049187487,
      0.85436975,
      0.6907995094,
      0.9216882494,
      0.6848810791,
      0.7135941325,
      0.6918227721,
      0.7079133914,
      0.8728369116,
      0.6482698569,
      0.592303611,
      0.6811522948,
      0.7830350732,
      0.6890964665,
      0.7948960487,
      0.8057372771,
      0.7351651186,
      0.6191736858,
      0.5402378784,
      0.8144860485,
      0.0380175116,
      0.4782222403,
      0.0997507259,
      0.5723582407,
      0.8171165007,
      0.0,
      0.373270181
    )

    nvalues.length shouldEqual expected.length
    (nvalues, expected).zipped.foreach {
      case (n, e) => n shouldEqual e +- EPS
    }
  }

  it should "have minimum of 0 and maximum of 1 given list with at least 2 elements" in {
    forAll(listGen) { v =>
      val normed = Normalization.minmax(v)
      normed.min shouldEqual 0.0
      normed.max shouldEqual 1.0
    }
  }

  it should "have values between 0 and 1 inclusive given list with at least 2 elements" in {
    forAll(listGen) { v =>
      Normalization.minmax(v) foreach { n =>
        n should be >= 0.0
        n should be <= 1.0
      }
    }
  }

  "standardScore" should "work correctly" in {
    val nvalues = Normalization.standardScore(values)
    // Sample variance (Stddev)
    val expected = List(
      1.5898769248,
      0.6389746586,
      0.8581618359,
      0.6452295528,
      0.4464373609,
      0.3882680791,
      0.1155101573,
      -0.7674388292,
      -0.2748001738,
      0.6720445846,
      -0.5816848746,
      -0.2817658038,
      0.2935410834,
      0.4605833031,
      -0.139348063,
      0.3454701881,
      0.8557833764,
      0.1784055554,
      0.8932791994,
      0.1108685568,
      1.2152858814,
      0.0825587443,
      0.2199027896,
      0.1157631614,
      0.1927299233,
      0.9816137509,
      -0.0925648587,
      -0.3602699514,
      0.064722734,
      0.5520618114,
      0.1027223388,
      0.6087967859,
      0.6606539722,
      0.3230839615,
      -0.2317414833,
      -0.6093175973,
      0.702502244,
      -3.0116039697,
      -0.9059589362,
      -2.7163135569,
      -0.4556752642,
      0.7150845684,
      -3.1934543208,
      -1.4079794009
    )
    // Population variance (Stddev)
    // val expected = DenseVector(
    //   1.6082576155 ,
    //   0.6463618943 ,
    //   0.8680831116 ,
    //   0.6526891018 ,
    //   0.4515986579 ,
    //   0.3927568765 ,
    //   0.1168455792 ,
    //   -0.7763112492,
    //   -0.27797716  ,
    //   0.6798141442 ,
    //   -0.5884097787,
    //   -0.2850233202,
    //   0.2969347347 ,
    //   0.4659081424 ,
    //   -0.1409590767,
    //   0.3494641958 ,
    //   0.8656771546 ,
    //   0.1804681159 ,
    //   0.9036064697 ,
    //   0.1121503168 ,
    //   1.229335896  ,
    //   0.0835132124 ,
    //   0.2224451029 ,
    //   0.1171015083 ,
    //   0.1949580889 ,
    //   0.9929622638 ,
    //   -0.0936350082,
    //   -0.3644350603,
    //   0.0654709986 ,
    //   0.5584442409 ,
    //   0.1039099197 ,
    //   0.6158351328 ,
    //   0.6682918441 ,
    //   0.3268191603 ,
    //   -0.2344206646,
    //   -0.6163619654,
    //   0.7106239271 ,
    //   -3.0464213573,
    //   -0.9164327979,
    //   -2.7477170691,
    //   -0.4609433614,
    //   0.7233517167 ,
    //   -3.2303740944,
    //   -1.4242571601
    // )

    nvalues.length shouldEqual expected.length
    (nvalues, expected).zipped.foreach {
      case (n, e) => n shouldEqual e +- EPS
    }
  }

  it should "have zero mean and unit variance given list with at least 2 elements" in {
    forAll(listGen) { v =>
      val normed = Normalization.standardScore(v)
      val (mean, variance) = Normalization.meanAndVariance(normed)
      mean shouldEqual 0.0 +- EPS
      variance shouldEqual 1.0 +- EPS
    }
  }

  "sigmoid" should "work correctly" in {
    val nvalues = Normalization.sigmoid(values)
    // Sample variance (Stddev)
    val expected = List(
      0.830598786749606,
      0.654521644062448,
      0.702276466023591,
      0.655934649293416,
      0.60979185185966,
      0.595865704971478,
      0.528845473320888,
      0.317033399185054,
      0.431729040794474,
      0.661960824977037,
      0.35854499625712,
      0.430020919211573,
      0.57286282709325,
      0.613152542555385,
      0.465219246148562,
      0.585518681863256,
      0.701778928458609,
      0.544483464661734,
      0.709566420504479,
      0.527688783251235,
      0.771232888354311,
      0.52062797070143,
      0.554755223563027,
      0.528908513150125,
      0.548033888629873,
      0.727428302416176,
      0.476875294296077,
      0.410894220571596,
      0.516175038093467,
      0.634613814359491,
      0.525658026710967,
      0.647666283732786,
      0.659407278586627,
      0.580075653350538,
      0.442322524580712,
      0.352214878379359,
      0.668742318697595,
      0.046904388996034,
      0.287827475618839,
      0.062017565784432,
      0.388012271716248,
      0.67152368855989,
      0.039412792809988,
      0.196552953438361
    )

    nvalues.length shouldEqual expected.length
    (nvalues, expected).zipped.foreach {
      case (n, e) => n shouldEqual e +- EPS
    }
  }

  it should "have values between 0 and 1 given list with at least 2 elements" in {
    forAll(listGen) { v =>
      Normalization.sigmoid(v) foreach { n =>
        n should be > 0.0
        n should be < 1.0
      }
    }
  }

}
