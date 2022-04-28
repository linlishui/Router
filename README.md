
### 使用步骤

0. 在所需路由模块的 `build.gradle` 中添加依赖：
    ```
    implementation 'io.github.linlishui:router:0.0.2'
    ```

1. 注册路由，在需要跳转的目的模块注册路由，如下：
    ```kotlin
    class WanRouterMeta : RouterMeta {

        // RouterItem类中，第一个参数是路径名称，第二个参数是跳转的类，第三个参数是跳转类的类型
        override fun buildTable(): RouterTable = RouterTable.Builder()
            .addItem(RouterItem("/wan/android", WanAndroidActivity::class.java, RouteType.ACTIVITY))
            .build()
    }
    ```

2. 在任意一个模块中创建如下关联目录：`src/main/resources/META-INF/services`

3. 接着在创建的 `services` 目录里，创建名称为 `lishui.lib.router.table.RouterMeta` 的文件

4. 添加实现RouterMeta接口的全路径类名
    ```
    # 放入实现了`lishui.lib.router.table.RouterMeta`接口的全路径类

    lishui.demo.wanandroid.WanRouterMeta
    ```

5. 尽可能早地进行初始化操作，如下代码：
    ```java
    // 在子线程执行初始化
    Router.init(context)
    ```

6. 路由跳转例子：
    ```java
    Router.getInstance().build("/wan/android").navigation()
    ```