import java.util.ArrayList;
import java.util.regex.Pattern;

public class SymbolTable {

	// singleton class
	
	private static SymbolTable table = null;
	
	private ArrayList<Symbol> array = null;
	
	
	public static SymbolTable getInstance(){
		
		if(table == null){
			table = new SymbolTable();
		}
		
		return table;
	}
		
	private SymbolTable(){
		array = new ArrayList<Symbol>();
	}
	
	public Symbol addSymbol(String name, Token t) throws CustomException{
		
		if(name == null || t == null) return null;
		
		if(!isValidSymbol(name))
			throw new CustomException(CustomException.SYMBOL_WRONG_NAME);
		
		Symbol sym = new Symbol(name, t.getSection(), t.getAddr());
		array.add(sym);
		
		return sym;
	}
	
	public Symbol search_allSymbol(String n) throws CustomException{

		if(n == null) throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
		
		String name;
		if(n.charAt(0)=='@' || n.charAt(0)=='#') name = n.substring(1);
		else name = n.substring(0);

		for(Symbol s : array){
			if(s.compareName(name)) return s;
		}
		
		return null;
	}
	
	public Symbol search_localSymbol(String n, int section) throws CustomException{
		
		if(n == null) throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
		
		String name;
		if(n.charAt(0)=='@' || n.charAt(0)=='#') name = n.substring(1);
		else name = n.substring(0);
		
		for(Symbol s : array){
			if(s.compareName(name) && s.getSection()==section)
				return s;
		}
		
		return null;
	}
	
	public Symbol search_globalSymbol(String n) throws CustomException{
		
		if(n == null) throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
		
		String name;
		if(n.charAt(0)=='@' || n.charAt(0)=='#') name = n.substring(1);
		else name = n.substring(0);
		
		for(Symbol s : array){
			if(s.compareName(name) && s.isEXTDEF())
				return s;
		}
		
		return null;
	}
	
	public void print(){
		for(Symbol s : array){
			s.print();
		}
	}
		
	private boolean isValidSymbol(String n){
		String pattern = "^[A-Z]{1}([0-9]|[A-Z])+$";
		return Pattern.matches(pattern, n);
	}
}


