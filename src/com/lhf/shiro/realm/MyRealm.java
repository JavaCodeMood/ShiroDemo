package com.lhf.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class MyRealm extends AuthorizingRealm {

	/**
	 * 授权方法:
	 * 1. 实际返回的是 SimpleAuthorizationInfo 类的实例
	 * 2. 可以调用 SimpleAuthorizationInfo 的 addRole 来添加当前登录 user 的权限信息. 
	 * 3. 可以调用 PrincipalCollection 参数的 getPrimaryPrincipal() 方法来获取用户信息 
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		
		Object principal = principalCollection.getPrimaryPrincipal();
		
		if("admin".equals(principal)){
			info.addRole("admin");
		}
		if("user".equals(principal)){
			info.addRole("list");
		}
		
		info.addRole("user");
		
		return info;
	}

	/**
	 * 认证方法
	 * 1. 编写表单: 表单的 action、和 username、password 的参数都是什么 ? 
	 * 回答: 提交到你想提交的地方, username 和 password 也参数名称都任意. 
	 * 2. 例如, 提交到了一个 SpringMVC 的 handler: 
	 * 1). 获取用户名、密码
	 * 2). 
	 * Subject currentUser = SecurityUtils.getSubject();
	 * UsernamePasswordToken token = new UsernamePasswordToken(username, password);
	 * currentUser.login(token);
	 * 3. 当 Subject 调用 login 方法时, 即会触发当前的 doGetAuthenticationInfo 方法. 且把 
	 * UsernamePasswordToken 对象传入, 然后再该方法中执行真正的认证: 访问数据库进行比对. 
	 * 1). 获取用户名和密码
	 * 2). 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		System.out.println(token.getPrincipal());
		System.out.println(token.getCredentials());
		
		//1. 从 token 中获取登录的 username! 注意不需要获取 password.
		
		//2. 利用 username 查询数据库得到用户的信息. 
		
		//3. 创建 SimpleAuthenticationInfo 对象并返回. 注意该对象的凭证式从数据库中查询得到的. 
		//而不是页面输入的. 实际的密码校验可以交由 Shiro 来完成
		
		//4. 关于密码加密的事: shiro 的密码加密可以非常非常的复杂, 但实现起来却可以非常简单.
		//1). 可以选择加密方式: 在当前的 realm 中编写一个 public 类型的不带参数的方法, 使用 @PostConstruct
		//注解进行修饰, 在其中来设置密码的匹配方式.
		//2). 设置盐值: 盐值一般是从数据库中查询得到的.  
		//3). 调用 new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName)
		//构造器返回 SimpleAuthenticationInfo 对象:  credentialsSalt 为 
		//ByteSource credentialsSalt = new Md5Hash(source);
		
		//登陆的主要信息: 可以是一个实体类的对象, 但该实体类的对象一定是根据 token 的 username 查询得到的. 
		Object principal = token.getPrincipal();
		//认证信息: 从数据库中查询出来的信息. 密码的比对交给 shiro 去进行比较
		String credentials = "afdbaa3ee63a8b4e97196dcfd24b03fc";
		//设置盐值: 
		String source = "abcdefg";
		ByteSource credentialsSalt = new Md5Hash(source);
		System.out.println(credentialsSalt); 
		
		//当前 Realm 的 name
		String realmName = getName();
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		return info;
	}

	//@PostConstruct: 相当于 bean 节点的 init-method 配置. 
	public void setCredentialMatcher(){
		HashedCredentialsMatcher  credentialsMatcher = new HashedCredentialsMatcher();
		//设置加密的方式
		credentialsMatcher.setHashAlgorithmName("MD5");
		//设置加密的次数
		credentialsMatcher.setHashIterations(1024);
		
		setCredentialsMatcher(credentialsMatcher);
	}
	
	public static void main(String[] args) {
		String saltSource = "abcdefg";
		
		String hashAlgorithmName = "MD5";
		String credentials = "admin";
		Object salt = new Md5Hash(saltSource);
		int hashIterations = 1024;
				
		Object result = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
		System.out.println(result);
		System.out.println("68f3139a38b232392cc9d3b6ddd762f7".equals(result.toString()));
	}

}
