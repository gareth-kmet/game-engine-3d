package toolbox;

public class StringUtils {
	
	public static String enumToString(Enum<?> e) {
		return enumToString(e.toString());
	}
	public static String enumToString(String enumName) {
		char[] chars = enumName.toCharArray();
		boolean makeUpper=false;
		
		String s="";
		
		for(char c : chars) {
			
			if(c=='$') {
				makeUpper=!makeUpper;
				continue;
			}else if(c=='_') {
				if(!makeUpper) {
					s+="_";
				}else {
					s+=" ";
					makeUpper=false;
				}
				continue;
			}
			
			String cs = c+"";
			if(makeUpper) {
				s+=cs.toUpperCase();
				makeUpper=false;
			}else {
				s+=cs.toLowerCase();
			}
		}
		return s;
		
	}

}
