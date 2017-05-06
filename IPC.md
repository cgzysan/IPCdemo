> Inter-Process Communication  进程间通信

其中涉及了`Service`，先复习一波`Service`

### Service ###
> 与其他的引用组件形成一些联系，从而可以根据其传来的信息在合适的时候执行合适的操作。

一般来说，联系分为两种：startService()以及bindService()。两个方法都可以使一个`Service`开始运行，但是还有诸多不同。

#### startService ####
1. 启动方式 ： 在其他组件中调用`startService()`方法后，服务即处于启动状态。</br>
2. 停止方式 ： service中调用stopSelf()，或者其他组件调用stopService()方法后，服务将停止。</br>
3. 通信方式 ： 没有提供默认的通信方式，启动service后该service就处于独立运行状态。</br>
4. 生命周期 ： 一旦启动，service即可在后台无限期运行，即使启动service的组件已经被销毁也不受影响，知道其被终止。</br>

#### bindService ####
1. 启动方式 ： `bindService()`方法后，服务即处于启动状态。
2. 停止方式 ： 所有与service绑定的组件都被销毁，或者他们都调用了`unbindService()`方法后，service将停止运行。
3. 可以通过 ServiceConnection进行通信，组件可以与service进行交互、发送请求、获取结果，甚至是利用IPC跨进程执行这些操作。
4. 当所有与其绑定的组件都取消绑定(可能是组件被销毁也有可能是其调用了unbindService()方法)后，service将停止。

### 创建一个Service ###
创建一个类继承Service或者他的子类，重写其中的一些关键方法，同时在Manifests文件里面为其声明，根据需要配置一些其他属性。

* onCreate() <br>
	在每个service的生命周期中这个方法会且仅会调用一次，并且它的调用在onStartCommand()以及onBond()之前，我们可以在这个方法中进行一些一次性的初始化工作。
* onStartCommand() <br>
	当其他组件通过startService()方法启动service时，此方法将会被调用。
* onBind() <br>
	当其他组件通过bindService()方法与service相绑定之后，此方法将会被调用。这个方法有一个IBinder的返回值，这意味着在重写它的时候必须返回一个IBinder对象，它是用来支撑其他组件与service之间的通信的——另外，如果你不想让这个service被其他组件所绑定，可以通过在这个方法返回一个null值来实现。
* onDestroy() <br>
	这是service一生中调用的最后一个方法，当这个方法被调用之后，service就会被销毁。所以我们应当在这个方法里面进行一些资源的清理，比如注册的一些监听器什么的。	

在Manifests文件里进行声明的时候，只有android:name属性是必须要有的，其他的属性都可以没有。

	<service  android:enabled=["true" | "false"] android:exported=["true" | "false"] android:icon="drawable resource" android:isolatedProcess=["true" | "false"] android:label="string resource" android:name="string" android:permission="string" android:process="string" >
	</service>

* android:enabled : 如果为true，则这个service可以被系统实例化，如果为false，则不行。默认为true
* android:exported : 如果为true，则其他应用的组件也可以调用这个service并且可以与它进行互动，如果为false，则只有与service同一个应用或者相同user ID的应用可以开启或绑定此service。它的默认值取决于service是否有intent filters。如果一个filter都没有，就意味着只有指定了service的准确的类名才能调用，也就是说这个service只能应用内部使用——其他的应用不知道它的类名。这种情况下exported的默认值就为false。反之，只要有了一个filter，就意味着service是考虑到外界使用的情况的，这时exported的默认值就为true
* android:icon : 一个象征着这个service的icon
* android:isolatedProcess : 如果设置为true，这个service将运行在一个从系统中其他部分分离出来的特殊进程中，我们只能通过Service API来与它进行交流。默认为false。
* android:label : 显示给用户的这个service的名字。如果不设置，将会默认使用<application>的label属性。
* android:name : 这个service的路径名，例如“com.lypeer.demo.MyService”。这个属性是唯一一个必须填的属性。
* android:permission : 其他组件必须具有所填的权限才能启动这个service。
* android:process : service运行的进程的name。默认启动的service是运行在主进程中的。

### bindService() ###
>这是一种比startService更复杂的启动方式，同时使用这种方式启动的service也能完成更多的事情，比如其他组件可向其发送请求，接受来自它的响应，甚至通过它来进行IPC等等。我们通常将绑定它的组件成为客户端，而称它为服务器。

>如果要创建一个支持绑定的service，我们必须要重写它的onBind()方法。这个方法会返回一个IBinder对象，它是客户端用来和服务器进行交互的接口。而要得到IBinder接口，我们通常有三种方式：继承Binder类，使用Messenger类，使用AIDL。

#### 客户端的配置 ####
客户端需要调用`bindService()`方法：

	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
    	return mBase.bindService(service, conn, flags);
	}

可以看到，bindService()方法需要三个参数，第一个是一个intent，我们都很熟悉——它和startService()里面那个intent是一样的，用来指定启动哪一个service以及传递一些数据过去。第二个参数是实现客户端与服务端通信的一个关键类。要想实现它，就必须重写两个回调方法：`onServiceConnected()`以及`onServiceDisconnected()`，而我们可以通过这两个回调方法得到服务端里面的IBinder对象，从而达到通信的目的。

bindService()方法的第三个参数是一个int值，它是一个指示绑定选项的标志，通常应该是 BIND_AUTO_CREATE，以便创建尚未激活的服务。其他可能的值为 BIND_DEBUG_UNBIND 和 BIND_NOT_FOREGROUND，或 0（表示无）。

#### 服务端的配置 ####
>如果要创建一个支持绑定的service，我们必须要重写它的onBind()方法。这个方法会返回一个IBinder对象，它是客户端用来和服务器进行交互的接口。而要得到IBinder接口，我们通常有三种方式：继承Binder类，使用Messenger类，使用AIDL。

可以看到，这里提出了一个IBinder接口的概念。那么这个IBinder接口是什么呢？它是一个在整个Android系统中都非常重要的东西，是为高性能而设计的轻量级远程调用机制的核心部分。当然，它不仅仅可以用于远程调用，也可以用于进程内调用——事实上，我们现在所说的service这里的IBinder既有可能出现远程调用的场景，比如用它来进行IPC，也有可能出现进程内调用的场景，比如用它来进行同进程内客户端与服务器的交互。

一般来讲，我们有三种方式可以获得IBinder的对象：继承Binder类，使用Messenger类，使用AIDL。接下来我将就这三种方式展开来讲。

#### 继承Binder类 ####
* 在service类中，创建一个满足以下任一要求的Binder实例
	* 包含客户端可调用的公共方法
	* 返回当前Service实例，其中包含客户端可调用的公共方法
	* 返回由当前service承载的其他类的实例，其中包含客户端可调用的公共方法

* 在onBind()方法中返回这个Binder实例
* 在客户端中通过onServiceDisconnected()方法接收传过去的Binder实例，并通过它提供的方法进行后续操作

可以看到，在使用这种方法进行客户端与服务端之间的交互是需要有一个强制类型转换的——在onServiceDisconnected()中获得一个经过转换的IBinder对象，我们必须将其转换为service类中的Binder实例的类型才能正确的调用其方法。而这强制类型转换其实就隐含了一个**使用这种方法的条件：客户端和服务端应当在同一个进程中**！不然在类型转换的时候也许会出现问题——在另一个进程中一定有这个Binder实例么？没有的话就不能完成强制类型转换。

线程内通信基本上就是这样了，没什么复杂的地方。接下来我们看看这种方式启动的service如何进行IPC。

#### 使用Messenger ####
> 以前讲到跨进程通信，我们总是第一时间想到AIDL(Android接口定义语言)，实际上，使用Messenger在很多情况下是比使用AIDL简单得多的，具体是为什么下文会有比较。

> 大家看到Messenger可能会很轻易的联想到Message，然后很自然的进一步联想到Handler——没错，Messenger的核心其实就是Message以及Handler来进行线程间的通信。下面讲一下通过这种方式实现IPC的步骤：

* 服务端实现一个Handler，由其接受来自客户端的每个调用的回调
* 使用实现的Handler创建Messenger对象
* 通过Messenger得到一个IBinder对象，并将其通过onBind()返回给客户端
* 客户端使用 `IBinder` 将 `Messenger`（引用服务的 Handler）实例化，然后使用后者将 Message 对象发送给服务
* 服务端在其 Handler 中（具体地讲，是在 handleMessage() 方法中）接收每个 Message
用这种方式，客户端并没有像扩展Binder类那样直接调用服务端的方法，而是采用了用Message来传递信息的方式达到交互的目的。

服务端主要是返给客户端一个IBinder实例，以供服务端构造Messenger，并且处理客户端发送过来的Message。当然，不要忘了要在Manifests文件里面注册：

	<service  android:name=".xxx" android:enabled="true" android:exported="true">
    <intent-filter>
        <action android:name="com.xxx.messenger"></action>
        <category android:name="android.intent.category.DEFAULT"/>
    </intent-filter>
	</service>

可以看到，这里注册的就和我们原先注册的有一些区别了，主要是因为我们在这里要跨进程通信，所以在另外一个进程里面并没有我们的service的实例，此时必须要给其他的进程一个标志，这样才能让其他的进程找到我们的service。

> 客户端就主要是发起与服务端的绑定，以及通过onServiceConnected()方法来过去服务端返回来的IBinder，借此构造Messenger，从而可以通过发送Message的方式与服务端进行交互。客户端里也可以创建一个Handler实例，让它接收来自服务端的信息，同时让服务端在客户端给它发的请求完成了之后再给客户端发送一条信息即可。

用Messenger来进行IPC的话整体的流程是非常清晰的，Message在其中起到了一个信使的作用，通过它客户端与服务端的信息得以互通。

#### 通过AIDL ####
> AIDL，即Android Interface Definition Language，Android接口定义语言。它是一种IDL语言，可以拿来生成用于IPC的代码。在我看来，它其实就是一个模板。为什么这样说呢？在我们的使用中，实际上起作用的并不是我们写的AIDL代码，而是系统根据它生成的一个IInterface实例的代码。而如果大家多生成几个这样的实例，然后把它们拿来比较，你会发现它们都是有套路的——都是一样的流程，一样的结构，只是根据具体的AIDL文件的不同有细微的变动。所以其实AIDL就是为了避免我们一遍遍的写一些千篇一律的代码而出现的一个模板。

使用AIDL来通过bindService()进行线程间通信基本有以下步骤：

* 服务端创建一个AIDL文件，将暴露给客户端的接口在里面声明
* 在service中实现这些接口
* 客户端绑定服务端，并将onServiceConnected()得到的IBinder转为AIDL生成的IInterface实例
* 通过得到的实例调用其暴露的方法

### AIDL ###
设计这门语言的目的是为了实现进程间通信。<br>
其实AIDL这门语言非常的简单，基本上它的语法和 Java 是一样的，只是在一些细微处有些许差别:

* 文件类型：用AIDL书写的文件的后缀是 .aidl，而不是 .java。
* 数据类型：AIDL默认支持一些数据类型，在使用这些数据类型的时候是不需要导包的，但是除了这些类型之外的数据类型，在使用之前必须导包， 就算目标文件与当前正在编写的 .aidl 文件在同一个包下 ——在 Java 中，这种情况是不需要导包的。比如，现在我们编写了两个文件，一个叫做 Book.java ，另一个叫做 BookManager.aidl ，它们都在 com.lypeer.aidldemo 包下 ，现在我们需要在 .aidl 文件里使用 Book 对象，那么我们就必须在 .aidl 文件里面写上 import com.lypeer.aidldemo.Book; 哪怕 .java 文件和 .aidl 文件就在一个包下。</br>默认支持的数据类型包括：
	* Java中的八种基本数据类型，包括 byte，short，int，long，float，double，boolean，char。
	* String 类型。
	* CharSequence类型。
	* List类型：List中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。List可以使用泛型。
	* Map类型：Map中的所有元素必须是AIDL支持的类型之一，或者是一个其他AIDL生成的接口，或者是定义的parcelable。Map是不支持泛型的。

* 定向tag：AIDL中的定向 tag 表示了在跨进程通信中数据的流向，其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，而 inout 则表示数据可在服务端与客户端之间双向流通。其中，数据流向是针对在客户端中的那个传入方法的对象而言的。in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，并且客户端将会同步服务端对该对象的任何变动。<br>**另外，Java 中的基本类型和 String ，CharSequence 的定向 tag 默认且只能是 in 。还有，请注意， 请不要滥用定向 tag ，而是要根据需要选取合适的——要是不管三七二十一，全都一上来就用 inout ，等工程大了系统的开销就会大很多——因为排列整理参数的开销是很昂贵的。**

* 两种AIDL文件：所有的AIDL文件大致可以分为两类，一类是用来定义parcelable对象，以供其他AIDL文件使用AIDL中非默认支持的数据类型的。一类是用来定义方法接口，以供系统使用来完成跨进程通信的。可以看到，两类文件都是在“定义”些什么，而不涉及具体的实现，这就是为什么它叫做“Android接口定义语言”。

注意： 这里有一个坑！ 大家可能注意到了，在 Book.aidl 文件中，我一直在强调： Book.aidl与Book.java的包名应当是一样的。 这似乎理所当然的意味着这两个文件应当是在同一个包里面的——事实上，很多比较老的文章里就是这样说的，他们说最好都在 aidl 包里同一个包下，方便移植——然而在 Android Studio 里并不是这样。如果这样做的话，系统根本就找不到 Book.java 文件，从而在其他的AIDL文件里面使用 Book 对象的时候会报 Symbol not found 的错误。为什么会这样呢？因为 Gradle 。大家都知道，Android Studio 是默认使用 Gradle 来构建 Android 项目的，而 Gradle 在构建项目的时候会通过 sourceSets 来配置不同文件的访问路径，从而加快查找速度——问题就出在这里。Gradle 默认是将 java 代码的访问路径设置在 java 包下的，这样一来，如果 java 文件是放在 aidl 包下的话那么理所当然系统是找不到这个 java 文件的

http://www.open-open.com/lib/view/open1469493830770.html
http://www.open-open.com/lib/view/open1469493649028.html