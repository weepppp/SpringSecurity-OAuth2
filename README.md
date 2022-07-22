# SpringSecurity-OAuth2

❔ 为什么会产生网站第三方登录
1. 简化注册环节，减少可能因为注册繁琐带来的用户损失；
2. 简化用户设置个人信息过程，通过第三方登录，直接获取用户头像昵称等基本个人信息，无需用户自行设置；
3. 共享账号已有的用户关系，用户进入产品中就能找到熟悉的人，容易留住用户，发现同样使用该应用也用同样第三方方式登录的好友，会有惊喜感；
4. 节省用户的记忆成本，用户在使用多个应用时，只需使用第三方登录即可，无需记得每个平台的账户和密码；
5. 用户可以把平台上的某些内容一键分享到第三方平台。

❔ OAuth2.0是什么？
OAuth 是一个开放标准，该标准允许用户让第三方应用访问该用户在某一网站上存储的私密资源（如头像、照片、视频等），而在这个过程中无需将用户名和密码提供给第三方应用。实现这一功能是通过提供一个令牌（token），而不是用户名和密码来访问他们存放在特定服务提供者的数据。采用令牌（token）的方式可以让用户灵活的对第三方应用授权或者收回权限

❔ 另一个一个入门问题：第三方登录为什么要用OAuth2.0的token，不用普通密码或者传统session？原因有三点：
1. 令牌是短期的，到期会自动失效，用户自己无法修改。密码一般长期有效，用户不修改，就不会发生变化。
2. 令牌可以被数据所有者撤销，会立即失效。密码一般不允许被他人撤销。
3. 令牌有权限范围（scope）。对于网络服务来说，只读令牌就比读写令牌更安全。密码一般是完整权限。
所以OAuth2.0的token比普通密码更加灵活可控，并且传统的 Web 开发登录认证一般都是基于 session 的，但是在前后端分离的架构中继续使用 session 就会有许多不便，因为移动端（Android、iOS、微信小程序等）要么不支持 cookie（微信小程序），要么使用非常不便，对于这些问题，使用 OAuth2 认证都能解决。

❔  好了，现在我们知道OAuth2.0的好处了，那我们该怎样去实现这种授权模式呢？
首先要明确OAuth2 协议一共支持 4 种不同的授权模式：
1. 授权码模式：常见的第三方平台登录功能基本都是使用这种模式。
2. 简化模式（2.1已禁用）：简化模式是不需要客户端服务器参与，直接在浏览器中向授权服务器申请令牌（token）， 授权服务直接重定向回去 , 在浏览器//域名/回调url#access_token，一般如果网站是纯静态页面则可以采用这种方式。
3. 密码模式（2.1已禁用）：密码模式是用户把用户名密码直接告诉客户端，客户端使用说这些信息向授权服务器申请令牌（token）。这需要用户对客户端高度信任，例如客户端应用和服务提供商就是同一家公司，我们自己做前后端分离登录就可以采用这种模式。
4. 客户端模式：客户端模式是指客户端使用自己的名义而不是用户的名义向服务提供者申请授权，严格来说，客户端模式并不能算作 OAuth 协议要解决的问题的一种解决方案，但是，对于开发者而言，在一些前后端分离应用或者为移动端提供的认证授权服务器上使用这种模式还是非常方便的。
下面，我们就先介绍下授权码模式，并做一个简单的demo：
1. 首先，我会在我的网站的一个网页上放一个超链接（我的网站相当于是第三方应用），用户 A （服务方的用户，例如微信用户）点击这个超链接就会去请求授权服务器（微信的授权服务器），用户点击的过程其实也就是我跟用户要授权的过程
2. 接下来的第三步，就是用户点击了超链接之后，像授权服务器发送请求，一般来说，我放在我的网站的这个网页上的超链接可能有如下参数：
https://wx.qq.com/oauth/authorize?response_type=code&client_id=javaboy&redirect_uri=www.javaboy.org&scope=all
这里边有好几个参数，在后面的代码中我们都会用到，这里先和大家简单解释一下：
● response_type 表示授权类型，使用授权码模式的时候这里固定为 code，表示要求返回授权码（将来拿着这个授权码去获取 access_token）。
● client_id 表示客户端 id，也就是我应用的 id。有的小伙伴对这个不好理解，我说一下，如果我想让我的网站接入微信登录功能，我肯定得去微信开放平台注册，去填入我自己应用的基本信息等等，弄完之后，微信会给我一个 APPID，也就是我这里的 client_id，所以，从这里可以看出，授权服务器在校验的时候，会做两件事：1.校验客户端的身份；2.校验用户身份。
● redirect_uri 表示用户登录在成功/失败后，跳转的地址（成功登录微信后，跳转到我的网站中的哪个页面），跳转的时候，还会携带上一个授权码参数。
● scope 表示授权范围，即 我的网站拿着用户的 token 都能干啥（一般来说就是获取用户非敏感的基本信息）。
3. 接下来第四步，拿着第三步获取到的 code 以及自己的 client_id 和 client_secret 以及其他一些信息去授权服务器请求令牌，微信的授权服务器在校验过这些数据之后，就会发送一个令牌回来。这个过程一般是在后端完成的，而不是利用 js 去完成。
4. 接下来拿着这个 token，我们就可以去请求用户信息了。
一般情况下我们认为授权码模式是四种模式中最安全的一种模式，因为这种模式我们的 access_token 不用经过浏览器或者移动端 App，是直接从我们的后台发送到授权服务器上，这样就很大程度减少了 access_token 泄漏的风险。

❔ 一个完整的授权码模式demo
包含板块列项：
|项目|端口|备注|
|:-|:-:|-:|
|auth-server|8080|授权服务器|
|user-server|8081|资源服务器|
|client-app|8082|第三方应用|

![image](https://user-images.githubusercontent.com/100891076/180454889-58efd4aa-fe03-4475-b8f8-e1de3a496937.png)




