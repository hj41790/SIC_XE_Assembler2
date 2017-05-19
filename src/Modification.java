
public class Modification {

	private int 	section;
	private int 	location;
	private int 	length;
	private int 	operation;
	private String 	symbol;
	private Token 	token;
	
	public Modification(int _section, int _length, int _operation, String _symbol, Token _token){
		
		section = _section;
		length = _length;
		operation = _operation;
		symbol = _symbol;
		token = _token;
		
		location = 0;
	}
	
	public void setLocation(int _location){
		location = _location;
	}
	
	public String toString(){
		
		String result = "";
		
		result += String.format("%06X", location);
		result += String.format("%02X", length);
		
		if(operation==1) result += "+";
		else result += "-";
		
		result += symbol;
		
		return result;
	}
	
	public boolean checkToken(Token t){
		return (t == token);
	}
	
	public boolean checkSection(int s){
		return (s == section);
	}
	
}
