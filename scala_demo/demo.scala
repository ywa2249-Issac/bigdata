package demo

/*
    object关键字：声明一个单例对象
 */

object demo {

  //这里打印倒向九九乘法口诀表
  /*指令风格的编程实现九九乘法表*/
  def printMultiTable() {
    var i = 1 //这里只有i在作用范围内
    while (i <= 9) {
      var j = i //这里只有i和j在作用范围内
      while (j <= 9) {
        val prod = (i * j).toString()
        var k = prod.length()
        while (k < 4) {
          print(" ")
          k += 1
        }
        print(i + "*" + j + "=" + prod)
        j += 1
      }
      println()
      i += 1
    }
  }

  def main(args: Array[String]): Unit = {
    printMultiTable()
  }
}
