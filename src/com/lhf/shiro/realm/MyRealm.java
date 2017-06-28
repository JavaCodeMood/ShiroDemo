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
	 * ��Ȩ����:
	 * 1. ʵ�ʷ��ص��� SimpleAuthorizationInfo ���ʵ��
	 * 2. ���Ե��� SimpleAuthorizationInfo �� addRole ����ӵ�ǰ��¼ user ��Ȩ����Ϣ. 
	 * 3. ���Ե��� PrincipalCollection ������ getPrimaryPrincipal() ��������ȡ�û���Ϣ 
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
	 * ��֤����
	 * 1. ��д��: ���� action���� username��password �Ĳ�������ʲô ? 
	 * �ش�: �ύ�������ύ�ĵط�, username �� password Ҳ�������ƶ�����. 
	 * 2. ����, �ύ����һ�� SpringMVC �� handler: 
	 * 1). ��ȡ�û���������
	 * 2). 
	 * Subject currentUser = SecurityUtils.getSubject();
	 * UsernamePasswordToken token = new UsernamePasswordToken(username, password);
	 * currentUser.login(token);
	 * 3. �� Subject ���� login ����ʱ, ���ᴥ����ǰ�� doGetAuthenticationInfo ����. �Ұ� 
	 * UsernamePasswordToken ������, Ȼ���ٸ÷�����ִ����������֤: �������ݿ���бȶ�. 
	 * 1). ��ȡ�û���������
	 * 2). 
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		System.out.println(token.getPrincipal());
		System.out.println(token.getCredentials());
		
		//1. �� token �л�ȡ��¼�� username! ע�ⲻ��Ҫ��ȡ password.
		
		//2. ���� username ��ѯ���ݿ�õ��û�����Ϣ. 
		
		//3. ���� SimpleAuthenticationInfo ���󲢷���. ע��ö����ƾ֤ʽ�����ݿ��в�ѯ�õ���. 
		//������ҳ�������. ʵ�ʵ�����У����Խ��� Shiro �����
		
		//4. ����������ܵ���: shiro ��������ܿ��Էǳ��ǳ��ĸ���, ��ʵ������ȴ���Էǳ���.
		//1). ����ѡ����ܷ�ʽ: �ڵ�ǰ�� realm �б�дһ�� public ���͵Ĳ��������ķ���, ʹ�� @PostConstruct
		//ע���������, �����������������ƥ�䷽ʽ.
		//2). ������ֵ: ��ֵһ���Ǵ����ݿ��в�ѯ�õ���.  
		//3). ���� new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName)
		//���������� SimpleAuthenticationInfo ����:  credentialsSalt Ϊ 
		//ByteSource credentialsSalt = new Md5Hash(source);
		
		//��½����Ҫ��Ϣ: ������һ��ʵ����Ķ���, ����ʵ����Ķ���һ���Ǹ��� token �� username ��ѯ�õ���. 
		Object principal = token.getPrincipal();
		//��֤��Ϣ: �����ݿ��в�ѯ��������Ϣ. ����ıȶԽ��� shiro ȥ���бȽ�
		String credentials = "afdbaa3ee63a8b4e97196dcfd24b03fc";
		//������ֵ: 
		String source = "abcdefg";
		ByteSource credentialsSalt = new Md5Hash(source);
		System.out.println(credentialsSalt); 
		
		//��ǰ Realm �� name
		String realmName = getName();
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
		return info;
	}

	//@PostConstruct: �൱�� bean �ڵ�� init-method ����. 
	public void setCredentialMatcher(){
		HashedCredentialsMatcher  credentialsMatcher = new HashedCredentialsMatcher();
		//���ü��ܵķ�ʽ
		credentialsMatcher.setHashAlgorithmName("MD5");
		//���ü��ܵĴ���
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
