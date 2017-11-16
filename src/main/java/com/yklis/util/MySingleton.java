package com.yklis.util;

import java.security.KeyPair;

//饿汉式单例类.在类初始化时，已经自行实例化
public class MySingleton {
	
    //构造方法限定为private,避免类在外部被实例化
	private MySingleton() {}  
	
    //与下面注释代码的效果一样
	private static final MySingleton mySingleton = new MySingleton();
	/*private static final MySingleton mySingleton;
    //静态代码块：当类被载入时，静态代码块被执行，且只被执行一次，静态块常用来执行类属性的初始化
	static{
	    mySingleton = new MySingleton();
	}*/
	
	private KeyPair keyPair;//单例类的成员变量
	
    //静态工厂方法   
    public static MySingleton getInstance() {  
        return mySingleton;  
    }
    
	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}


}
