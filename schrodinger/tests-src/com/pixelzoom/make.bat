{\rtf1\ansi\ansicpg1252\deff0\deflang1033{\fonttbl{\f0\fswiss\fcharset0 Arial;}}
{\*\generator Msftedit 5.41.15.1507;}\viewkind4\uc1\pard\f0\fs20 javac *.java\par
jar cvf myjavabean.jar -C ../.. com/pixelzoom/MyJavaBean$1.class -C ../.. com/pixelzoom/MyJavaBean$2.class -C ../.. com/pixelzoom/MyJavaBean.class\par
\par
java -jar "C:\\Documents and Settings\\Sam Reid\\Desktop\\proguard3.5\\proguard3.5\\lib\\proguard.jar" @myjavabean-mac.pro\par
java -classpath myjavabean-mac-pro.jar com.pixelzoom.MyJavaBean\par
}
 