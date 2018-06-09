最近尝试了Python语言的开源命令行便捷工具库Google Fire，它是用来加速用户编写命令行程序的一个小工具库，该工具使用非常方便，节省了编写命令行程序繁琐的参数解析代码的时间。

但是我发现Java语言还缺少这样一个工具库，于是花了2天时间，将Google Fire移植到了Java语言上，命名为Startup。有了Startup，Java同学也可以从此不用解析繁琐的命令行参数了。

## 安装依赖

```xml
<dependency>
    <groupId>com.github.pyloque</groupId>
    <artifactId>startup</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 让Jedis秒变命令行
```java
import java.io.IOException;

import codehole.startup.Startup;
import redis.clients.jedis.Jedis;

public class Demo {

    public static void main(String[] args) {
        Startup.shell(new Jedis());
    }

}
```
使用maven将程序打包成jar包文件demo.jar，接下来就可以体验命令行版本的Jedis了。
```sh
$ java -jar demo.jar
> @set codehole superhero
OK
> @get codehole
superhero
> @keys *
[codehole]
> @del codehole
1
> @sadd books str[]:java,golang,python
1
> @sadd books str[]:java,golang,python
3
> @smembers books
[python, golang, java]
> @scard books
3
```
我们还可以使用单行命令模式，将shell方法改成fire即可
```java
Startup.fire(new Jedis(), args);  // 需要传递args参数
```
接下来，我们尝试一下单行命令的效果
```sh
$ java -jar demo.jar @set codehole superhero
OK
$ java -jar demo.jar @get codehole
superhero
$ java -jar demo.jar @keys "*"
[codehole]
$ java -jar demo.jar @del codehole
1
$ java -jar demo.jar @sadd books str[]:java,golang,python
3
$ java -jar demo.jar @smembers books
[python, golang, java]
$ java -jar demo.jar @scard books
3
```
Redis的set命令在Jedis里存在多个重载函数，很难记清楚具体有哪些参数。不过没关系，Startup提供了类自省功能，可以列出指定匹配模式的函数调用形式。
```sh
$ java -jar demo.jar ! @set
class: redis.clients.jedis.Jedis
    method: set(String arg0, String arg1, String arg2)
    method: set(String arg0, String arg1, String arg2, String arg3, int arg4)
    method: set(String arg0, String arg1)
    method: set(String arg0, String arg1, String arg2, String arg3, long arg4)
class: redis.clients.jedis.BinaryJedis
    method: set(byte[] arg0, byte[] arg1, byte[] arg2)
    method: set(byte[] arg0, byte[] arg1, byte[] arg2, byte[] arg3, int arg4)
    method: set(byte[] arg0, byte[] arg1, byte[] arg2, byte[] arg3, long arg4)
    method: set(byte[] arg0, byte[] arg1)
```
自省操作符是!，它连带祖先类一会自省了。当set方法有如此多时，如何调用具体哪个set方法呢？Startup使用参数数量和参数类型来匹配函数，如果有多个函数满足匹配条件，就使用第一个函数。比如现在要调用byte[]类型的set函数，我们可以这样写
```sh
> java -jar demo.jar @set byte[]:1,2,3 byte[]:10,20
OK
> java -jar demo.jar @get byte[]:1,2,3
[B@ae45eb6
```
因为输出是一个byte[]数组，所以显示的是它的toString()调用的结果。如果用户不满意输出效果，可以通过自定义```IConsole.print```方法来优化输出。
```java
Startup.fire(Object target, String[] args, IConsole console);
```

细节规则
--
1. 方法调用使用@前缀，如@set
2. 字段访问使用\$前缀，如$host
3. 方法调用如果遇到同样参数数量的方法不止一个，那么在传递参数时就必须携带参数类型前缀信息，便于startup确定具体应该调用哪个方法。如```@set str:codehole str:superhero```，表示调用两个参数都是String类型的set方法。如果不给予参数类型提示，那么自动使用第一个找到的方法，这可能不是你想要的。
4. startup的target参数可以是普通对象，也可以是类。比如```Startup.fire(Jedis.class, args)```，那么后续的参数作用对象就是Jedis.class。
5. 如果target是类，那么可以使用操作符+号对类进行实例化，后面跟着构造器的参数列表。比如Jedis有构造器```Jedis(String host, int port)```，那么就可以这样实例化```+ str:localhost int:6379```
6. 如果target是类，并且有默认构造器的话，那么如果要访问实例方法或者实例字段的话，就会对这个类进行自动实例化。如果没有默认构造器，这时候就会抛出异常。
7. 如果某个方法返回值为void，Startup会进行特殊处理，返回调用对象自身。 
8. 自省操作符!可以使用```! @```显示所有方法，可以使用```! $```显示所有字段，如果不带参数，等价于```! *```列出所有的字段和方法。像Jedis类里面实例方法特别多，如果全部列出来会让人崩溃，这时可以用第三个参数maxlen，表示最多显示多少行。比如```! @ 100```最多显示100行方法。还可以通过模式匹配\*号来查找方法，比如```! @*set*```匹配所有包行set字符串的方法列表。
9. 输入exit和q可以退出命令行shell。

实例演示
--
下面我们使用链式调用的例子来演示Startup的强大威力。首先我们编写一个Counter类，可以对该类里面的整形字段value进行incr和decr操作。然后使用Startup对它进行shell化。同时我们定一个coeff静态变量，用于放大自增自减的效果。
```java
import codehole.startup.Startup;

public class Demo {

    static class Counter {
        int value;
        static int coeff = 1;

        public Counter() {
            this(10);
        }

        public Counter(int value) {
            this.value = value;
        }

        public static int coeff() {
            return coeff;
        }
        
        public static void coeff(int c) {
            coeff = c;
        }

        public Counter incr() {
            return incr(1);
        }

        public Counter incr(int v) {
            this.value += v * coeff;
            return this;
        }

        public Counter decr() {
            return incr(1);
        }

        public Counter decr(int v) {
            this.value -= v * coeff;
            return this;
        }

        public int value() {
            return value;
        }
        
        public String toString() {
            return String.format("Counter(value=%d)", value);
        }

    }

    public static void main(String[] args) {
        Startup.shell(Counter.class); // 注意，这里的目标是类
    }

}
```
我们运行一下，体验一下Startup的神奇魅力。

**自省**
```sh
$ java -jar demo.jar
# 列出所有方法
> ! @
class: codehole.startup.demo.Demo.Counter
    method: value()
    method: decr()
    method: decr(int arg0)
    method: static coeff(int arg0)
    method: static coeff()
    method: incr(int arg0)
    method: incr()
# 列出所有字段
> ! $
class: codehole.startup.demo.Demo.Counter
    field: int value
    field: static int coeff
# 列出所有的incr方法
> ! @incr
class: codehole.startup.demo.Demo.Counter
    method: incr(int arg0)
    method: incr()
# 列出所有的decr方法
> ! @decr
class: codehole.startup.demo.Demo.Counter
    method: decr()
    method: decr(int arg0)
# 列出后缀为cr的方法
> ! @*cr
class: codehole.startup.demo.Demo.Counter
    method: decr()
    method: decr(int arg0)
    method: incr(int arg0)
    method: incr()
```

**构造器**
```sh
# 默认构造器
> +
Counter(value=10)
# 调用构造器Counter(int)
> + 5
Counter(value=5)
# 构造完继续获取实例字段
> + $value
10
> + 5 $value
5
# 构造完继续调用实例方法
> + @value
10
> + 5 @value
5
# 对类[访问实例字段或调用实例方法]会自动构造该类的默认实例，再回调方法或者访问实例字段
> $value
10
> @value
10
```

**静态字段和方法**
```sh
# 对类调用静态方法和对实例调用静态方法效果是一样的
> @coeff
1
> + @coeff
1
# 对类访问静态字段和对实例访问静态字段效果是一样的
> $coeff
1
> + $coeff
1
# 返回类型为void的方法会自动调整返回结果为调用对象
> @coeff 2
class codehole.startup.demo.Demo$Counter
> + @coeff 2
Counter(value=10)
> @coeff 2 $coeff
2
```

**链式调用**
```sh
> @incr @incr @incr 5 @incr 5 $value
22
> @incr 5 @decr 5 @incr 5 @decr 5 $value
10
> + @incr @incr @incr 5 @incr 5 $value
22
> + @incr 5 @decr 5 @incr 5 @decr 5 $value
10
```

仅支持有限的参数类型
--
Java的类型非常繁多，暂时无法支持所有的类型，目前仅对原生类型以及原生类型的包装类以及原生类型的数组做了支持，容器类型暂时还没有想到合适的方法来支持。

所有支持的参数类型如下，引用部分为对应的参数前缀名称
* byte short int long boolean char String
> byte short int long bool char str
* Byte Short Integer Long Boolean Character
> Byte Short Int Long Bool Char
* byte[] short[] int[] long[] boolean[] char[] String[]
> byte[] short[] int[] long[] bool[] char[] str[]
* Byte[] Short[] Integer[] Long[] Boolean[] Character[]
> Byte[] Short[] Int[] Long[] Bool[] Char[]

