package apm.db;

import apm.globalinfo.Save;
import apm.mode.ClassInfo;

class Filter {
	
	static Save filter(Object object){
		int threshold = 1024*512;//512K
		 if(object instanceof ClassInfo)
		 {	 
			 ClassInfo classInfo = (ClassInfo) object;
		     int size = classInfo.getBytes();
		     return size>(threshold)?Save.NOTSAVE:Save.SAVE;//保存大于512K的类的信息
		 }
		 return Save.SAVE;
	}
}
